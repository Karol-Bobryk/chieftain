package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class UserIsBarredException extends AuthenticationException {
  public UserIsBarredException(@Nullable String msg) {
    super(msg);
  }
}
