package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class UserSecretNotProvidedException extends AuthenticationException {
  public UserSecretNotProvidedException(@Nullable String msg) {
    super(msg);
  }
}
