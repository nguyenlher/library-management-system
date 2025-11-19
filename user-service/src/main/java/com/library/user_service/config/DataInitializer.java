package com.library.user_service.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.library.user_service.entity.UserProfile;
import com.library.user_service.repository.UserProfileRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserProfileRepository userProfileRepository;

    public DataInitializer(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userProfileRepository.count() == 0) {
            // Create sample user profiles
            // Note: These user_ids should correspond to users created in auth-service
            createSampleUserProfiles();
            System.out.println("Sample user profiles initialized successfully!");
        } else {
            System.out.println("User profiles already exist, skipping initialization.");
        }
    }

    private void createSampleUserProfiles() {
        // Sample user profile 1 - Admin user
        UserProfile adminProfile = new UserProfile();
        adminProfile.setUserId(1L);
        adminProfile.setName("Administrator");
        adminProfile.setPhone("+1234567890");
        adminProfile.setAddress("123 Admin Street, Admin City, AC 12345");
        adminProfile.setBorrowLock(false);
        adminProfile.setTotalFine(BigDecimal.ZERO);
        userProfileRepository.save(adminProfile);

        // Sample user profile 2 - Librarian user
        UserProfile librarianProfile = new UserProfile();
        librarianProfile.setUserId(2L);
        librarianProfile.setName("John Librarian");
        librarianProfile.setPhone("+1234567891");
        librarianProfile.setAddress("456 Library Avenue, Book City, BC 67890");
        librarianProfile.setBorrowLock(false);
        librarianProfile.setTotalFine(BigDecimal.ZERO);
        userProfileRepository.save(librarianProfile);

        // Sample user profile 3 - Regular user
        UserProfile regularUserProfile = new UserProfile();
        regularUserProfile.setUserId(3L);
        regularUserProfile.setName("Nguyen Van A");
        regularUserProfile.setPhone("+1234500001");
        regularUserProfile.setAddress("789 Tan Phong, Q7, Tp. Ho Chi Minh, VN");
        regularUserProfile.setBorrowLock(false);
        regularUserProfile.setTotalFine(BigDecimal.valueOf(0));
        userProfileRepository.save(regularUserProfile);

        // Sample user profile 4 - Regular user
        UserProfile lockedUserProfile = new UserProfile();
        lockedUserProfile.setUserId(4L);
        lockedUserProfile.setName("Tran Thi B");
        lockedUserProfile.setPhone("+1234500002");
        lockedUserProfile.setAddress("321 Tan Binh, Q1, Tp. Ho Chi Minh, VN");
        lockedUserProfile.setBorrowLock(false);
        lockedUserProfile.setTotalFine(BigDecimal.valueOf(25.75));
        userProfileRepository.save(lockedUserProfile);

        // Sample user profile 5 - Another regular user
        UserProfile user3Profile = new UserProfile();
        user3Profile.setUserId(5L);
        user3Profile.setName("Le Van C");
        user3Profile.setPhone("+1234500003");
        user3Profile.setAddress("654 Binh Thanh, Q3, Tp. Ho Chi Minh, VN");
        user3Profile.setBorrowLock(false);
        user3Profile.setTotalFine(BigDecimal.ZERO);
        userProfileRepository.save(user3Profile);
    }
}