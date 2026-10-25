package com.technoboost.config;

import com.technoboost.entity.User;
import com.technoboost.enums.Role;
import com.technoboost.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Dev-only convenience: seeds an admin account so the admin UI is usable
 * out of the box. Never active in prod.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    static final String ADMIN_EMAIL = "admin@technoboost.com";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }
        userRepository.save(User.builder()
                .firstName("Admin")
                .lastName("User")
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ROLE_ADMIN)
                .emailVerified(true)
                .build());
        log.info("Seeded dev admin user: {} (password: Admin@123)", ADMIN_EMAIL);
    }
}
