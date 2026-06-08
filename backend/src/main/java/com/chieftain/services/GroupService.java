package com.chieftain.services;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.enums.LogSeverity;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.GroupPrivilegeEntity;
import com.chieftain.models.GroupUserPermissionEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.GroupPrivilegeRepository;
import com.chieftain.repositories.GroupRepository;
import com.chieftain.repositories.GroupUserPermissionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
  private final GroupRepository groupRepository;
  private final GroupPrivilegeRepository groupPrivilegeRepository;
  private final GroupUserPermissionRepository groupUserPermissionRepository;
  private final LogService logService;

  public GroupService(
          GroupRepository groupRepository,
          GroupPrivilegeRepository groupPrivilegeRepository,
          GroupUserPermissionRepository groupUserPermissionRepository,
          LogService logService) {
    this.groupRepository = groupRepository;
    this.groupPrivilegeRepository = groupPrivilegeRepository;
    this.groupUserPermissionRepository = groupUserPermissionRepository;
    this.logService = logService;
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

    logService.logGroupPrivilegeAction(
            group,
            user,
            LogSeverity.INFO,
            "PRIVILEGES_GRANTED",
            "User " + user.getEmailAddress() + " received new permissions in group: " + group.getName()
    );

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

      logService.logGroupPrivilegeAction(
              group,
              user,
              LogSeverity.INFO,
              "PRIVILEGES_GRANTED",
              "User " + user.getEmailAddress() + " received new permissions in group: " + group.getName()
      );
    }

    privilegeEntities = groupPrivilegeRepository.saveAll(privilegeEntities);
    return privilegeEntities;
  }

  public List<GroupUserPermissionEntity> getPermissionEntities(
      Collection<GroupUserPermission> permissions) {
    return groupUserPermissionRepository.findAllByPermissionNameIn(permissions);
  }
}
