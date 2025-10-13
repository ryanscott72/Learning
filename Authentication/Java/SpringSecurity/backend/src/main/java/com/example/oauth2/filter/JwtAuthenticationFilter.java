package com.example.oauth2.filter;

import com.example.oauth2.service.CustomUserDetailsService;
import com.example.oauth2.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A Servlet {@link OncePerRequestFilter} that checks whether any given {@link HttpServletRequest}
 * contains a valid JWT Token.
 *
 * <p>If it does not contain a valid JWT Token, the request continues as
 * <strong>unauthenticated</strong> and is ultimately rejected by the SecurityConfig's {@code
 * .anyRequest().authenticated()} rule.
 *
 * <p>If it does contain a valid JWT Token, the next Filter in the FilterChain is invoked.
 *
 * @see com.example.oauth2.config.SecurityConfig
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  /**
   * @param request The request to process
   * @param response The response associated with the request
   * @param chain Provides access to the next filter in the chain for this filter to pass the
   *     request and response to for further processing
   * @throws ServletException If the processing fails for any other reason
   * @throws IOException If an I/O error occurs during this filter's processing of the request
   */
  @Override
  protected void doFilterInternal(
      final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
      throws ServletException, IOException {

    String jwt = null;

    // First, try to get token from Authorization header (Bearer token)
    final String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
    }

    // If no Bearer token, try to get from cookie
    if (jwt == null && request.getCookies() != null) {
      for (final Cookie cookie : request.getCookies()) {
        if ("token".equals(cookie.getName())) {
          jwt = cookie.getValue();
          break;
        }
      }
    }

    String username = null;

    if (jwt != null) {
      try {
        username = jwtUtil.extractUsername(jwt);
      } catch (final Exception e) {
        // TODO Invalid token
      }
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (jwtUtil.validateToken(jwt, userDetails)) {
        final var authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    chain.doFilter(request, response);
  }
}
