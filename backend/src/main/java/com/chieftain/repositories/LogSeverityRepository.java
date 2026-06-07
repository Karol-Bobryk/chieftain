package com.chieftain.repositories;

import com.chieftain.enums.LogSeverity;
import com.chieftain.models.LogSeverityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogSeverityRepository extends JpaRepository<LogSeverityEntity, Integer> {
    Optional<LogSeverityEntity> findByLogSeverityName(LogSeverity severityName);
}
