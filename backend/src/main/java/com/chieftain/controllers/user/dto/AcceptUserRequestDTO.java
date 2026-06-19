package com.chieftain.controllers.user.dto;

import com.chieftain.enums.SystemRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptUserRequestDTO {
  @NotNull
  private SystemRole role;
}
