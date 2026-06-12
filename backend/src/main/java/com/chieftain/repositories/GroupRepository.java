package com.chieftain.repositories;

import com.chieftain.models.GroupEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
  void deleteById(UUID groupId);
}
