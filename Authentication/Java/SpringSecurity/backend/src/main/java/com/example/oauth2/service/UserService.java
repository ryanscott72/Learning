package com.example.oauth2.service;

import com.example.oauth2.dto.RegisterRequest;
import com.example.oauth2.dto.RegisterResponse;
import com.example.oauth2.model.User;
import com.example.oauth2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public RegisterResponse registerUser(RegisterRequest request) {
    // Check if username already exists
    if (userRepository.existsByUsername(request.getUsername())) {
      return new RegisterResponse(false, "Username already exists", null);
    }

    // Create new User POJO
    User user =
        User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .role("USER")
            .enabled(true)
            .build();

    // Save to database
    User savedUser = userRepository.save(user);

    return new RegisterResponse(true, "User registered successfully", savedUser.getId());
  }
}
