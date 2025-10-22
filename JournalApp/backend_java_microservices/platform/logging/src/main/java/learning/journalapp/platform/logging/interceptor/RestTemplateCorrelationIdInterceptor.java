package learning.journalapp.platform.logging.interceptor;

import java.io.IOException;
import learning.journalapp.platform.logging.CorrelationIdHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * RestTemplate interceptor that automatically adds the correlation ID to outgoing HTTP requests.
 *
 * <p>This ensures that when Service A calls Service B, the correlation ID is propagated, allowing
 * you to trace requests across multiple services.
 *
 * <p>Usage: Register this interceptor with your RestTemplate bean.
 */
public class RestTemplateCorrelationIdInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
      throws IOException {

    final String correlationId = CorrelationIdHolder.getCorrelationId();

    if (correlationId != null) {
      request.getHeaders().add(CorrelationIdHolder.CORRELATION_ID_HEADER, correlationId);
    }

    return execution.execute(request, body);
  }
}
