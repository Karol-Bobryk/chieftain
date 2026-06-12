package com.chieftain.services;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.enums.LogSeverity;
import com.chieftain.events.GroupPrivilegeLogEvent;
import com.chieftain.exceptions.GroupNotFoundException;
import com.chieftain.models.*;
import com.chieftain.repositories.*;
import jakarta.transaction.Transactional;
import java.util.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GroupService {
  private final GroupRepository groupRepository;
  private final GroupPrivilegeRepository groupPrivilegeRepository;
  private final GroupUserPermissionRepository groupUserPermissionRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final TaskService taskService;

  public GroupService(
      GroupRepository groupRepository,
      GroupPrivilegeRepository groupPrivilegeRepository,
      GroupUserPermissionRepository groupUserPermissionRepository,
      ApplicationEventPublisher applicationEventPublisher,
      TaskRepository taskRepository,
      UserRepository userRepository,
      TaskService taskService) {
    this.groupRepository = groupRepository;
    this.groupPrivilegeRepository = groupPrivilegeRepository;
    this.groupUserPermissionRepository = groupUserPermissionRepository;
    this.applicationEventPublisher = applicationEventPublisher;
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
    this.taskService = taskService;
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

    applicationEventPublisher.publishEvent(
        new GroupPrivilegeLogEvent(
            group.getId(),
            user.getPkUserId(),
            LogSeverity.INFO,
            "PRIVILEGES_GRANTED",
            "User "
                + user.getEmailAddress()
                + " received new permissions in group: "
                + group.getName()));

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

      applicationEventPublisher.publishEvent(
          new GroupPrivilegeLogEvent(
              group.getId(),
              user.getPkUserId(),
              LogSeverity.INFO,
              "PRIVILEGES_GRANTED",
              "User "
                  + user.getEmailAddress()
                  + " received new permissions in group: "
                  + group.getName()));
    }

    privilegeEntities = groupPrivilegeRepository.saveAll(privilegeEntities);
    return privilegeEntities;
  }

  public boolean isUserEligible(
      UserEntity user, GroupEntity group, GroupUserPermission groupPermission) {
    return groupPrivilegeRepository.existsByGroupAndUserAndPermission(
        group, user, getPermissionEntity(groupPermission));
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

  public void addGroupMembers(
      GroupEntity group,
      UUID requesterId,
      List<UserEntity> newMembers,
      List<GroupUserPermission> permissions) {

    List<UserEntity> membersToAdd =
        newMembers.stream()
            .filter(checkMember -> !group.getMembers().contains(checkMember))
            .toList();
    groupPrivilegeRepository
        .findPermission(group.getId(), requesterId, GroupUserPermission.ADD_USER_TO_GROUP)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "User has insufficient permissions"));

    group.getMembers().addAll(membersToAdd);
    groupRepository.save(group);

    addPrivilegesForMultipleUsers(group, membersToAdd, permissions);
  }

  public GroupEntity getByIdAndOrganization(UUID groupId, OrganizationEntity organization) {
    GroupEntity group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("No group with id " + groupId));
    if (!group.getOrganization().getPkOrganizationId().equals(organization.getPkOrganizationId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return group;
  }

  public void removeMember(UUID groupId, UUID userId, UUID requesterId) {
    GroupEntity group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("No group with id " + groupId));

    groupPrivilegeRepository
        .findPermission(groupId, requesterId, GroupUserPermission.REMOVE_USER_FROM_GROUP)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "User has insufficient permissions"));

    boolean wasRemoved = group.getMembers().removeIf(id -> id.getPkUserId().equals(userId));
    if (!wasRemoved) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    groupPrivilegeRepository.deleteByUserPkUserIdAndGroupId(userId, groupId);
    groupRepository.save(group);
  }

  public boolean isUserNotInGroup(GroupEntity group, UserEntity user) {
    return !group.getMembers().contains(user);
  }

  public void removeDeletedMemberFromAssignees(UUID groupId, UUID userId) {
    UserEntity user = userRepository.findByPkUserId(userId).orElseThrow();
    List<TaskEntity> tasks = taskRepository.findByGroupIdAndAssigneesContaining(groupId, user);

    for (TaskEntity task : tasks) {
      task.getAssignees().remove(user);
    }
  }

  public void deleteGroup(UUID groupId, UUID requesterId) {
    groupPrivilegeRepository
        .findPermission(groupId, requesterId, GroupUserPermission.REMOVE_USER_FROM_GROUP)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "User has insufficient permissions"));
    GroupEntity group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("No group with id " + groupId));

    List<TaskEntity> tasks = taskRepository.findByGroupId(groupId);

    for (TaskEntity task : tasks) {
      taskService.delete(task);
    }
    groupPrivilegeRepository.deleteAllByGroupId(groupId);
    groupRepository.deleteById(groupId);
  }

}
