package com.kitadevelopers.pos.modules.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

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

        BigDecimal costPrice
){}
