package com.kitadevelopers.pos.modules.order.dto;

public record CheckoutRequest (
        String customerEmail,
        String customerName,
        String notes
){}
