package com.chieftain.repositories;

import com.chieftain.models.GroupEntity;
import com.chieftain.models.UserEntity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
  Page<GroupEntity> findAllByMembersContaining(UserEntity member, Pageable pageable);

  void deleteById(UUID groupId);

  Optional<GroupEntity> findByShareToken(String shareToken);
}
