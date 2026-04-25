package com.kitadevelopers.pos.modules.payment.dto;

import java.math.BigDecimal;

public record WebhookRequest(
        String order_id,
        String transaction_status,
        String status_code,
        String gross_amount,
        String signature_key
) {}