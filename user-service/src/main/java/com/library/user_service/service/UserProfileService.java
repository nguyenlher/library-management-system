package com.library.user_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.user_service.dto.UpdateUserProfileDto;
import com.library.user_service.dto.UserProfileDto;
import com.library.user_service.dto.UserProfileResponseDto;
import com.library.user_service.entity.UserProfile;
import com.library.user_service.repository.UserProfileRepository;

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileResponseDto createUserProfile(UserProfileDto dto) {
        // Check if profile already exists for this user
        if (userProfileRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new RuntimeException("User profile already exists for user ID: " + dto.getUserId());
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(dto.getUserId());
        userProfile.setName(dto.getName());
        userProfile.setPhone(dto.getPhone());
        userProfile.setAddress(dto.getAddress());
        userProfile.setBorrowLock(dto.getBorrowLock() != null ? dto.getBorrowLock() : Boolean.FALSE);
        userProfile.setTotalFine(dto.getTotalFine() != null ? dto.getTotalFine() : java.math.BigDecimal.ZERO);

        UserProfile saved = userProfileRepository.save(userProfile);
        return mapToResponseDto(saved);
    }

    public Optional<UserProfileResponseDto> getUserProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .map(this::mapToResponseDto);
    }

    public Optional<UserProfileResponseDto> getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    public List<UserProfileResponseDto> getAllUserProfiles() {
        return userProfileRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public UserProfileResponseDto updateUserProfile(Long id, UpdateUserProfileDto dto) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User profile not found with ID: " + id));

        if (dto.getName() != null) {
            userProfile.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            userProfile.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            userProfile.setAddress(dto.getAddress());
        }
        if (dto.getBorrowLock() != null) {
            userProfile.setBorrowLock(dto.getBorrowLock());
        }
        if (dto.getTotalFine() != null) {
            userProfile.setTotalFine(dto.getTotalFine());
        }

        UserProfile updated = userProfileRepository.save(userProfile);
        return mapToResponseDto(updated);
    }

    public void deleteUserProfileByUserId(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for user ID: " + userId));
        userProfileRepository.delete(userProfile);
    }

    public void lockUserBorrowing(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for user ID: " + userId));
        userProfile.setBorrowLock(Boolean.TRUE);
        userProfileRepository.save(userProfile);
    }

    public void unlockUserBorrowing(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for user ID: " + userId));
        userProfile.setBorrowLock(Boolean.FALSE);
        userProfileRepository.save(userProfile);
    }

    public void addFine(Long userId, java.math.BigDecimal fineAmount) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for user ID: " + userId));
        userProfile.setTotalFine(userProfile.getTotalFine().add(fineAmount));
        userProfileRepository.save(userProfile);
    }

    private UserProfileResponseDto mapToResponseDto(UserProfile userProfile) {
        return new UserProfileResponseDto(
                userProfile.getId(),
                userProfile.getUserId(),
                userProfile.getName(),
                userProfile.getPhone(),
                userProfile.getAddress(),
                userProfile.getBorrowLock(),
                userProfile.getTotalFine(),
                userProfile.getCreatedAt(),
                userProfile.getUpdatedAt()
        );
    }
}