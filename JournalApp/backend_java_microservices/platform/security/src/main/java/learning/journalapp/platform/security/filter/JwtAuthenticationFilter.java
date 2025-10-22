package learning.journalapp.platform.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import learning.journalapp.platform.logging.StructuredLogger;
import learning.journalapp.platform.security.util.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter for validating JWT tokens in HTTP requests. This filter extracts JWT
 * tokens from the Authorization header, validates them, and sets the authentication in the security
 * context.
 *
 * <p>Required beans: - JwtTokenProvider: for token validation - UserDetailsService: for loading
 * user details
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final StructuredLogger logger =
      StructuredLogger.getLogger(JwtAuthenticationFilter.class);
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(
      final JwtTokenProvider jwtTokenProvider, final UserDetailsService userDetailsService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    try {
      final String jwt = extractJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
        final String username = jwtTokenProvider.getUsernameFromToken(jwt);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        final UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("Set authentication for user", Map.of("username", username));
      }
    } catch (Exception ex) {
      logger.error("Could not set user authentication in security context", ex);
      // Don't throw exception - let the request continue without authentication
      // The security configuration will handle unauthorized access
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extract JWT token from the Authorization header. Expected format: "Bearer {token}"
   *
   * @param request the HTTP request
   * @return JWT token, or null if not found or invalid format
   */
  private String extractJwtFromRequest(final HttpServletRequest request) {
    final String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }

    return null;
  }
}
