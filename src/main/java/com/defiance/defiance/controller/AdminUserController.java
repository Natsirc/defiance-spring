package com.defiance.defiance.controller;

import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.User;
import com.defiance.defiance.repository.UserRepository;
import com.defiance.defiance.service.AuthService;
import com.defiance.defiance.util.ResponseUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserRepository userRepository;
    private final AuthService authService;

    public AdminUserController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        authService.requireAdmin(authHeader);
        List<Map<String, Object>> users = userRepository.findAll().stream()
            .map(ResponseUtil::userToMap)
            .collect(Collectors.toList());
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("users", users);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload
    ) {
        authService.requireAdmin(authHeader);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (payload.containsKey("is_admin")) {
            user.setIsAdmin(Boolean.parseBoolean(payload.get("is_admin").toString()));
        }
        userRepository.save(user);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("user", ResponseUtil.userToMap(user));
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @PathVariable Long id
    ) {
        authService.requireAdmin(authHeader);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }
}
