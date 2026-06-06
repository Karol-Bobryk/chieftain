package com.chieftain.controllers.user;

import com.chieftain.controllers.auth.dto.ErrorResponseDTO;
import com.chieftain.exceptions.RoleNotFoundException;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.chieftain.controllers.user")
public class UserExceptionHandler {
  @ExceptionHandler(RoleNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleRoleNotFound(RoleNotFoundException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(ErrorResponseDTO.of(status, ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status).body(ErrorResponseDTO.of(status, ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
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
  public ResponseEntity<ErrorResponseDTO> handleGeneralUserError(Exception ex) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity.status(status)
        .body(
            ErrorResponseDTO.of(
                status, "Unknown user operation error occurred: " + ex.getMessage()));
  }
}
