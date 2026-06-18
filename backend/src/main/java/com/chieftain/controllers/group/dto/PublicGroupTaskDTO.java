package com.chieftain.controllers.group.dto;

import com.chieftain.enums.TaskStatus;
import com.chieftain.models.TaskEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicGroupTaskDTO {
    String name;
    String description;
    TaskStatus status;
    LocalDateTime startedAt;
    LocalDateTime deadline;
    List<String> assignees;


    public static PublicGroupTaskDTO ofEntity(TaskEntity task){
        return new PublicGroupTaskDTO(
                task.getName(),
                task.getDescription(),
                task.getStatus().getStatusName(),
                task.getStartedAt(),
                task.getDeadline(),
                task.getAssignees()
                        .stream().map(user -> user.getName() + " " + user.getSurname()).toList()
        );
    }
}
