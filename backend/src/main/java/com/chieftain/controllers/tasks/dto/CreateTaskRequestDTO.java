package com.chieftain.controllers.tasks.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTaskRequestDTO {
  @Nullable UUID parentTaskId;

  @Nonnull UUID groupId;

  @NotBlank(message = "Task name cannot be null")
  String name;

  @Nullable String description;

  @Nonnull Instant startedAt;

  @Nonnull @Future Instant deadline;

  @Nullable List<UUID> assignees;
}
