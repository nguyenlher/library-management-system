package com.library.auth_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.library.auth_service.dto.TokenResponseDto;
import com.library.auth_service.dto.UserLoginDto;
import com.library.auth_service.dto.UserRegistrationDto;
import com.library.auth_service.entity.RefreshToken;
import com.library.auth_service.entity.Status;
import com.library.auth_service.entity.User;
import com.library.auth_service.repository.RefreshTokenRepository;
import com.library.auth_service.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService; // Assume JwtService exists for token generation

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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

        // Save refresh token to database
        LocalDateTime expiresAt = jwtService.getRefreshTokenExpirationTime();
        RefreshToken refreshTokenEntity = new RefreshToken(user.getId(), refreshToken, expiresAt);
        refreshTokenRepository.save(refreshTokenEntity);

        System.out.println("Login successful for user: " + loginDto.getEmail());
        return new TokenResponseDto(accessToken, refreshToken, user.getId(), user.getEmail());
    }

    public User register(UserRegistrationDto registrationDto) {
        System.out.println("Registration attempt for email: " + registrationDto.getEmail());
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());
        User user = new User(registrationDto.getEmail(), hashedPassword, registrationDto.getRole(), Status.ACTIVE);
        User savedUser = userRepository.save(user);
        System.out.println("Registration successful for user: " + savedUser.getEmail());
        return savedUser;
    }

    public TokenResponseDto refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not active");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Delete old refresh token and save new one
        refreshTokenRepository.delete(storedToken);
        LocalDateTime newExpiresAt = jwtService.getRefreshTokenExpirationTime();
        RefreshToken newRefreshTokenEntity = new RefreshToken(user.getId(), newRefreshToken, newExpiresAt);
        refreshTokenRepository.save(newRefreshTokenEntity);

        return new TokenResponseDto(newAccessToken, newRefreshToken, user.getId(), user.getEmail());
    }
}