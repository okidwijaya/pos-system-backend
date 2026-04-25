package com.kitadevelopers.pos.modules.payment.dto;

import com.kitadevelopers.pos.modules.payment.enums.PaymentMethods;

import java.util.UUID;

public record ManualPaymentRequest(
        UUID orderId,
        String proofImage,
        String notes,
        PaymentMethods method
){}
