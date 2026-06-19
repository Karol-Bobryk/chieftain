package com.chieftain.controllers.tasks.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTaskRequestDTO {
  @Nullable UUID parentTaskId;

  @NotNull
  UUID groupId;

  @NotBlank(message = "Task name cannot be null")
  String name;

  @Nullable String description;

  @NotNull Instant startedAt;

  @NotNull @Future Instant deadline;

  @Nullable List<UUID> assignees;
}
