package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class UserIsNotAcceptedException extends AuthenticationException {
  public UserIsNotAcceptedException(@Nullable String msg) {
    super(msg);
  }
}
