package com.chieftain.controllers.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDTO {
  private String name;
  private String surname;
  private String emailAddress;
  private String password;
  private String organizationToken;
}
