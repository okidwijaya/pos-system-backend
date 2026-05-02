package com.kitadevelopers.pos.modules.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        @NotBlank
        String name,

        String description,

        @NotNull
        @Positive
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stock,

        UUID categoryId,

        @DecimalMin("0.00") @DecimalMax("1.00")
        BigDecimal taxRate
){
}
