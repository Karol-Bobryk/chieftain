package com.chieftain.repositories;

import com.chieftain.enums.LogSeverity;
import com.chieftain.models.LogSeverityEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogSeverityRepository extends JpaRepository<LogSeverityEntity, Integer> {
  Optional<LogSeverityEntity> findByLogSeverityName(LogSeverity severityName);
}
