package com.kitadevelopers.pos.modules.auth.service;

import com.kitadevelopers.pos.common.exception.BusinessException;
import com.kitadevelopers.pos.common.exception.ErrorCode;
import com.kitadevelopers.pos.common.service.EmailService;
import com.kitadevelopers.pos.modules.auth.dto.AuthResponse;
import com.kitadevelopers.pos.modules.auth.dto.LoginRequest;
import com.kitadevelopers.pos.modules.auth.dto.RegisterRequest;
import com.kitadevelopers.pos.modules.auth.entity.PasswordResetToken;
import com.kitadevelopers.pos.modules.auth.entity.RefreshToken;
import com.kitadevelopers.pos.modules.auth.repository.PasswordResetTokenRepository;
import com.kitadevelopers.pos.modules.auth.repository.RefreshTokenRepository;
import com.kitadevelopers.pos.modules.user.entity.User;
import com.kitadevelopers.pos.modules.user.enums.Role;
import com.kitadevelopers.pos.modules.user.repository.UserRepository;
import com.kitadevelopers.pos.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend.reset-password-url:http://localhost:3000/reset-password}")
    private String resetPasswordUrl;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.findByEmail(request.email()).isPresent()){
            throw BusinessException.conflict(ErrorCode.DUPLICATE_RESOURCE, "Email is already registered");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.CASHIER)
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> BusinessException.unauthorized(
                        ErrorCode.INVALID_CREDENTIALS,
                        "Invalid email or password"
                ));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw BusinessException.unauthorized(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid email or password"
            );
        }

        if(Boolean.FALSE.equals(user.getIsActive()) || Boolean.TRUE.equals(user.getIsDeleted())){
            throw BusinessException.unauthorized(
                    ErrorCode.UNAUTHORIZED,
                    "Account is inactive"
            );
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        saveRefreshToken(user, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public void saveRefreshToken(User user, String token){
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public AuthResponse refresh(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> BusinessException.unauthorized(
                        ErrorCode.INVALID_TOKEN,
                        "Invalid refresh token"
                ));

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw BusinessException.unauthorized(
                    ErrorCode.TOKEN_EXPIRED,
                    "Refresh token expired"
            );
        }

        User user = refreshToken.getUser();

        if(Boolean.FALSE.equals(user.getIsActive()) || Boolean.TRUE.equals(user.getIsDeleted())){
            refreshTokenRepository.delete(refreshToken);
            throw BusinessException.unauthorized(ErrorCode.UNAUTHORIZED, "Account is inactive");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenRepository.delete(refreshToken);
        saveRefreshToken(user, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken){
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    public String hashToken(String token){
        return DigestUtils.sha256Hex(token);
    }

    public void forgotPassword(String email){
        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null) return;

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .hashToken(tokenHash)
                .expiredDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = resetPasswordUrl + "?token=" + rawToken;

       emailService.send(user.getEmail(), resetLink);
    }

    public void resetPassword(String rawToken, String newPassword){
        String hashed = hashToken(rawToken);

        PasswordResetToken validToken = passwordResetTokenRepository
                .findByHashToken(hashed)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (validToken.isUsed() || validToken.getExpiredDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token Expired or Used");
        }

        User user = validToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        validToken.setUsed(true);
        passwordResetTokenRepository.save(validToken);
    }
}
