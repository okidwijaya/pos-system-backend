package com.kitadevelopers.pos.modules.payment.service;

import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.order.service.OrderService;
import com.kitadevelopers.pos.modules.payment.dto.WebhookRequest;
import com.kitadevelopers.pos.modules.payment.entity.Payment;
import com.kitadevelopers.pos.modules.payment.enums.PaymentStatus;
import com.kitadevelopers.pos.modules.payment.repository.PaymentRepository;
import com.kitadevelopers.pos.modules.payment.security.SignatureVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebhookService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final SignatureVerifier verifier;
    private final PaymentService paymentService;
    private final OrderService orderService;

    @Transactional
    public void handle(WebhookRequest req){
        boolean valid = verifier.verify(
                req.order_id(),
                req.status_code(),
                req.gross_amount(),
                req.signature_key()
        );

        if(!valid){
            throw new RuntimeException("Invalid signature");
        }

        Payment payment = paymentRepository.findByExternalId(req.order_id())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if(payment.getStatus() != PaymentStatus.PENDING){
            return;
        }

        Order order = payment.getOrder();

        switch (req.transaction_status()){

            case "settlement", "capture" -> {
                payment.setStatus(PaymentStatus.PAID);
                payment.setPaidAt(LocalDateTime.now());

//                Order order = payment.getOrder();
                order.setOrderStatus(OrderStatus.PAID);

                orderRepository.save(order);
            }

            case "expire" -> {
                payment.setStatus(PaymentStatus.EXPIRED);
                payment.setFailureReason("Payment expired");

                order.setOrderStatus(OrderStatus.CANCELLED);
                orderService.rollbackStock(order);
            }

            case "cancel" -> {
                payment.setStatus(PaymentStatus.FAILED);
                orderService.rollbackStock(order);
            }
        }

            paymentRepository.save(payment);
            orderRepository.save(order);
        }
}

//    @Transactional
//    public void handleWebhook(WebhookRequest request){
//        Payment payment = paymentRepository.findByExternalId(request.externalId())
//                .orElseThrow(() -> new RuntimeException("Payment not found"));
//
//        if(!verifySignature(request)){
//            throw new RuntimeException("Invalid Signature");
//        }
//
//        if(payment.getStatus() == PaymentStatus.PAID){
//            return;
//        }
//
//        if("PAID".equalsIgnoreCase(request.status())){
//            payment.setStatus(PaymentStatus.PAID);
//            payment.setPaidAt(LocalDateTime.now());
//
//            Order order = payment.getOrder();
//            order.setOrderStatus(OrderStatus.PAID);
//
//            orderRepository.save(order);
//
//            paymentRepository.save(payment);
//        }
//    }
