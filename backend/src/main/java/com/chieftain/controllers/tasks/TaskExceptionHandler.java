package com.chieftain.controllers.tasks;

import com.chieftain.controllers.auth.dto.ErrorResponseDTO;
import com.chieftain.exceptions.GroupNotFoundException;
import com.chieftain.exceptions.SubtaskNotEligible;
import com.chieftain.exceptions.TaskNotFoundException;
import com.chieftain.exceptions.UserNotEligible;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.chieftain.controllers.tasks")
public class TaskExceptionHandler {

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

  @ExceptionHandler(SubtaskNotEligible.class)
  public ResponseEntity<ErrorResponseDTO> handleSubtaskNotEligible(SubtaskNotEligible ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "Request field validation failed " + ex.getMessage()));
  }

  @ExceptionHandler(UserNotEligible.class)
  public ResponseEntity<ErrorResponseDTO> handleUserNotEligible(UserNotEligible ex) {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status)
        .body(
            ErrorResponseDTO.of(
                status, "User is not allowed to perform this action: " + ex.getMessage()));
  }

  @ExceptionHandler({GroupNotFoundException.class, TaskNotFoundException.class})
  public ResponseEntity<ErrorResponseDTO> handleNotFound(Exception ex) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status)
        .body(
            ErrorResponseDTO.of(
                status,
                "Request field validation failed " + Objects.requireNonNull(ex.getMessage())));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneralAuthError(Exception ex) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity.status(status)
        .body(ErrorResponseDTO.of(status, "Unknown task error occurred: " + ex.getMessage()));
  }
}
