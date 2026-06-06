package com.chieftain.controllers.group.dto;

import com.chieftain.enums.GroupUserPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Member list cannot be blank")
    List<UUID> memberIds;

    @NotEmpty(message = "Permissions list cannot be empty")
    List<GroupUserPermission> permissions;
}
