package com.kitadevelopers.pos.modules.payment.service;

import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.order.service.OrderService;
//import com.kitadevelopers.pos.modules.payment.dto.ManualPaymentRequest;
import com.kitadevelopers.pos.modules.payment.dto.RejectPaymentRequest;
import com.kitadevelopers.pos.modules.payment.dto.VerifyPaymentRequest;
import com.kitadevelopers.pos.modules.payment.config.MidtransProperties;
import com.kitadevelopers.pos.modules.payment.entity.Payment;
import com.kitadevelopers.pos.modules.payment.enums.PaymentStatus;
import com.kitadevelopers.pos.modules.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderService orderService;
    @Mock private MidtransProperties midtransProperties;

    @InjectMocks private PaymentService paymentService;

    private UUID orderId;
    private Order order;

    @BeforeEach
    void setup() {
        orderId = UUID.randomUUID();
        order = Order.builder()
                .id(orderId)
                .orderNumber("ORD-TEST")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("50000"))
                .build();
    }

    @Test
    void createPayment_success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(paymentRepository.save(any())).thenAnswer(i -> {
            Payment payment = i.getArgument(0);
            payment.setId(UUID.randomUUID());
            return payment;
        });

        var result = paymentService.createPayment(orderId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PaymentStatus.PENDING.name());
        assertThat(result.amount()).isEqualByComparingTo("50000");
    }

    @Test
    void createPayment_throwsWhenPaymentAlreadyExists() {
        Payment existing = Payment.builder().id(UUID.randomUUID()).build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> paymentService.createPayment(orderId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exist");
    }

    @Test
    void verifyManualPayment_updatesOrderToPaid() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .order(order)
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        paymentService.verifyManualPayment(paymentId, new VerifyPaymentRequest("Verified by admin"));

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getPaidAt()).isNotNull();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void rejectManualPayment_rollsBackStock() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .order(order)
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        paymentService.rejectManualPayment(paymentId, new RejectPaymentRequest("Invalid proof"));

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderService, times(1)).rollbackStock(order);
    }

    @Test
    void verifyManualPayment_throwsWhenAlreadyProcessed() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .order(order)
                .status(PaymentStatus.PAID)
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() ->
                paymentService.verifyManualPayment(paymentId, new VerifyPaymentRequest("test"))
        ).isInstanceOf(RuntimeException.class);
    }
}
