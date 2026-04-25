package com.kitadevelopers.pos.modules.auth.dto;

public record AuthResponse (
        String accessToken,
        String refreshToken
){}
