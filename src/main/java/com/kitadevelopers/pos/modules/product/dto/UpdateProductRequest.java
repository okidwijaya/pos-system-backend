package com.kitadevelopers.pos.modules.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank
        String name,

        String description,

        @NotNull
        @Positive
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stock
){
}
