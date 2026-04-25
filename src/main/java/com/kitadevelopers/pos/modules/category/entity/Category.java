package com.kitadevelopers.pos.modules.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    private LocalDateTime createdAt;

    private String description;

    @PrePersist
    public void onCrate() { this.createdAt = LocalDateTime.now(); }
}
