package learning.journalapp.graphql;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import learning.journalapp.dto.*;
import learning.journalapp.entity.User;
import learning.journalapp.repository.UserRepository;
import learning.journalapp.service.UserService;
import learning.journalapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
@RequiredArgsConstructor
public class GraphQLController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Value("${jwt.expiration}")
  private Long jwtExpiration;

  @MutationMapping
  public RegisterResponse register(@Argument final RegisterInput input) {
    return userService.registerUser(input);
  }

  @MutationMapping
  public LoginResponse login(@Argument final LoginInput input) {
    try {
      final Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(input.username(), input.password()));

      final String token = jwtUtil.generateToken(authentication.getName());

      // Get response from context
      final HttpServletResponse response =
          ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
              .getResponse();

      if (response != null) {
        final Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration / 1000));
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
      }

      return LoginResponse.LOGIN_RESPONSE_SUCCESS_TEMPLATE
          .username(authentication.getName())
          .build();

    } catch (final UsernameNotFoundException e) {
      return LoginResponse.LOGIN_RESPONSE_USER_NOT_FOUND;
    } catch (final BadCredentialsException e) {
      return LoginResponse.LOGIN_RESPONSE_BAD_CREDENTIALS;
    } catch (final Exception e) {
      return LoginResponse.LOGIN_RESPONSE_INTERNAL_SERVER_ERROR;
    }
  }

  @MutationMapping
  public String logout() {
    final HttpServletResponse response =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

    if (response != null) {
      final Cookie cookie = new Cookie("token", null);
      cookie.setHttpOnly(true);
      cookie.setSecure(false);
      cookie.setPath("/");
      cookie.setMaxAge(0);
      response.addCookie(cookie);
    }

    return "Logged out successfully";
  }

  @QueryMapping
  public UserProfileResponse profile() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new RuntimeException("Not authenticated");
    }

    final User user =
        userRepository
            .findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new UserProfileResponse(
        user.getUsername(), user.getFirstName(), user.getLastName(), user.getRole());
  }
}
