package com.chieftain.controllers.user.dto;

import com.chieftain.enums.SystemRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptUserRequestDTO {
  private SystemRole role;
}
