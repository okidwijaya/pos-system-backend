package com.kitadevelopers.pos.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        String email,
        String password
){}
