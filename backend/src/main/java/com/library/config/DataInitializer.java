package com.library.config;

import com.library.model.User;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed default admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    "admin@library.com",
                    passwordEncoder.encode("admin123"),
                    "Library Administrator",
                    "ROLE_ADMIN"
            );
            userRepository.save(admin);
            logger.info("✅ Default admin user created: username=admin, password=admin123");
        } else {
            logger.info("ℹ️  Admin user already exists, skipping seed.");
        }

        // Seed a librarian user for testing
        if (!userRepository.existsByUsername("librarian")) {
            User librarian = new User(
                    "librarian",
                    "librarian@library.com",
                    passwordEncoder.encode("librarian123"),
                    "Head Librarian",
                    "ROLE_LIBRARIAN"
            );
            userRepository.save(librarian);
            logger.info("✅ Default librarian user created: username=librarian, password=librarian123");
        }
    }
}
