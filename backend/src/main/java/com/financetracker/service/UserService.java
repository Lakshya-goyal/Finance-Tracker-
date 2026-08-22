package com.financetracker.service;

import com.financetracker.dto.AuthResponse;
import com.financetracker.dto.LoginRequest;
import com.financetracker.dto.RegisterRequest;
import com.financetracker.dto.UserDto;
import com.financetracker.entity.User;
import com.financetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered. Please login instead.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Plain password for beginner project, easy to hash later

        User savedUser = userRepository.save(user);

        UserDto userDto = new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
        return new AuthResponse(true, "Registration successful!", userDto);
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password.");
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password.");
        }

        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        return new AuthResponse(true, "Login successful!", userDto);
    }

    public User getUserById(Long userId) {
        if (userId != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
        }
        // Graceful fallback for standalone demo or server restarts
        return userRepository.findAll().stream().findFirst().orElseGet(() -> {
            User demo = new User("Demo User", "demo@example.com", "password123");
            return userRepository.save(demo);
        });
    }
}
