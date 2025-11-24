package com.library.user_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.library.user_service.dto.UpdateUserProfileDto;
import com.library.user_service.dto.UserProfileDto;
import com.library.user_service.dto.UserProfileResponseDto;
import com.library.user_service.service.UserProfileService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getAllProfiles() {
        List<UserProfileResponseDto> profiles = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponseDto> getProfileByUserId(@PathVariable Long userId) {
        return userProfileService.getUserProfileByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponseDto> getProfileById(@PathVariable Long id) {
        return userProfileService.getUserProfileById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    @PostMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> createProfile(@RequestBody UserProfileDto profileDto) {
        UserProfileResponseDto created = userProfileService.createUserProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateUserProfileDto updateDto) {
        UserProfileResponseDto updated = userProfileService.updateUserProfile(id, updateDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}/profile")
    public ResponseEntity<Void> deleteProfileByUserId(@PathVariable Long userId) {
        userProfileService.deleteUserProfileByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/lock")
    public ResponseEntity<Void> lockBorrowing(@PathVariable Long userId) {
        userProfileService.lockUserBorrowing(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/unlock")
    public ResponseEntity<Void> unlockBorrowing(@PathVariable Long userId) {
        userProfileService.unlockUserBorrowing(userId);
        return ResponseEntity.ok().build();
    }
}
