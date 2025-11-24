package com.library.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.library.auth_service.dto.TokenResponseDto;
import com.library.auth_service.dto.UserLoginDto;
import com.library.auth_service.entity.Status;
import com.library.auth_service.entity.User;
import com.library.auth_service.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService; // Assume JwtService exists for token generation

    public TokenResponseDto login(UserLoginDto loginDto) {
        System.out.println("Login attempt for email: " + loginDto.getEmail());
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> {
                    System.out.println("User not found: " + loginDto.getEmail());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
                });

        if (user.getStatus() != Status.ACTIVE) {
            System.out.println("User not active: " + loginDto.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not active");
        }

        boolean passwordMatches = passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash());
        System.out.println("Password matches: " + passwordMatches + " for user: " + loginDto.getEmail());
        if (!passwordMatches) {
            System.out.println("Invalid password for user: " + loginDto.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        System.out.println("Login successful for user: " + loginDto.getEmail());
        return new TokenResponseDto(accessToken, refreshToken, user.getId(), user.getEmail());
    }
}