package learning.journalapp.platform.logging.config;

import learning.journalapp.platform.logging.filter.CorrelationIdFilter;
import learning.journalapp.platform.logging.interceptor.RestTemplateCorrelationIdInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/** Auto-configuration for logging commons beans. */
@Configuration
public class LoggingAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public CorrelationIdFilter correlationIdFilter() {
    return new CorrelationIdFilter();
  }

  /**
   * Automatically configure RestTemplate to propagate correlation IDs. This only activates if
   * RestTemplate is on the classpath.
   */
  @Bean
  @ConditionalOnClass(RestTemplate.class)
  public RestTemplateCustomizer restTemplateCorrelationIdCustomizer() {
    return restTemplate ->
        restTemplate.getInterceptors().add(new RestTemplateCorrelationIdInterceptor());
  }
}
