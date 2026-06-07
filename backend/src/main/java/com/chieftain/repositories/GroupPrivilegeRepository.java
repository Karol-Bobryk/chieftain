package com.chieftain.repositories;

import com.chieftain.models.GroupPrivilegeEntity;
import com.chieftain.models.GroupPrivilegeId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPrivilegeRepository
    extends JpaRepository<GroupPrivilegeEntity, GroupPrivilegeId> {
  List<GroupPrivilegeEntity> findAllById(GroupPrivilegeId id);
}
