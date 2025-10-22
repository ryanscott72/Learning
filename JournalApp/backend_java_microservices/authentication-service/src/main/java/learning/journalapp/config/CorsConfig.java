package learning.journalapp.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Value("${cors.allowed-origins:http://localhost:3000}")
  private List<String> allowedOrigins;

  @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
  private List<String> allowedMethods;

  @Value("${cors.allowed-headers:*}")
  private List<String> allowedHeaders;

  @Value("${cors.allow-credentials:true}")
  private boolean allowCredentials;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(allowedMethods);
    configuration.setAllowedHeaders(allowedHeaders);
    configuration.setAllowCredentials(allowCredentials);

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
