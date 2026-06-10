package com.chieftain.services;

import com.chieftain.exceptions.InvalidOrganizationJoinToken;
import com.chieftain.exceptions.OrganizationNotFoundException;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.repositories.OrganizationRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
  //TODO: add logs
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

  public OrganizationEntity createByName(String organizationName) {
    OrganizationEntity organization = new OrganizationEntity();
    organization.setName(organizationName);
    organization.setBlocked(false);
    return organizationRepository.save(organization);
  }

  public OrganizationEntity getOrganizationById(UUID organizationId) {
    return organizationRepository
        .findById(organizationId)
        .orElseThrow(
            () -> new OrganizationNotFoundException("No organization with id: " + organizationId));
  }
}
