package com.kitadevelopers.pos.modules.auth.repository;

import com.kitadevelopers.pos.modules.auth.entity.RefreshToken;
import com.kitadevelopers.pos.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByUser(User user);
}
