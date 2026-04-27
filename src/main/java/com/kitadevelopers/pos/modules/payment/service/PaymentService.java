package com.kitadevelopers.pos.modules.payment.service;

import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.order.service.OrderService;
import com.kitadevelopers.pos.modules.payment.dto.ManualPaymentRequest;
import com.kitadevelopers.pos.modules.payment.dto.PaymentResponse;
import com.kitadevelopers.pos.modules.payment.dto.RejectPaymentRequest;
import com.kitadevelopers.pos.modules.payment.dto.VerifyPaymentRequest;
import com.kitadevelopers.pos.modules.payment.entity.Payment;
import com.kitadevelopers.pos.modules.payment.enums.PaymentMethods;
import com.kitadevelopers.pos.modules.payment.enums.PaymentStatus;
import com.kitadevelopers.pos.modules.payment.mapper.PaymentMapper;
import com.kitadevelopers.pos.modules.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Transactional
    public PaymentResponse createPayment(UUID orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order Not Found"));

        paymentRepository.findByOrderId(orderId)
                .ifPresent(p ->{
                    throw new RuntimeException("Payment already exist");
                });

        String externalId = "PAY-" + System.currentTimeMillis();
        String paymentUrl =  "https://mock-payment.com/" + externalId;
        String transactionId = "TRX-" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .order(order)
                .externalId(externalId)
                .paymentUrl(paymentUrl)
                .transactionId(transactionId)
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .build();

        paymentRepository.save(payment);

        return PaymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse manualPayment(ManualPaymentRequest request){
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order Not Found"));

        if(order.getOrderStatus() !=  OrderStatus.PENDING){
            throw new RuntimeException("Order Already processed");
        }

        if(paymentRepository.findByOrderId(order.getId()).isPresent()){
            throw new RuntimeException("Payment already exist");
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

        paymentRepository.save(payment);

        if(payment.getStatus() == PaymentStatus.PAID){
            order.setOrderStatus(OrderStatus.PAID);
            orderRepository.save(order);
        }
        return PaymentMapper.toResponse(payment);
    }

    @Transactional
    public void verifyManualPayment(UUID id, VerifyPaymentRequest request){

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if(payment.getStatus() != PaymentStatus.PENDING){
            throw new RuntimeException("Payment slready processed");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setNotes(request.notes());

        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.PAID);

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

    @Transactional
    public void rejectManualPayment(UUID id, RejectPaymentRequest request){

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if(payment.getStatus() != PaymentStatus.PENDING){
            throw new RuntimeException("Payment already processed");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(request.reason());

        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.CANCELLED);

        orderService.rollbackStock(order);

        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}
