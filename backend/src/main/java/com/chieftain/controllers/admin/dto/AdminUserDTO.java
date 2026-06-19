package com.chieftain.controllers.admin.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
  private UUID userId;
  private String name;
  private String surname;
  private String emailAddress;
  private boolean blocked;
}