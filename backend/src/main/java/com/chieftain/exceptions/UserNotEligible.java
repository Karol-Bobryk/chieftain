package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;

public class UserNotEligible extends RuntimeException {
  public UserNotEligible(@Nullable String msg) {
    super(msg);
  }
}
