package com.chieftain.controllers.auth.dto;

import com.chieftain.repositories.dto.JwtTokens;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshAccessTokenResponseDTO extends JwtTokens {
  @NotBlank(message = "Access token cannot be blank")
  String accessToken;

  @NotBlank(message = "Refresh token cannot be blank")
  String refreshToken;
}
