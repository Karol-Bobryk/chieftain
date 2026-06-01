package com.chieftain.repositories;

import com.chieftain.models.UserEntity;
import java.util.Optional;
import java.util.UUID;

import com.chieftain.repositories.dto.SecretHashOnly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmailAddress(String emailAddress);
  boolean existsByEmailAddress(String emailAddress);
  SecretHashOnly findSecretHashByEmailAddress(String emailAddress);

}
