package com.chieftain.services;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.exceptions.GroupNotFoundException;
import com.chieftain.models.*;
import com.chieftain.repositories.GroupPrivilegeRepository;
import com.chieftain.repositories.GroupRepository;
import com.chieftain.repositories.GroupUserPermissionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
  private final GroupRepository groupRepository;
  private final GroupPrivilegeRepository groupPrivilegeRepository;
  private final GroupUserPermissionRepository groupUserPermissionRepository;

  public GroupService(
      GroupRepository groupRepository,
      GroupPrivilegeRepository groupPrivilegeRepository,
      GroupUserPermissionRepository groupUserPermissionRepository) {
    this.groupRepository = groupRepository;
    this.groupPrivilegeRepository = groupPrivilegeRepository;
    this.groupUserPermissionRepository = groupUserPermissionRepository;
  }

  public GroupEntity getGroupById(UUID groupId) {
    return groupRepository
        .findById(groupId)
        .orElseThrow(() -> new GroupNotFoundException("No group with id " + groupId));
  }

  public GroupEntity save(GroupEntity groupEntity) {
    return groupRepository.save(groupEntity);
  }

  @Transactional
  public List<GroupPrivilegeEntity> addPrivilegesForUser(
      GroupEntity group, UserEntity user, Collection<GroupUserPermission> permissions) {

    List<GroupPrivilegeEntity> entities = new ArrayList<>();

    for (var permission : getPermissionEntities(permissions)) {
      GroupPrivilegeEntity privilegeEntity = new GroupPrivilegeEntity();
      privilegeEntity.setGroup(group);
      privilegeEntity.setUser(user);
      privilegeEntity.setPermission(permission);
      entities.add(privilegeEntity);
    }

    return groupPrivilegeRepository.saveAll(entities);
  }

  public List<GroupPrivilegeEntity> addPrivilegesForMultipleUsers(
      GroupEntity group, List<UserEntity> users, Collection<GroupUserPermission> permissions) {

    List<GroupPrivilegeEntity> privilegeEntities = new ArrayList<>();

    List<GroupUserPermissionEntity> grantedPermissions = getPermissionEntities(permissions);

    for (var user : users) {
      for (var permission : grantedPermissions) {
        GroupPrivilegeEntity privilege = new GroupPrivilegeEntity();
        privilege.setGroup(group);
        privilege.setUser(user);
        privilege.setPermission(permission);
        privilegeEntities.add(privilege);
      }
    }

    privilegeEntities = groupPrivilegeRepository.saveAll(privilegeEntities);
    return privilegeEntities;
  }

  public boolean isUserEligible(
      UserEntity user, GroupEntity group, GroupUserPermission groupPermission) {
    return groupPrivilegeRepository.existsById(
        new GroupPrivilegeId(
            user.getPkUserId(),
            group.getId(),
            getPermissionEntity(groupPermission).getPermissionId()));
  }

  public List<GroupPrivilegeEntity> getAllUserGroupPrivileges(GroupPrivilegeId gpId) {
    return groupPrivilegeRepository.findAllById(gpId);
  }

  public GroupUserPermissionEntity getPermissionEntity(GroupUserPermission permission) {
    return groupUserPermissionRepository.findByPermissionName(permission);
  }

  public List<GroupUserPermissionEntity> getPermissionEntities(
      Collection<GroupUserPermission> permissions) {
    return groupUserPermissionRepository.findAllByPermissionNameIn(permissions);
  }

  public boolean isUserInGroup(GroupEntity group, UserEntity user){
     return group.getMembers().contains(user);
  }
}
