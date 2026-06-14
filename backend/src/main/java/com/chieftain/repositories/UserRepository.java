package com.chieftain.repositories;

import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.dto.SecretHashOnly;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);

    Optional<UserEntity> findByPkUserId(UUID pkUserId);

    List<UserEntity> findAllByPkUserIdIn(List<UUID> pkUserIds);

    SecretHashOnly findSecretHashByEmailAddress(String emailAddress);

    Page<UserEntity> findByOrganization(OrganizationEntity organization, Pageable pageable);
}
