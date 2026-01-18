package com.defiance.defiance.config;

import com.defiance.defiance.model.User;
import com.defiance.defiance.repository.UserRepository;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            boolean hasAdmin = userRepository.findAll().stream()
                .anyMatch(user -> Boolean.TRUE.equals(user.getIsAdmin()));
            if (!hasAdmin) {
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setEmail("admin@defiance.co");
                admin.setPhone("0000000000");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setIsAdmin(true);
                admin.setEmailVerified(true);
                admin.setAuthToken(UUID.randomUUID().toString());
                userRepository.save(admin);
            }
        };
    }
}
