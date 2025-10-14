package com.example.oauth2.service;

import com.example.oauth2.dto.RegisterInput;
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
  public RegisterResponse registerUser(final RegisterInput request) {
    // Check if username already exists
    if (userRepository.existsByUsername(request.username())) {
      return RegisterResponse.REGISTER_RESPONSE_USERNAME_ALREADY_EXISTS;
    }

    // Create new User POJO
    final User user =
        User.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .role("USER")
            .enabled(true)
            .build();

    // Save to database
    final User savedUser = userRepository.save(user);

    return RegisterResponse.REGISTER_RESPONSE_SUCCESS_TEMPLATE.userId(savedUser.getId()).build();
  }
}
