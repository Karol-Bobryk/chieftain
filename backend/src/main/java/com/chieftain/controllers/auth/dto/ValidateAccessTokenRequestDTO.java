package com.chieftain.controllers.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidateAccessTokenRequestDTO {
  @NotBlank(message = "Access token cannot be blank")
  String accessToken;
}
