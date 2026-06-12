package com.chieftain.controllers.user.dto;

import com.chieftain.models.GroupEntity;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDisplayDTO {
  @Nonnull UUID groupId;

  @NotBlank(message = "group name cannot be null")
  String name;

  @Nonnull List<UserDisplayDTO> members;

  public static GroupDisplayDTO fromGroupEntity(GroupEntity group) {
    GroupDisplayDTO groupDisplay = new GroupDisplayDTO();
    groupDisplay.setGroupId(group.getId());
    groupDisplay.setName(group.getName());
    groupDisplay.setMembers(
        group.getMembers().stream()
            .map((e) -> new UserDisplayDTO(e.getPkUserId(), e.getName(), e.getSurname()))
            .toList());

    return groupDisplay;
  }
}
