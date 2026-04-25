package com.kitadevelopers.pos.modules.auth.dto;

public record LogoutRequest (
    String refreshToken
){}
