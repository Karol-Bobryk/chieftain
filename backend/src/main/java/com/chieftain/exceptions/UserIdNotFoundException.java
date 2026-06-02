package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class UserIdNotFoundException extends AuthenticationException {
  public UserIdNotFoundException(@Nullable String msg) {
    super(msg);
  }
}
