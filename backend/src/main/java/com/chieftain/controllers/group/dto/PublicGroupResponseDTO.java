package com.chieftain.controllers.group.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicGroupResponseDTO {
    String groupName;
    List<PublicGroupMemberDTO> members;
    List<PublicGroupTaskDTO> tasks;
}
