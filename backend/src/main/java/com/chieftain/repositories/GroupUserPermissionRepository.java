package com.chieftain.repositories;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.models.GroupUserPermissionEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUserPermissionRepository
    extends JpaRepository<GroupUserPermissionEntity, Integer> {
  List<GroupUserPermissionEntity> findAllByPermissionNameIn(
      Collection<GroupUserPermission> permissionNames);
}
