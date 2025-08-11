package com.arle.authapi.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @Email @NotBlank String email,
        @NotBlank String role
) {}
