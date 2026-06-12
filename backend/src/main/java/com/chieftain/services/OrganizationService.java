package com.chieftain.services;

import com.chieftain.controllers.organization.dto.OrganizationUserResponseDTO;
import com.chieftain.enums.SystemRole;
import com.chieftain.exceptions.InvalidOrganizationJoinToken;
import com.chieftain.exceptions.OrganizationNotFoundException;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.RoleEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.GroupPrivilegeRepository;
import com.chieftain.repositories.OrganizationRepository;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

import com.chieftain.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static com.chieftain.enums.SystemRole.OWNER;
import static com.chieftain.enums.SystemRole.SITE_ADMIN;

@Service
public class OrganizationService {
  // TODO: add logs
  private final OrganizationRepository organizationRepository;
  private final GroupPrivilegeRepository groupPrivilegeRepository;
  private final UserRepository userRepository;

  public OrganizationService(OrganizationRepository organizationRepository, GroupPrivilegeRepository groupPrivilegeRepository, UserRepository userRepository) {
    this.organizationRepository = organizationRepository;
    this.groupPrivilegeRepository = groupPrivilegeRepository;
    this.userRepository = userRepository;
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

  public Page<UserEntity> getUsersInOrganization(OrganizationEntity organization, Pageable pageable){
    return userRepository.findByOrganization(organization, pageable);
  }

}
