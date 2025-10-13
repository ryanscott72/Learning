package com.example.oauth2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {

  public static final RegisterResponseBuilder REGISTER_RESPONSE_SUCCESS_TEMPLATE =
      RegisterResponse.builder().success(true).message("User registered successfully");
  public static final RegisterResponse REGISTER_RESPONSE_USERNAME_ALREADY_EXISTS =
      RegisterResponse.builder().success(false).message("Username already exists").build();

  private boolean success;
  private String message;
  private Long userId;
}
