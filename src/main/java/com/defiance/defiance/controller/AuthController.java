package com.defiance.defiance.controller;

import com.defiance.defiance.dto.AuthLoginRequest;
import com.defiance.defiance.dto.AuthRegisterRequest;
import com.defiance.defiance.dto.ProfileUpdateRequest;
import com.defiance.defiance.model.User;
import com.defiance.defiance.repository.UserRepository;
import com.defiance.defiance.service.AuthService;
import com.defiance.defiance.util.ResponseUtil;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody AuthRegisterRequest request) {
        User user = authService.register(request);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("user", ResponseUtil.userToMap(user));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthLoginRequest request) {
        User user = authService.login(request);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("user", ResponseUtil.userToMap(user));
        body.put("token", user.getAuthToken());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        User user = authService.requireUser(authHeader);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("user", ResponseUtil.userToMap(user));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        authService.logout(authHeader);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @Valid @RequestBody ProfileUpdateRequest request
    ) {
        User user = authService.requireUser(authHeader);
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddressLine(request.getAddressLine());
        user.setBarangay(request.getBarangay());
        user.setCity(request.getCity());
        user.setProvince(request.getProvince());
        user.setPostalCode(request.getPostalCode());
        userRepository.save(user);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("user", ResponseUtil.userToMap(user));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String token = payload.get("token");
        if (email != null && token != null) {
            authService.verify(email, token);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/resend")
    public ResponseEntity<Map<String, Object>> resend(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email != null) {
            authService.resendVerification(email);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/verify-link")
    public ResponseEntity<Map<String, Object>> verifyLink(
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "token", required = false) String token
    ) {
        if (email != null && token != null) {
            authService.verify(email, token);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Map<String, Object>> forgot(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email != null) {
            authService.requestPasswordReset(email);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, Object>> reset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        String token = payload.get("token");
        if (email != null && password != null && token != null) {
            authService.resetPassword(email, token, password);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }
}
