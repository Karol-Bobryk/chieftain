package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;

public class SubtaskNotEligible extends RuntimeException {
  public SubtaskNotEligible(@Nullable String msg) {
    super(msg);
  }
}
