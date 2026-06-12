package com.chieftain.controllers.group.dto;

import com.chieftain.enums.TaskStatus;
import com.chieftain.models.TaskEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupTaskResponseDTO {
  UUID taskId;
  String name;
  TaskStatus status;
  LocalDateTime deadline;
  LocalDateTime startedAt;
  List<UUID> assigneeIds;

  public static GroupTaskResponseDTO fromTaskEntity(TaskEntity task) {
    return new GroupTaskResponseDTO(
        task.getId(),
        task.getName(),
        task.getStatus().getStatusName(),
        task.getDeadline(),
        task.getStartedAt(),
        task.getAssignees().stream().map(u -> u.getPkUserId()).toList());
  }
}
