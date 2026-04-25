package com.kitadevelopers.pos.modules.auth.entity;

import com.kitadevelopers.pos.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hashToken;

    @ManyToOne
    private User user;

    private LocalDateTime expiredDate;

    private boolean used;
}

