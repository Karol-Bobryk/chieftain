package com.chieftain.repositories;

import com.chieftain.models.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPrivilegeRepository
    extends JpaRepository<GroupPrivilegeEntity, GroupPrivilegeId> {
  List<GroupPrivilegeEntity> findAllById(GroupPrivilegeId id);

  boolean existsByGroupAndUserAndPermission(
      GroupEntity group, UserEntity user, GroupUserPermissionEntity permission);
}
