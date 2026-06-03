package com.chieftain.controllers.auth;

import com.chieftain.controllers.auth.dto.ErrorResponseDTO;
import com.chieftain.exceptions.EmailAddressNotFoundException;
import com.chieftain.exceptions.EmailIsAlreadyTakenException;
import com.chieftain.exceptions.InvalidUserSecretProvidedException;
import com.chieftain.exceptions.UserSecretNotProvidedException;
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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneralAuthError(Exception ex) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity.status(status)
        .body(
            ErrorResponseDTO.of(
                status, "Unknown authentication error occurred: " + ex.getMessage()));
  }
}
