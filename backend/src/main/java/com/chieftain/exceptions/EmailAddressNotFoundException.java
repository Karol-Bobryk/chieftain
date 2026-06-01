package com.chieftain.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class EmailAddressNotFoundException extends AuthenticationException {
  public EmailAddressNotFoundException(@Nullable String msg) {
    super(msg);
  }
}
