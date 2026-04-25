package com.kitadevelopers.pos.modules.auth.entity;

import com.kitadevelopers.pos.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

       @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String token;

        @ManyToOne
        private User user;

        private LocalDateTime expiryDate;
}
