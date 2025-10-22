package learning.journalapp.platform.logging;

import org.slf4j.MDC;

/**
 * Utility class for accessing the current correlation ID. This is useful when you need to pass the
 * correlation ID to async operations or when making HTTP calls to other services.
 */
public class CorrelationIdHolder {

  public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
  private static final String CORRELATION_ID_MDC_KEY = "correlationId";

  private CorrelationIdHolder() {
    // Utility class
  }

  /**
   * Get the current correlation ID from MDC.
   *
   * @return correlation ID, or null if not set
   */
  public static String getCorrelationId() {
    return MDC.get(CORRELATION_ID_MDC_KEY);
  }

  /**
   * Set the correlation ID in MDC.
   *
   * @param correlationId the correlation ID to set
   */
  public static void setCorrelationId(final String correlationId) {
    if (correlationId != null && !correlationId.trim().isEmpty()) {
      MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
    }
  }

  /** Clear the correlation ID from MDC. */
  public static void clearCorrelationId() {
    MDC.remove(CORRELATION_ID_MDC_KEY);
  }
}
