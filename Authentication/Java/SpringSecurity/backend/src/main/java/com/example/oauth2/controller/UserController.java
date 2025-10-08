package com.example.oauth2.controller;

import com.example.oauth2.dto.LoginRequest;
import com.example.oauth2.dto.LoginResponse;
import com.example.oauth2.dto.RegisterRequest;
import com.example.oauth2.dto.RegisterResponse;
import com.example.oauth2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final AuthenticationManager authenticationManager;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
    RegisterResponse response = userService.registerUser(request);

    if (response.isSuccess()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    try {
      // This checks the database for the user credentials
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.getUsername(), request.getPassword()));

      return ResponseEntity.ok(
          new LoginResponse(true, "Login successful", authentication.getName()));

    } catch (UsernameNotFoundException e) {
      return ResponseEntity.status(404)
          .body(new LoginResponse(false, "User not found. Please register first.", null));
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(401)
          .body(new LoginResponse(false, "Invalid username or password", null));
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new LoginResponse(false, "Authentication failed", null));
    }
  }

  @GetMapping("/profile")
  public ResponseEntity<String> getProfile(Authentication authentication) {
    return ResponseEntity.ok("Welcome, " + authentication.getName() + "!");
  }

  @GetMapping("/secure")
  public ResponseEntity<String> secureEndpoint(Authentication authentication) {
    return ResponseEntity.ok("This is a secured endpoint. User: " + authentication.getName());
  }
}
