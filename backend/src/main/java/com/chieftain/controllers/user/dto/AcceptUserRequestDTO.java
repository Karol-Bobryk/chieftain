package com.chieftain.controllers.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptUserRequestDTO {
  @NotBlank(message = "Role cannot be blank")
  private String role;
}
