package com.kitadevelopers.pos.modules.payment.repository;

import com.kitadevelopers.pos.modules.payment.entity.Payment;
import com.kitadevelopers.pos.modules.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByExternalId(String externalId);
    Optional<Payment> findByOrderId(UUID orderId);

    List<Payment> findByStatusAndCreatedAtBefore(
            PaymentStatus status,
            LocalDateTime time
    );
}
