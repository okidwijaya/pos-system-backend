package com.kitadevelopers.pos.modules.auth.repository;

import com.kitadevelopers.pos.modules.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByHashToken(String hashToken);
}
