package learning.journalapp.service;

import java.util.Map;
import learning.journalapp.entity.User;
import learning.journalapp.platform.logging.StructuredLogger;
import learning.journalapp.platform.security.util.JwtTokenProvider;
import learning.journalapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private static final StructuredLogger logger = StructuredLogger.getLogger(AuthService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder; // From platform!
  private final JwtTokenProvider jwtTokenProvider; // From platform!
  private final AuthenticationManager authenticationManager;

  @Transactional
  public String login(final String username, final String password) {
    logger.info("User login attempt", Map.of("username", username));

    try {
      // Authenticate user
      final Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(username, password));

      // Generate JWT token
      final String token = jwtTokenProvider.generateToken(username);

      logger.info("User logged in successfully", Map.of("username", username));

      return token;

    } catch (Exception e) {
      logger.error("Login failed", e, Map.of("username", username));
      throw e;
    }
  }

  @Transactional
  public User register(final String username, final String email, final String password) {
    logger.info("User registration attempt", Map.of("username", username, "email", email));

    // Check if user already exists
    if (userRepository.existsByUsername(username)) {
      logger.warn("Registration failed - username already exists", Map.of("username", username));
      throw new RuntimeException("Username already exists");
    }

    if (userRepository.existsByEmail(email)) {
      logger.warn("Registration failed - email already exists", Map.of("email", email));
      throw new RuntimeException("Email already exists");
    }

    // Create new user
    final User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password)); // Use platform encoder!

    final User savedUser = userRepository.save(user);

    logger.info(
        "User registered successfully",
        Map.of("username", username, "userId", savedUser.getId().toString()));

    return savedUser;
  }
}
