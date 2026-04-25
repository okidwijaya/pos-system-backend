package com.kitadevelopers.pos.modules.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse (
        String paymentId,
        String orderId,
        String externalId,
        String paymentUrl,
        String status,
        BigDecimal amount,
        LocalDateTime paidAt
){}
