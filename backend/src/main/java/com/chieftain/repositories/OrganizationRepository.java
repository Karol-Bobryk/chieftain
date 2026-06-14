package com.chieftain.repositories;

import com.chieftain.models.OrganizationEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {
  Optional<OrganizationEntity> findByJoinToken(String joinToken);

  OrganizationEntity getOrganizationByPkOrganizationId(UUID organizationId);
}
