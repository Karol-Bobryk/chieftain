package com.chieftain.exceptions;

import jakarta.annotation.Nullable;
import javax.naming.AuthenticationException;

public class InvalidUserSecretProvidedException extends AuthenticationException {
  public InvalidUserSecretProvidedException(@Nullable String message) {
    super(message);
  }
}
