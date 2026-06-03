package com.chieftain.services;

import com.chieftain.exceptions.InvalidOrganizationJoinToken;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
  private final OrganizationRepository organizationRepository;

  public OrganizationService(OrganizationRepository organizationRepository) {
    this.organizationRepository = organizationRepository;
  }

  public OrganizationEntity getByToken(String joinToken) {
    return organizationRepository
        .findByJoinToken(joinToken)
        .orElseThrow(
            () -> new InvalidOrganizationJoinToken("No organization with token " + joinToken));
  }

  public void save(OrganizationEntity organizationEntity) {
    organizationRepository.save(organizationEntity);
  }
}
