package com.chieftain.controllers.auth;

import com.chieftain.controllers.auth.dto.ErrorResponseDTO;
import com.chieftain.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.chieftain.controllers.auth")
public class AuthExceptionHandler {
  @ExceptionHandler(EmailIsAlreadyTakenException.class)
  public ResponseEntity<ErrorResponseDTO> handleEmailTaken() {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "Email address is already in use"));
  }

  @ExceptionHandler(UserIsBarredException.class)
  public ResponseEntity<ErrorResponseDTO> handleBarredUser() {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status).body(ErrorResponseDTO.of(status, "User is banned"));
  }

  @ExceptionHandler(UserSecretNotProvidedException.class)
  public ResponseEntity<ErrorResponseDTO> handleSecretNotProvided() {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(ErrorResponseDTO.of(status, "Secret not provided"));
  }

  @ExceptionHandler(InvalidUserSecretProvidedException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidSecret() {
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "Invalid user secret provided"));
  }

  @ExceptionHandler(EmailAddressNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleEmailNotFound() {
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "There is no matching user for given email address"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus()
  public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(
            ErrorResponseDTO.of(
                status,
                "Request field validation failed "
                    + Objects.requireNonNull(ex.getBindingResult().getFieldError())
                        .getDefaultMessage()));
  }

  @ExceptionHandler(RefreshTokenBlacklistedException.class)
  public ResponseEntity<ErrorResponseDTO> handleBlacklistedRefreshToken() {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "Use of this refresh token is forbidden"));
  }

  @ExceptionHandler(RefreshTokenJtiNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleRefreshTokenNoJti() {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "Refresh token is invalid, couldn't get token JTI"));
  }

  @ExceptionHandler(RefreshTokenExpNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleRefreshTokenNoExp() {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status)
        .body(
            ErrorResponseDTO.of(
                status, "Refresh token is invalid, couldn't get token expiry date"));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorResponseDTO> handleRefreshTokenExpired() {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "Refresh token is expired"));
  }

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<ErrorResponseDTO> handleRefreshTokenInvalid() {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "Refresh token is invalid"));
  }

  @ExceptionHandler(UserIsNotAcceptedException.class)
  public ResponseEntity<ErrorResponseDTO> handleUserIs(UserIsNotAcceptedException ex) {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status)
            .body(ErrorResponseDTO.of(status, ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneralAuthError(Exception ex) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity.status(status)
        .body(
            ErrorResponseDTO.of(
                status, "Unknown authentication error occurred: " + ex.getMessage()));
  }
}
