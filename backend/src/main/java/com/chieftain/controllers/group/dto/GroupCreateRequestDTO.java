package com.chieftain.controllers.group.dto;

import com.chieftain.enums.GroupUserPermission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupCreateRequestDTO {
  @NotBlank(message = "Group name cannot be blank")
  String name;

  List<UUID> members;

  List<GroupUserPermission> roles;

  @JsonIgnore
  @AssertTrue(message = "Members and roles must be provided together or both omitted")
  public boolean isMembersAndRolesValid() {
    boolean membersPresent = members != null && !members.isEmpty();
    boolean rolesPresent = roles != null && !roles.isEmpty();

    return (membersPresent && rolesPresent) || (!membersPresent && !rolesPresent);
  }
}
