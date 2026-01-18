package com.defiance.defiance.service;

import com.defiance.defiance.exception.ResourceNotFoundException;
import com.defiance.defiance.model.User;
import com.defiance.defiance.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User update(Long id, User updated) {
        User existing = getById(id);
        updated.setId(existing.getId());
        return userRepository.save(updated);
    }

    public void delete(Long id) {
        User existing = getById(id);
        userRepository.delete(existing);
    }
}
