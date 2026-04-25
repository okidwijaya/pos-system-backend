package com.kitadevelopers.pos.modules.payment.controller;

import com.kitadevelopers.pos.common.response.ApiResponse;
import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.payment.dto.ManualPaymentRequest;
import com.kitadevelopers.pos.modules.payment.dto.PaymentResponse;
import com.kitadevelopers.pos.modules.payment.dto.RejectPaymentRequest;
import com.kitadevelopers.pos.modules.payment.dto.VerifyPaymentRequest;
import com.kitadevelopers.pos.modules.payment.entity.Payment;
import com.kitadevelopers.pos.modules.payment.enums.PaymentStatus;
import com.kitadevelopers.pos.modules.payment.repository.PaymentRepository;
import com.kitadevelopers.pos.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @PreAuthorize("hasRole('CASHIER')")
    @PostMapping("/create/{orderId}")
    public ApiResponse<PaymentResponse> create(@PathVariable UUID orderId){
        return ApiResponse.success(service.createPayment(orderId));
    }

    @PostMapping("/mock/{externalId}/pay")
    public ResponseEntity<String> mockPay(@PathVariable String externalId){
        Payment payment = paymentRepository.findByExternalId(externalId)
                .orElseThrow();

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.PAID);

        paymentRepository.save(payment);
        orderRepository.save(order);
        return ResponseEntity.ok("Mock payment Paid successs");
    }

    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN')")
    @PostMapping("/manual-payment")
    public ApiResponse<PaymentResponse> manualPayment(@RequestBody ManualPaymentRequest request){
        return ApiResponse.success(service.manualPayment(request));
    }

    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN')")
    @PostMapping("/{paymentId}/verify")
    public ApiResponse<String> verify(
            @PathVariable("paymentId") UUID paymentId,
            @RequestBody VerifyPaymentRequest request
    ){
        service.verifyManualPayment(paymentId, request);
        return ApiResponse.success("Payment verified");
    }

    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN')")
    @PostMapping("/{paymentId}/reject")
    public ApiResponse<String> reject(
            @PathVariable("paymentId") UUID paymentId,
            @RequestBody RejectPaymentRequest request
    ){
        service.rejectManualPayment(paymentId, request);
        return ApiResponse.success("Payment rejected");
    }
}