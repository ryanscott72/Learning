package learning.journalapp.platform.logging;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Structured logger wrapper that adds context to log messages using MDC. This logger integrates
 * with Logstash encoder for JSON-formatted logs.
 *
 * <p>Usage:
 *
 * <pre>
 * private static final StructuredLogger logger = StructuredLogger.getLogger(MyClass.class);
 *
 * logger.info("User logged in");
 * logger.info("Order created", Map.of("orderId", "123", "userId", "456"));
 * logger.error("Failed to process payment", exception, Map.of("orderId", "123"));
 * </pre>
 */
public class StructuredLogger {

  private final Logger logger;

  private StructuredLogger(final Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  /** Get a structured logger for a class. */
  public static StructuredLogger getLogger(final Class<?> clazz) {
    return new StructuredLogger(clazz);
  }

  // ========== INFO Level ==========

  public void info(final String message) {
    logger.info(message);
  }

  public void info(final String message, final Map<String, String> context) {
    withContext(context, () -> logger.info(message));
  }

  // ========== DEBUG Level ==========

  public void debug(final String message) {
    logger.debug(message);
  }

  public void debug(final String message, final Map<String, String> context) {
    withContext(context, () -> logger.debug(message));
  }

  // ========== WARN Level ==========

  public void warn(final String message) {
    logger.warn(message);
  }

  public void warn(final String message, final Map<String, String> context) {
    withContext(context, () -> logger.warn(message));
  }

  public void warn(final String message, final Throwable t) {
    logger.warn(message, t);
  }

  public void warn(final String message, final Throwable t, final Map<String, String> context) {
    withContext(context, () -> logger.warn(message, t));
  }

  // ========== ERROR Level ==========

  public void error(final String message) {
    logger.error(message);
  }

  public void error(final String message, final Throwable t) {
    logger.error(message, t);
  }

  public void error(final String message, final Map<String, String> context) {
    withContext(context, () -> logger.error(message));
  }

  public void error(final String message, final Throwable t, final Map<String, String> context) {
    withContext(context, () -> logger.error(message, t));
  }

  // ========== Helper Methods ==========

  /**
   * Execute a logging operation with MDC context. Context is automatically cleaned up after
   * logging.
   */
  private void withContext(final Map<String, String> context, final Runnable logOperation) {
    if (context == null || context.isEmpty()) {
      logOperation.run();
      return;
    }

    // Add context to MDC
    context.forEach(MDC::put);

    try {
      logOperation.run();
    } finally {
      // Clean up MDC
      context.keySet().forEach(MDC::remove);
    }
  }

  /** Check if DEBUG level is enabled. */
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  /** Check if INFO level is enabled. */
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }
}
