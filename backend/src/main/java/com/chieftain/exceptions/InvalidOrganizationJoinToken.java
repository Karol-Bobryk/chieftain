package com.chieftain.exceptions;

public class InvalidOrganizationJoinToken extends RuntimeException {
  public InvalidOrganizationJoinToken(String message) {
    super(message);
  }
}
