package com.chieftain.repositories;

import com.chieftain.models.OrganizationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationLogRepository extends JpaRepository<OrganizationLogEntity, Long> {
}
