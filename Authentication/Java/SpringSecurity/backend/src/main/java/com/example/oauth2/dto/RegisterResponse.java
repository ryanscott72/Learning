package com.example.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** (DTO for registration response) */
@Data
@AllArgsConstructor
public class RegisterResponse {
  private boolean success;
  private String message;
  private Long userId;
}
