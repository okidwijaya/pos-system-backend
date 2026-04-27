package com.kitadevelopers.pos.modules.payment.service;

import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.order.service.OrderService;
import com.kitadevelopers.pos.modules.payment.entity.Payment;
import com.kitadevelopers.pos.modules.payment.enums.PaymentStatus;
import com.kitadevelopers.pos.modules.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePayments(){

        List<Payment> payments = paymentRepository.findByStatusAndCreatedAtBefore(
                PaymentStatus.PENDING, LocalDateTime.now().minusMinutes(30)
        );

        for(Payment p : payments){
                p.setStatus(PaymentStatus.EXPIRED);
                orderService.rollbackStock(p.getOrder());
//                Order order = p.getOrder();
//                order.setOrderStatus(OrderStatus.CANCELLED);
        }
        paymentRepository.saveAll(payments);
    }
}
