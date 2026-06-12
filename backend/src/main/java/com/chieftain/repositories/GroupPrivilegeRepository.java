package com.chieftain.repositories;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.models.*;
import com.chieftain.models.GroupPrivilegeEntity;
import com.chieftain.models.GroupPrivilegeId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupPrivilegeRepository
    extends JpaRepository<GroupPrivilegeEntity, GroupPrivilegeId> {
  List<GroupPrivilegeEntity> findAllById(GroupPrivilegeId id);

  boolean existsByGroupAndUserAndPermission(
      GroupEntity group, UserEntity user, GroupUserPermissionEntity permission);

  @Modifying
  @Query(
      "DELETE FROM GroupPrivilegeEntity g WHERE g.user.pkUserId =:userId AND g.group.id =:groupId")
  void deleteByUserPkUserIdAndGroupId(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

  @Query(
      "SELECT g FROM GroupPrivilegeEntity g WHERE g.group.id = :groupId"
          + " AND g.user.pkUserId = :userId AND  g.permission.permissionName = :permission")
  Optional<GroupPrivilegeEntity> findPermission(
      @Param("groupId") UUID groupId,
      @Param("userId") UUID userId,
      @Param("permission") GroupUserPermission groupUserPermission);

  void deleteAllByGroupId(UUID groupId);
}
