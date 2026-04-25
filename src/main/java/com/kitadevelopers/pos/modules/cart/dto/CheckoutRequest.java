package com.kitadevelopers.pos.modules.cart.dto;

public record CheckoutRequest (
        String customerName,
        String customerEmail,
        String customerPhone
){}
