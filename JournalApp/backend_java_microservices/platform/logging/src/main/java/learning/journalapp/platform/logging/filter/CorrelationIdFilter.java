package learning.journalapp.platform.logging.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Filter that adds a correlation ID to every HTTP request. The correlation ID is: 1. Extracted from
 * the X-Correlation-Id header if present 2. Generated as a new UUID if not present 3. Added to MDC
 * for logging 4. Added to the response header
 *
 * <p>This enables request tracing across multiple microservices.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {

  private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
  private static final String CORRELATION_ID_MDC_KEY = "correlationId";

  @Override
  public void doFilter(
      final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {

    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpServletResponse httpResponse = (HttpServletResponse) response;

    // Get or generate correlation ID
    String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
    if (correlationId == null || correlationId.trim().isEmpty()) {
      correlationId = UUID.randomUUID().toString();
    }

    // Add to MDC for logging
    MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

    // Add to response headers
    httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

    try {
      chain.doFilter(request, response);
    } finally {
      // Clean up MDC
      MDC.remove(CORRELATION_ID_MDC_KEY);
    }
  }
}
