package com.chieftain.controllers.group.dto;

import com.chieftain.enums.GroupUserPermission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddGroupMemberRequestDTO {

    List<UUID> memberIds;

    List<GroupUserPermission> permissions;
}
