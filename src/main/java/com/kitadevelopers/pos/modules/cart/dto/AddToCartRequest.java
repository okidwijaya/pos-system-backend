package com.kitadevelopers.pos.modules.cart.dto;

import java.util.UUID;

public record AddToCartRequest(
        UUID productId,
        Integer quantity
) {}
