package com.kitadevelopers.pos.modules.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse (
        String id,
        List<CartItemResponse> items,
        BigDecimal total
){}
