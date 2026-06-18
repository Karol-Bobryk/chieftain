package com.chieftain.controllers.user.dto;

import com.chieftain.models.UserEntity;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDisplayDTO {
  UUID userId;
  String name;
  String surname;

  public static UserDisplayDTO ofEntity(UserEntity user) {
    return new UserDisplayDTO(user.getPkUserId(), user.getName(), user.getSurname());
  }
}
