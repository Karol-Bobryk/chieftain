package com.chieftain.exceptions;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenJtiNotFoundException extends AuthenticationException {
  public RefreshTokenJtiNotFoundException(String message) {
    super(message);
  }
}
