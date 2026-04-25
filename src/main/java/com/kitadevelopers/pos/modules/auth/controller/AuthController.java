package com.kitadevelopers.pos.modules.auth.controller;

import com.kitadevelopers.pos.common.response.ApiResponse;
import com.kitadevelopers.pos.modules.auth.dto.*;
import com.kitadevelopers.pos.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request){
        return ApiResponse.success(service.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request){
        return ApiResponse.success(service.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestBody RefreshRequest request){
        return ApiResponse.success(service.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestBody LogoutRequest request){
        service.logout(request.refreshToken());
        return ApiResponse.success("Logout Success");
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request){
        service.forgotPassword(request.email());
        return ApiResponse.success("DEV: If email exists, reset link sent");
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request){
        service.resetPassword(request.token(), request.newPassword());
        return ApiResponse.success("Password reset success");
    }
}
