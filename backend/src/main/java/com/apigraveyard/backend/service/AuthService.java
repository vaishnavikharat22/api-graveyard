package com.apigraveyard.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.apigraveyard.backend.dto.request.LoginRequest;
import com.apigraveyard.backend.dto.request.RegisterRequest;
import com.apigraveyard.backend.dto.response.AuthResponse;
import com.apigraveyard.backend.dto.response.MessageResponse;
import com.apigraveyard.backend.model.User;
import com.apigraveyard.backend.repository.UserRepository;
import com.apigraveyard.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .isVerified(false)
                .build();

        userRepository.save(user);

        return new MessageResponse("Registration successful!");
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(AuthResponse.UserInfo.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .build())
                .build();
    }
}