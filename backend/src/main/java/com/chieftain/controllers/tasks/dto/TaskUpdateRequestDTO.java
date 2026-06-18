package com.chieftain.controllers.group.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateRequestDTO {
    @Nullable
    String name;
    @Nullable
    String description;
    @Nullable
    LocalDateTime startedAt;
    @Nullable
    LocalDateTime deadline;
    @Nullable
    LocalDateTime doneAt;
    @Nullable
    String statusId;
}
