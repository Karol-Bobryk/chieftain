package com.chieftain.controllers.auth;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.auth.dto.RefreshAccessTokenRequestDTO;
import com.chieftain.controllers.auth.dto.RefreshAccessTokenResponseDTO;
import com.chieftain.controllers.auth.dto.ValidateAccessTokenRequestDTO;
import com.chieftain.exceptions.InvalidUserSecretProvidedException;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.dto.JwtTokens;
import com.chieftain.services.JwtService;
import com.chieftain.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/token")
public class TokenController {
  private final JwtService jwtService;
  private final UserService userService;

  public TokenController(JwtService jwtService, UserService userService) {
    this.jwtService = jwtService;
    this.userService = userService;
  }

  @PostMapping("/refresh")
  @Transactional
  public ResponseEntity<RefreshAccessTokenResponseDTO> refreshAccessToken(
      @Valid @RequestBody RefreshAccessTokenRequestDTO request) {
    UUID userId = JwtService.getUserId(request.getRefreshToken());
    UserEntity userEntity = userService.getUserById(userId);
    CustomUserDetails userDetails = new CustomUserDetails(userEntity);

    JwtTokens newTokens = jwtService.refreshTokens(request.getRefreshToken(), userDetails);
    return new ResponseEntity<>(
        new RefreshAccessTokenResponseDTO(newTokens.getAccessToken(), newTokens.getRefreshToken()),
        HttpStatus.OK);
  }

  @PostMapping("/validate")
  public ResponseEntity<Void> validateToken(
      @Valid @RequestBody ValidateAccessTokenRequestDTO request)
      throws InvalidUserSecretProvidedException {

    UUID userId = JwtService.getUserId(request.getAccessToken());
    UserEntity userEntity = userService.getUserById(userId);
    CustomUserDetails userDetails = new CustomUserDetails(userEntity);

    if (!JwtService.isTokenValidForUser(request.getAccessToken(), userDetails)) {
      throw new InvalidUserSecretProvidedException("Provided auth token is not valid");
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
