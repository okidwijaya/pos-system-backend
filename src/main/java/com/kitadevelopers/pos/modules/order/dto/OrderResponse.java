package com.kitadevelopers.pos.modules.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse (
        String orderNumber,
        String cashierName,
        String customerName,
        BigDecimal total,
        List<OrderResponseItem> items
){}
