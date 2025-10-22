package learning.journalapp.platform.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Base security configuration providing common beans NOT annotated with @Configuration -
 * microservices will extend this
 */
public abstract class BaseSecurityConfig {

  /**
   * BCrypt password encoder with default strength 10 All microservices should use the same password
   * encoding
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
