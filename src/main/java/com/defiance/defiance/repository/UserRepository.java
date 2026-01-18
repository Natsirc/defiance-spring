package com.defiance.defiance.repository;

import com.defiance.defiance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAuthToken(String authToken);
    Optional<User> findByEmailAndVerificationToken(String email, String verificationToken);
    Optional<User> findByEmailAndResetToken(String email, String resetToken);
}
