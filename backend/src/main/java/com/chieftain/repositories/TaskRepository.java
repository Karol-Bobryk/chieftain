package com.chieftain.repositories;

import com.chieftain.models.TaskEntity;
import com.chieftain.models.UserEntity;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
  boolean existsById(@Nonnull UUID id);

  List<TaskEntity> findByGroupIdAndAssigneesContaining(UUID groupId, UserEntity user);
}
