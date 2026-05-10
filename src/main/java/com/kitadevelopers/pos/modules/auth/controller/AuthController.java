package com.kitadevelopers.pos.modules.auth.controller;

import com.kitadevelopers.pos.common.exception.BusinessException;
import com.kitadevelopers.pos.common.config.RateLimitConfig;
import com.kitadevelopers.pos.common.response.ApiResponse;
import com.kitadevelopers.pos.modules.auth.dto.*;
import com.kitadevelopers.pos.modules.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    private final RateLimitConfig rateLimit;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest){
        enforceRateLimit(servletRequest);
        return ApiResponse.success(service.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest){
        enforceRateLimit(servletRequest);
        return ApiResponse.success(service.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request){
        return ApiResponse.success(service.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@Valid @RequestBody LogoutRequest request){
        service.logout(request.refreshToken());
        return ApiResponse.success("Logout Success");
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest servletRequest){
        enforceRateLimit(servletRequest);
        service.forgotPassword(request.email());
        return ApiResponse.success("DEV: If email exists, reset link sent");
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        service.resetPassword(request.token(), request.newPassword());
        return ApiResponse.success("Password reset success");
    }

    private void enforceRateLimit(HttpServletRequest request){
        if(!rateLimit.tryConsume(clientIp(request))){
            throw BusinessException.tooManyRequests("Too many requests. Please try again later.");
        }
    }

    private String clientIp(HttpServletRequest request){
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if(forwardedFor != null && !forwardedFor.isBlank()){
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
