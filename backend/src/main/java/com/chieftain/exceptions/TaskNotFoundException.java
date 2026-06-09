package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;

public class TaskNotFoundException extends RuntimeException {
  public TaskNotFoundException(@Nullable String msg) {
    super(msg);
  }
}
