package com.kitadevelopers.pos.modules.payment.dto;

import com.kitadevelopers.pos.modules.payment.enums.PaymentMethods;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ManualPaymentRequest(
        @NotNull
        UUID orderId,
        String proofImage,
        String notes,
        @NotNull
        PaymentMethods method
){}
