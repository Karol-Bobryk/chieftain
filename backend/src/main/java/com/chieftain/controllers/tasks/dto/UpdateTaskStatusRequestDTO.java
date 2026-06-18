package com.chieftain.controllers.tasks.dto;

import com.chieftain.enums.TaskStatus;
import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTaskStatusRequestDTO {
  @Nonnull TaskStatus status;
}
