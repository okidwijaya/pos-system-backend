package com.kitadevelopers.pos.modules.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest (
        @NotBlank(message = "Name is Required")
        String name,

        String description
){}
