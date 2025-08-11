package com.arle.authapi.service;

import com.arle.authapi.config.JwtUtil;
import com.arle.authapi.model.LoginRequest;
import com.arle.authapi.model.RegisterRequest;
import com.arle.authapi.model.ResetPasswordRequest;
import com.arle.authapi.model.User;
import com.arle.authapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Mono<String> register(RegisterRequest request) {
        return userRepository.findByUsername(request.username())
                .flatMap(existing -> Mono.<String>error(new RuntimeException("Username already exists")))
                .switchIfEmpty(userRepository.findByEmail(request.email())
                        .flatMap(existing -> Mono.<String>error(new RuntimeException("Email already exists")))
                        .switchIfEmpty(Mono.defer(() -> {
                            User user = new User(null, request.username(), passwordEncoder.encode(request.password()),
                                    request.email(), request.role(), null, null);
                            return userRepository.save(user)
                                    .map(saved -> jwtUtil.generateToken(saved.username(), saved.role()));
                        })));
    }

    public Mono<String> login(LoginRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(user -> passwordEncoder.matches(request.password(), user.password()))
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .map(user -> jwtUtil.generateToken(user.username(), user.role()));
    }
    public Mono<String> requestPasswordReset(String email) {
        String token = UUID.randomUUID().toString();
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new RuntimeException("Email not found")))
                .flatMap(user -> userRepository.save(new User(user.id(), user.username(), user.password(), user.email(),
                        user.role(), token, LocalDateTime.now().plusHours(1))))
                .map(user -> token);
    }

    public Mono<String> resetPassword(ResetPasswordRequest request) {
        return userRepository.findByResetToken(request.token())
                .filter(user -> user.resetTokenExpiry() != null && user.resetTokenExpiry().isAfter(LocalDateTime.now()))
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid or expired token")))
                .flatMap(user -> userRepository.save(new User(user.id(), user.username(),
                        passwordEncoder.encode(request.newPassword()), user.email(), user.role(), null, null)))
                .map(user -> "Password reset successfully");
    }
}
