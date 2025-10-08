package com.example.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
  private boolean success;
  private String message;
  private String username;
}
