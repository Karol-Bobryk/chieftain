package com.chieftain.repositories;

import com.chieftain.models.TaskEntity;
import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
  boolean existsById(@Nonnull UUID id);
}
