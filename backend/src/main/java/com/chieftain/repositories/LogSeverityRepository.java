package com.chieftain.repositories;

import com.chieftain.models.LogSeverityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogSeverityRepository extends JpaRepository<LogSeverityEntity, Integer> {}
