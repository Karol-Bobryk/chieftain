package com.chieftain.exceptions;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenExpNotFoundException extends AuthenticationException {
  public RefreshTokenExpNotFoundException(String message) {
    super(message);
  }
}
