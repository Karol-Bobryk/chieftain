package com.chieftain.controllers.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginUserRequestDTO {
  @NotBlank(message = "Email address cannot be blank")
  @Email(message = "Email address is invalid")
  String emailAddress;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 2, max = 255, message = "Password must be between 2 and 255 characters")
  String password;
}
