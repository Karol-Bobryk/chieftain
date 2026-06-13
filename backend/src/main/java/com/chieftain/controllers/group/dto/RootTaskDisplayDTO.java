package com.chieftain.controllers.group.dto;

import com.chieftain.controllers.user.dto.GroupDisplayDTO;
import com.chieftain.controllers.user.dto.UserDisplayDTO;
import com.chieftain.enums.TaskStatus;
import com.chieftain.models.TaskEntity;
import com.chieftain.models.TaskStatusEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class RootTaskDisplayDTO {

  @Nonnull private UUID taskId;

  private UserDisplayDTO creatorUser;

  @Nonnull private GroupDisplayDTO group;

  private String name;

  @Nonnull private LocalDateTime startedAt;

  @Nonnull private LocalDateTime createdAt;

  @Nullable private LocalDateTime doneAt;

  @Nonnull private LocalDateTime deadline;

  @Nonnull private TaskStatus status;

  @Nullable private String description;

  private List<UserDisplayDTO> assignees = new ArrayList<>();

  public static RootTaskDisplayDTO ofEntity(TaskEntity task) {
    RootTaskDisplayDTO dto = new RootTaskDisplayDTO();

    dto.setTaskId(task.getId());
    dto.setCreatorUser(UserDisplayDTO.ofEntity(task.getCreatorUser()));
    dto.setName(task.getName());
    dto.setStartedAt(task.getStartedAt());
    dto.setCreatedAt(task.getCreatedAt());
    dto.setDoneAt(task.getDoneAt());
    dto.setDeadline(task.getDeadline());
    dto.setStatus(task.getStatus().getStatusName());
    dto.setDescription(task.getDescription());
    dto.setGroup(GroupDisplayDTO.fromGroupEntity(task.getGroup()));
    dto.setAssignees(task.getAssignees().stream().map(UserDisplayDTO::ofEntity).toList());

    return dto;
  }
}
