package com.kitadevelopers.pos.modules.order.dto;

import java.math.BigDecimal;

public record OrderResponseItem(
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
) {}
