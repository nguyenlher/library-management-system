package com.library.auth_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.library.auth_service.dto.UserResponseDto;
import com.library.auth_service.dto.UserUpdateDto;
import com.library.auth_service.entity.Role;
import com.library.auth_service.entity.Status;
import com.library.auth_service.entity.User;
import com.library.auth_service.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(UserUpdateDto updateDto) {
        // Check if email already exists
        if (userRepository.findByEmail(updateDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setEmail(updateDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(updateDto.getPassword()));
        user.setRole(Role.valueOf(updateDto.getRole().toUpperCase()));
        user.setStatus(Status.ACTIVE);

        User saved = userRepository.save(user);
        return mapToResponseDto(saved);
    }

    public List<UserResponseDto> getAllUsers(String role) {
        List<User> users;
        if (role != null && !role.isEmpty()) {
            try {
                Role roleEnum = Role.valueOf(role.toUpperCase());
                users = userRepository.findByRole(roleEnum);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + role);
            }
        } else {
            users = userRepository.findAll();
        }
        
        return users.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToResponseDto(user);
    }

    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (updateDto.getEmail() != null && !updateDto.getEmail().isEmpty()) {
            // Check if email already exists for another user
            userRepository.findByEmail(updateDto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
                }
            });
            user.setEmail(updateDto.getEmail());
        }

        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(updateDto.getPassword()));
        }

        if (updateDto.getRole() != null && !updateDto.getRole().isEmpty()) {
            try {
                user.setRole(Role.valueOf(updateDto.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
            }
        }

        if (updateDto.getStatus() != null && !updateDto.getStatus().isEmpty()) {
            try {
                user.setStatus(Status.valueOf(updateDto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
            }
        }

        User updated = userRepository.save(user);
        return mapToResponseDto(updated);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    public UserResponseDto updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            user.setStatus(Status.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        User updated = userRepository.save(user);
        return mapToResponseDto(updated);
    }

    public String getUserEmail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return user.getEmail();
    }

    private UserResponseDto mapToResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
