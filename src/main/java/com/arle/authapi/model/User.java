package com.arle.authapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(
        @Id Long id,
        String username,
        String password,
        String email,
        String role,
        String resetToken,
        java.time.LocalDateTime resetTokenExpiry
) {}