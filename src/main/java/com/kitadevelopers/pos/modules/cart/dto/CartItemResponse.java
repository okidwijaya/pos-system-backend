package com.kitadevelopers.pos.modules.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse (
        String ProductName,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
){}
