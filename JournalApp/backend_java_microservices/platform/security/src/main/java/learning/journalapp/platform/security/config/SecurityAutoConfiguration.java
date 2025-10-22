package learning.journalapp.platform.security.config;

import learning.journalapp.platform.security.filter.JwtAuthenticationFilter;
import learning.journalapp.platform.security.util.JwtTokenProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Auto-configuration for security commons beans. These beans are automatically available to any
 * microservice that includes security module. Microservices can override any of these beans by
 * providing their own.
 */
@Configuration
public class SecurityAutoConfiguration {

  /** Provide BCrypt password encoder. Can be overridden by microservices if needed. */
  @Bean
  @ConditionalOnMissingBean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** Provide JWT token provider. Can be overridden by microservices if needed. */
  @Bean
  @ConditionalOnMissingBean
  public JwtTokenProvider jwtTokenProvider() {
    return new JwtTokenProvider();
  }

  /**
   * Provide JWT authentication filter. Requires JwtTokenProvider and UserDetailsService beans. Can
   * be overridden by microservices if needed.
   */
  @Bean
  @ConditionalOnMissingBean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      final JwtTokenProvider jwtTokenProvider, final UserDetailsService userDetailsService) {
    return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
  }
}
