package com.kitadevelopers.pos.modules.auth.dto;

public record ResetPasswordRequest (
        String token,
        String newPassword
){}
