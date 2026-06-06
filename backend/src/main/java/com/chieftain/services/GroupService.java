package com.chieftain.services;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.models.*;
import com.chieftain.repositories.GroupPrivilegeRepository;
import com.chieftain.repositories.GroupRepository;
import com.chieftain.repositories.GroupUserPermissionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

  public List<GroupUserPermissionEntity> getPermissionEntities(
      Collection<GroupUserPermission> permissions) {
    return groupUserPermissionRepository.findAllByPermissionNameIn(permissions);
  }

  public void addGroupMembers(GroupEntity group, List<UserEntity> newMembers, List<GroupUserPermission> permissions){

      List<UserEntity> membersToAdd = newMembers.stream().
              filter(checkMember -> !group.getMembers().contains(checkMember)).toList();

      group.getMembers().addAll(membersToAdd);
      groupRepository.save(group);

      addPrivilegesForMultipleUsers(group, membersToAdd, permissions);
  }

  public GroupEntity getByIdAndOrganization(UUID groupId, OrganizationEntity organization){
    GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if(!group.getOrganization().getPkOrganizationId().equals(organization.getPkOrganizationId())){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return group;
  }

  public void removeMember(UUID groupId, UUID userId){
    GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    group.getMembers().removeIf(id->id.getPkUserId().equals(userId));;
    groupPrivilegeRepository.deleteByUserPkUserIdAndGroupId(userId, groupId);
    groupRepository.save(group);

  }

}
