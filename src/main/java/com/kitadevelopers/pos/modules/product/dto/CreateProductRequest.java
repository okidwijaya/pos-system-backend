package com.kitadevelopers.pos.modules.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record  CreateProductRequest (
        @NotBlank(message = "Name is Required")
        String name,

        String description,

        @NotNull
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull
        @Min(value = 0, message = "Stock cannot be positive")
        Integer stock,

        @NotNull
        @PositiveOrZero
        BigDecimal costPrice,

        UUID categoryId,
        String sku,
        String barcode,

        @DecimalMin("0.00") @DecimalMax("1.00")
        BigDecimal taxRate
){}
