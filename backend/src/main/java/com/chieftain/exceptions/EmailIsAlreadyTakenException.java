package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class EmailIsAlreadyTakenException extends AuthenticationException {
  public EmailIsAlreadyTakenException(@Nullable String msg) {
    super(msg);
  }
}
