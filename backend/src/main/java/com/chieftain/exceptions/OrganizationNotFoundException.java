package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;

public class OrganizationNotFoundException extends RuntimeException {
  public OrganizationNotFoundException(@Nullable String msg) {
    super(msg);
  }
}
