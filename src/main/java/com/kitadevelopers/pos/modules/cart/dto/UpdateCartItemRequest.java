package com.kitadevelopers.pos.modules.cart.dto;

import java.util.UUID;

public record UpdateCartItemRequest (
        UUID itemId,
        Integer quantity
){}
