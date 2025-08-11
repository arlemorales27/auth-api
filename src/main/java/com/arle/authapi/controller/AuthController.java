package com.arle.authapi.controller;

import com.arle.authapi.model.*;
import com.arle.authapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public Mono<String> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/reset-password-request")
    public Mono<String> requestPasswordReset(@RequestParam String email) {
        return userService.requestPasswordReset(email);
    }

    @PostMapping("/reset-password")
    public Mono<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }

    @GetMapping("/admin/test")
    public Mono<String> adminTest() {
        return Mono.just("Admin access granted");
    }
    @GetMapping("/user/test")
    public Mono<String> userTest() {
        return Mono.just("User or Client access granted");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<String> handleInvalidCredentials(RuntimeException ex) {
        return Mono.just("Credenciales inválidas");
    }
}
