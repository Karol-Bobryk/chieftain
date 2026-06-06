package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;

public class UserNotFoundInQueueException extends RuntimeException{
  public UserNotFoundInQueueException(@Nullable String msg) {
    super(msg);
  }
}
