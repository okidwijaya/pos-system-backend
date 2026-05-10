package com.kitadevelopers.pos.modules.payment.service;

import com.kitadevelopers.pos.common.exception.BusinessException;
import com.kitadevelopers.pos.common.exception.ErrorCode;
import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.common.exception.ResourceNotFoundException;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.order.service.OrderService;
import com.kitadevelopers.pos.modules.payment.config.MidtransProperties;
import com.kitadevelopers.pos.modules.payment.dto.ManualPaymentRequest;
import com.kitadevelopers.pos.modules.payment.dto.PaymentResponse;
import com.kitadevelopers.pos.modules.payment.dto.RejectPaymentRequest;
import com.kitadevelopers.pos.modules.payment.dto.VerifyPaymentRequest;
import com.kitadevelopers.pos.modules.payment.entity.Payment;
//import com.kitadevelopers.pos.modules.payment.enums.PaymentMethods;
import com.kitadevelopers.pos.modules.payment.enums.PaymentStatus;
import com.kitadevelopers.pos.modules.payment.mapper.PaymentMapper;
import com.kitadevelopers.pos.modules.payment.repository.PaymentRepository;
import com.kitadevelopers.pos.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final MidtransProperties midtransProperties;

    @Transactional
    public PaymentResponse createPayment(UUID orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order Not Found", orderId));

        if(order.getOrderStatus() != OrderStatus.PENDING){
            throw BusinessException.badRequest(ErrorCode.INVALID_STATE, "Order already processed");
        }

        paymentRepository.findByOrderId(orderId)
                .ifPresent(p ->{
                    throw BusinessException.conflict(ErrorCode.DUPLICATE_RESOURCE, "Payment already exists");
                });

        String externalId = "PAY-" + UUID.randomUUID();
        GatewayPayment gatewayPayment = createGatewayPayment(order, externalId);

        Payment payment = Payment.builder()
                .order(order)
                .externalId(externalId)
                .paymentUrl(gatewayPayment.redirectUrl())
                .snapUrl(gatewayPayment.redirectUrl())
                .transactionId(gatewayPayment.token())
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .build();

        payment = paymentRepository.save(payment);

        return PaymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse manualPayment(ManualPaymentRequest request){
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> ResourceNotFoundException.of("Order Not Found", request.orderId()));

        if(order.getOrderStatus() !=  OrderStatus.PENDING){
            throw BusinessException.badRequest(ErrorCode.INVALID_STATE, "Order already processed");
        }

        if(paymentRepository.findByOrderId(order.getId()).isPresent()){
            throw BusinessException.conflict(ErrorCode.DUPLICATE_RESOURCE, "Payment already exists");
        }

        Payment payment = Payment.builder()
                .order(order)
                .externalId("PAY-MAN-" + UUID.randomUUID())
                .method(request.method())
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .proofImage(request.proofImage())
                .notes(request.notes())
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .build();

        payment = paymentRepository.save(payment);

        if(payment.getStatus() == PaymentStatus.PAID){
            order.setOrderStatus(OrderStatus.PAID);
            orderRepository.save(order);
        }
        return PaymentMapper.toResponse(payment);
    }

    @Transactional
    public void verifyManualPayment(UUID id, VerifyPaymentRequest request){

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Payment not found", id));

        if(payment.getStatus() != PaymentStatus.PENDING){
            throw BusinessException.badRequest(ErrorCode.INVALID_STATE, "Payment already processed");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setNotes(request.notes());
        payment.setVerifiedBy(SecurityUtil.getCurrentUserEmail());

        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.PAID);

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

    @Transactional
    public void rejectManualPayment(UUID id, RejectPaymentRequest request){

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Payment not found", id));

        if(payment.getStatus() != PaymentStatus.PENDING){
            throw BusinessException.badRequest(ErrorCode.INVALID_STATE, "Payment already processed");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(request.reason());
        payment.setRejectedBy(SecurityUtil.getCurrentUserEmail());

        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.CANCELLED);

        rollbackStockOnce(payment);

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

    public void rollbackStockOnce(Payment payment){
        if(payment.getStockRolledBackAt() != null){
            return;
        }
        orderService.rollbackStock(payment.getOrder());
        payment.setStockRolledBackAt(LocalDateTime.now());
    }

    private GatewayPayment createGatewayPayment(Order order, String externalId){
        if(midtransProperties == null
                || midtransProperties.getServerKey() == null
                || midtransProperties.getServerKey().isBlank()){
            return new GatewayPayment("TRX-" + UUID.randomUUID(), "https://mock-payment.com/" + externalId);
        }

        String auth = Base64.getEncoder().encodeToString(
                (midtransProperties.getServerKey() + ":").getBytes(StandardCharsets.UTF_8)
        );

        Map<String, Object> response = RestClient.create()
                .post()
                .uri(midtransProperties.getSnapUrl())
                .header("Authorization", "Basic " + auth)
                .body(Map.of(
                        "transaction_details", Map.of(
                                "order_id", externalId,
                                "gross_amount", order.getTotalAmount()
                        ),
                        "customer_details", Map.of(
                                "first_name", order.getCustomer() != null ? order.getCustomer().getName() : "Customer"
                        )
                ))
                .retrieve()
                .body(Map.class);

        if(response == null || response.get("redirect_url") == null || response.get("token") == null){
            throw BusinessException.badRequest(ErrorCode.INVALID_STATE, "Payment gateway response is invalid");
        }

        return new GatewayPayment(response.get("token").toString(), response.get("redirect_url").toString());
    }

    private record GatewayPayment(String token, String redirectUrl) {}
}
