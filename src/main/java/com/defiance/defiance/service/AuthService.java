package com.defiance.defiance.service;

import com.defiance.defiance.dto.AuthLoginRequest;
import com.defiance.defiance.dto.AuthRegisterRequest;
import com.defiance.defiance.exception.UnauthorizedException;
import com.defiance.defiance.model.User;
import com.defiance.defiance.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final String frontendBaseUrl;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        MailService mailService,
        @Value("${app.frontend-base-url}") String frontendBaseUrl
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public User register(AuthRegisterRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new UnauthorizedException("Email already registered");
        }
        if (request.getPasswordConfirmation() != null
            && !request.getPassword().equals(request.getPasswordConfirmation())) {
            throw new UnauthorizedException("Passwords do not match");
        }
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);
        user.setIsAdmin(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        User saved = userRepository.save(user);

        String link = frontendBaseUrl + "/verify.html?email=" + saved.getEmail()
            + "&token=" + saved.getVerificationToken();
        try {
            mailService.sendVerificationEmail(saved.getEmail(), link);
        } catch (Exception ignored) {
        }
        return saved;
    }

    public User login(AuthLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        if (Boolean.FALSE.equals(user.getEmailVerified())) {
            throw new UnauthorizedException("Please verify your email");
        }
        user.setAuthToken(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    public User requireUser(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Unauthorized");
        }
        return userRepository.findByAuthToken(token)
            .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
    }

    public User requireAdmin(String authHeader) {
        User user = requireUser(authHeader);
        if (Boolean.FALSE.equals(user.getIsAdmin())) {
            throw new UnauthorizedException("Admin access required");
        }
        return user;
    }

    public void logout(String authHeader) {
        User user = requireUser(authHeader);
        user.setAuthToken(null);
        userRepository.save(user);
    }

    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Email not found"));
        user.setVerificationToken(UUID.randomUUID().toString());
        userRepository.save(user);
        String link = frontendBaseUrl + "/verify.html?email=" + user.getEmail()
            + "&token=" + user.getVerificationToken();
        try {
            mailService.sendVerificationEmail(user.getEmail(), link);
        } catch (Exception ignored) {
        }
    }

    public void verify(String email, String token) {
        User user = userRepository.findByEmailAndVerificationToken(email, token)
            .orElseThrow(() -> new UnauthorizedException("Invalid verification link"));
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Email not found"));
        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpires(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        try {
            mailService.sendPasswordResetEmail(user.getEmail(), user.getResetToken());
        } catch (Exception ignored) {
        }
    }

    public void resetPassword(String email, String token, String newPassword) {
        User user = userRepository.findByEmailAndResetToken(email, token)
            .orElseThrow(() -> new UnauthorizedException("Invalid reset token"));
        if (user.getResetTokenExpires() == null || user.getResetTokenExpires().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Reset token expired");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpires(null);
        userRepository.save(user);
    }

    private String extractToken(String authHeader) {
        if (authHeader == null) return null;
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
