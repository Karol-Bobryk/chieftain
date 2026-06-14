package com.chieftain.controllers.organization.dto;

import com.chieftain.enums.SystemRole;
import com.chieftain.models.UserEntity;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationUserResponseDTO {
  UUID userId;
  String name;
  String surname;
  String emailAddress;
  String jobTitle;
  SystemRole role;

  public static OrganizationUserResponseDTO fromUserEntity(UserEntity user) {
    return new OrganizationUserResponseDTO(
        user.getPkUserId(),
        user.getName(),
        user.getSurname(),
        user.getEmailAddress(),
        user.getJobTitle(),
        user.getRole().getRoleName());
  }
}
