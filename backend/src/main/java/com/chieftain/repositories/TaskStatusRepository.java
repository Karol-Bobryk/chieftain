package com.chieftain.repositories;

import com.chieftain.enums.TaskStatus;
import com.chieftain.models.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Integer> {
  TaskStatusEntity findByStatusName(TaskStatus statusName);
}
