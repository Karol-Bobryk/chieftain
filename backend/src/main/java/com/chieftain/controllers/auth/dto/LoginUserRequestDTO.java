package com.chieftain.controllers.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserRequestDTO {
  private String emailAddress;
  private String password;
}
