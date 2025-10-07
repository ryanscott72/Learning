package com.example.oauth2.dto;

import lombok.Data;

/** (DTO for registration) */
@Data
public class RegisterRequest {
  private String username;
  private String password;
  private String firstName;
  private String lastName;
}
