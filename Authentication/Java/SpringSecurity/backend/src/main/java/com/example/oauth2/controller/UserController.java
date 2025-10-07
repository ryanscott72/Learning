package com.example.oauth2.controller;

import com.example.oauth2.dto.RegisterRequest;
import com.example.oauth2.dto.RegisterResponse;
import com.example.oauth2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
    RegisterResponse response = userService.registerUser(request);

    if (response.isSuccess()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
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
