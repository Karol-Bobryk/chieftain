package com.chieftain.repositories;

import com.chieftain.models.TaskLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskLogRepository extends JpaRepository<TaskLogEntity, Long> {
}
