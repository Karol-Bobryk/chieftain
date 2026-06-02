package com.chieftain.controllers.auth;

import com.chieftain.exceptions.EmailIsAlreadyTakenException;
import com.chieftain.exceptions.UserSecretNotProvidedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.chieftain.controllers.auth")
public class AuthExceptionHandler {
  // TODO: we should consider returning JSON body with a message field instead of this brutish raw
  // text

  @ExceptionHandler(EmailIsAlreadyTakenException.class)
  public ResponseEntity<String> handleEmailTaken() {
    return ResponseEntity.status(HttpStatus.CONFLICT).body("Email address is already in use");
  }

  @ExceptionHandler(UserSecretNotProvidedException.class)
  public ResponseEntity<String> handleSecretNotProvided() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Secret not provided");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralAuthError() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Unknown authentication error occurred");
  }
}
