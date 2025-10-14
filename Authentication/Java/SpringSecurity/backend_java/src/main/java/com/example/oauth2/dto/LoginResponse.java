package com.example.oauth2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

  public static final LoginResponseBuilder LOGIN_RESPONSE_SUCCESS_TEMPLATE =
      LoginResponse.builder().success(true).message("Login successful");
  public static final LoginResponse LOGIN_RESPONSE_USER_NOT_FOUND =
      LoginResponse.builder()
          .success(false)
          .message("User not found. Please register first.")
          .build();
  public static final LoginResponse LOGIN_RESPONSE_BAD_CREDENTIALS =
      LoginResponse.builder().success(false).message("Invalid username or password").build();
  public static final LoginResponse LOGIN_RESPONSE_INTERNAL_SERVER_ERROR =
      LoginResponse.builder().success(false).message("Authentication failed").build();

  private boolean success;
  private String message;
  private String token;
  private String username;
}
