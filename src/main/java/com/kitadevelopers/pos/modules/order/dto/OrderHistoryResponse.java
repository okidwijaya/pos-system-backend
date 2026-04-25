package com.kitadevelopers.pos.modules.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderHistoryResponse (
        String orderId,
        String orderNumber,
        String cashierName,
        String customerName,
        BigDecimal total,
        String status,
        LocalDateTime createdAt
){}
