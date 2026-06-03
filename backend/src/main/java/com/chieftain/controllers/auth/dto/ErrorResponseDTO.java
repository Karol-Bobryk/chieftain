package com.chieftain.controllers.auth.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponseDTO(String message, int errorCode, String errorName) {
  public static ErrorResponseDTO of(HttpStatus status, String message) {
    return new ErrorResponseDTO(message, status.value(), status.name());
  }
}
