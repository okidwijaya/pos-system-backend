package com.kitadevelopers.pos.modules.payment.entity;

import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.payment.enums.PaymentMethods;
import com.kitadevelopers.pos.modules.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // RELATION
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    // INTERNAL
    @Column(nullable = false, unique = true)
    private String externalId; // internal reference (PAY-xxx)

    @Column(unique = true)
    private String idempotencyKey;

    // GATEWAY DATA
    private String transactionId; // from Midtrans/Xendit

    @Enumerated(EnumType.STRING)
    private PaymentMethods method;
//    private String paymentGateway; // MIDTRANS / XENDIT
//    private String paymentChannel; // VA / QRIS / CARD

    // URL
    private String paymentUrl;
    private String snapUrl;

    // STATUS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // MONEY
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 10)
    private String currency;

    // TIME
    private LocalDateTime expiredAt;
    private LocalDateTime paidAt;

    // ERROR HANDLING
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currency == null) this.currency = "IDR";
        if (this.status == null) this.status = PaymentStatus.PENDING;
    }

    private String proofImage;
    private String notes;
}