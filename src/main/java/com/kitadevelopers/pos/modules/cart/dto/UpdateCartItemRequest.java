package com.kitadevelopers.pos.modules.cart.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCartItemRequest (
        @NotNull
        UUID itemId,

        @NotNull
        Integer quantity
){}
