package com.kitadevelopers.pos.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectPaymentRequest(
        @NotBlank
        String reason
) {}
