package learning.journalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
  private String username;
  private String firstName;
  private String lastName;
  private String role;
}
