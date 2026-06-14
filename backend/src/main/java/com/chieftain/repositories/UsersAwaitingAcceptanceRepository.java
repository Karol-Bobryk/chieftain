package com.chieftain.repositories;

import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.models.UsersAwaitingAcceptanceEntity;
import com.chieftain.models.UsersAwaitingAcceptanceId;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersAwaitingAcceptanceRepository
    extends JpaRepository<UsersAwaitingAcceptanceEntity, UsersAwaitingAcceptanceId> {
  Optional<UsersAwaitingAcceptanceEntity> findByUser(UserEntity user);

  Page<UsersAwaitingAcceptanceEntity> findAllByOrganization(
      OrganizationEntity organization, Pageable pageable);

  boolean existsByOrganizationAndUser(OrganizationEntity organization, UserEntity user);
}
