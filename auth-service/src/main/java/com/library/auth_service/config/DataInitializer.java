package com.library.auth_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.library.auth_service.entity.Role;
import com.library.auth_service.entity.Status;
import com.library.auth_service.entity.User;
import com.library.auth_service.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() == 0) {
            // Create sample users
            createSampleUsers();
            System.out.println("Sample users initialized successfully!");
        } else {
            System.out.println("Users already exist, skipping initialization.");
        }
    }

    private void createSampleUsers() {
        // Default password for all users
        String defaultPassword = "password123";
        String hashedPassword = passwordEncoder.encode(defaultPassword);

        // 1. Admin user
        User admin = new User();
        admin.setEmail("admin@library.com");
        admin.setPasswordHash(hashedPassword);
        admin.setRole(Role.ADMIN);
        admin.setStatus(Status.ACTIVE);
        userRepository.save(admin);

        // 2. Librarian user
        User librarian = new User();
        librarian.setEmail("librarian@library.com");
        librarian.setPasswordHash(hashedPassword);
        librarian.setRole(Role.LIBRARIAN);
        librarian.setStatus(Status.ACTIVE);
        userRepository.save(librarian);

        // 3. Regular user 1
        User user1 = new User();
        user1.setEmail("user1@library.com");
        user1.setPasswordHash(hashedPassword);
        user1.setRole(Role.USER);
        user1.setStatus(Status.ACTIVE);
        userRepository.save(user1);

        // 4. Regular user 2
        User user2 = new User();
        user2.setEmail("user2@library.com");
        user2.setPasswordHash(hashedPassword);
        user2.setRole(Role.USER);
        user2.setStatus(Status.ACTIVE);
        userRepository.save(user2);

        // 5. Regular user 3
        User user3 = new User();
        user3.setEmail("user3@library.com");
        user3.setPasswordHash(hashedPassword);
        user3.setRole(Role.USER);
        user3.setStatus(Status.ACTIVE);
        userRepository.save(user3);

        System.out.println("Created sample users:");
        System.out.println("- admin@library.com (ADMIN) - password: " + defaultPassword);
        System.out.println("- librarian@library.com (LIBRARIAN) - password: " + defaultPassword);
        System.out.println("- user1@library.com (USER) - password: " + defaultPassword);
        System.out.println("- user2@library.com (USER) - password: " + defaultPassword);
        System.out.println("- user3@library.com (USER) - password: " + defaultPassword);
    }
}