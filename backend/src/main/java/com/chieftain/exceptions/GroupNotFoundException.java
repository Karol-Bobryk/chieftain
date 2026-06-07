package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;

public class GroupNotFoundException extends RuntimeException {
  public GroupNotFoundException(@Nullable String msg) {
    super(msg);
  }
}
