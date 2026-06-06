package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class UserNotFoundInQueueException extends RuntimeException{
  public UserNotFoundInQueueException(@Nullable String msg) {
    super(msg);
  }
}
