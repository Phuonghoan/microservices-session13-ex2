package org.example.identityservice.controller;

import org.example.identityservice.entity.User;
import org.example.identityservice.repository.UserRepository;
import org.example.identityservice.service.AuthService;
import org.example.identityservice.util.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(
            AuthService authService,
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ==================================
    // BÀI 1: ĐĂNG KÝ TÀI KHOẢN
    // ==================================

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {

        User user = authService.register(
                request.username(),
                request.password()
        );

        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==================================
    // BÀI 2: TẠO JWT
    // ==================================

    @GetMapping(
            value = "/test-token",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String testToken(
            @RequestParam String username
    ) {

        // Tìm User trong PostgreSQL
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy User: " + username
                        )
                );

        // Sinh JWT
        return jwtUtil.generateToken(user);
    }

    // DTO của Bài 1
    public record RegisterRequest(
            String username,
            String password
    ) {
    }

    public record RegisterResponse(
            String id,
            String username,
            String role
    ) {
    }
}