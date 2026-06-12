package com.chieftain.controllers.group;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.group.dto.AddGroupMemberRequestDTO;
import com.chieftain.controllers.group.dto.GroupCreateRequestDTO;
import com.chieftain.controllers.group.dto.GroupCreateResponseDTO;
import com.chieftain.controllers.group.dto.GroupTaskResponseDTO;
import com.chieftain.enums.GroupUserPermission;
import com.chieftain.enums.LogSeverity;
import com.chieftain.events.GroupLogEvent;
import com.chieftain.events.GroupPrivilegeLogEvent;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.services.GroupService;
import com.chieftain.services.TaskService;
import com.chieftain.services.UserService;
import com.chieftain.services.UsersAwaitingAcceptanceService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
  private static final List<String> SORT_FIELDS = List.of("startedAt", "createdAt", "doneAt", "deadline");
  private final GroupService groupService;
  private final UserService userService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final UsersAwaitingAcceptanceService usersAwaitingAcceptanceService;
  private final TaskService taskService;


  public GroupController(
          GroupService groupService,
          UserService userService,
          ApplicationEventPublisher applicationEventPublisher,
          UsersAwaitingAcceptanceService usersAwaitingAcceptanceService, TaskService taskService) {
    this.groupService = groupService;
    this.userService = userService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.usersAwaitingAcceptanceService = usersAwaitingAcceptanceService;
    this.taskService = taskService;
  }

  @PutMapping("/create")
  @Transactional
  public ResponseEntity<GroupCreateResponseDTO> createGroup(
      @Valid @RequestBody GroupCreateRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    List<UserEntity> members = new ArrayList<>();

    if (request.getMembers() != null) {
      members = userService.getUsersByIds(request.getMembers());
      for (UserEntity member : members) {
        if (usersAwaitingAcceptanceService.isUserAwaiting(member.getOrganization(), member)) {
          throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "User " + member.getEmailAddress() + " is awaiting acceptance in organization");
        }
      }
    }

    UserEntity groupOwner = userService.getUserById(userDetails.getUserId());

    members.add(groupOwner);

    GroupEntity groupEntity = new GroupEntity();
    groupEntity.setName(request.getName());
    groupEntity.setMembers(members);
    groupEntity.setOrganization(userDetails.getOrganization());

    groupEntity = groupService.save(groupEntity);

    // Adding all capabilities for the group owner
    groupService.addPrivilegesForUser(
        groupEntity, groupOwner, List.of(GroupUserPermission.values()));

    // Adding all capabilities for the rest of the group
    groupService.addPrivilegesForMultipleUsers(
        groupEntity,
        members.stream().filter(e -> !e.equals(groupOwner)).toList(),
        request.getRoles());

    applicationEventPublisher.publishEvent(
        new GroupLogEvent(
            groupEntity.getId(),
            LogSeverity.INFO,
            "GROUP_CREATED",
            "Group '"
                + groupEntity.getName()
                + "' was created by: "
                + groupOwner.getEmailAddress()));

    applicationEventPublisher.publishEvent(
        new GroupPrivilegeLogEvent(
            groupEntity.getId(),
            groupOwner.getPkUserId(),
            LogSeverity.INFO,
            "GROUP_OWNER_PRIVILEGES_GRANTED",
            "User "
                + groupOwner.getEmailAddress()
                + " received full owner permissions for the group"));

    GroupCreateResponseDTO response = new GroupCreateResponseDTO(groupEntity.getId());
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/{groupId}/members")
  @Transactional
  public ResponseEntity<Void> addGroupMembers(
      @PathVariable UUID groupId,
      @Valid @RequestBody AddGroupMemberRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    GroupEntity group = groupService.getByIdAndOrganization(groupId, userDetails.getOrganization());

    List<UserEntity> newMembers = userService.getUsersByIds(request.getMemberIds());
    boolean differentOrganizationMembers =
        newMembers.stream()
            .anyMatch(
                d ->
                    !d.getOrganization()
                        .getPkOrganizationId()
                        .equals(group.getOrganization().getPkOrganizationId()));
    if (differentOrganizationMembers) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Cannot add users from different organization");
    }

    for (UserEntity member : newMembers) {
      if (usersAwaitingAcceptanceService.isUserAwaiting(member.getOrganization(), member)) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "User " + member.getEmailAddress() + " is awaiting acceptance in organization");
      }
    }

    groupService.addGroupMembers(
        group, userDetails.getUserId(), newMembers, request.getPermissions());

    String addedMembers =
        newMembers.stream().map(user -> user.getEmailAddress()).collect(Collectors.joining(", "));
    applicationEventPublisher.publishEvent(
        new GroupLogEvent(
            group.getId(),
            LogSeverity.INFO,
            "GROUP_MEMBER_ADDED",
            "User: " + addedMembers + " added to group"));

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{groupId}/members/{userId}")
  @Transactional
  public ResponseEntity<Void> removeGroupMember(
      @PathVariable UUID groupId,
      @PathVariable UUID userId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    groupService.getByIdAndOrganization(groupId, userDetails.getOrganization());
    groupService.removeMember(groupId, userId, userDetails.getUserId());
    groupService.removeDeletedMemberFromAssignees(groupId, userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{groupId}/delete")
  @Transactional
  public ResponseEntity<Void> deleteGroup(
      @PathVariable UUID groupId, @AuthenticationPrincipal CustomUserDetails userDetails) {

    groupService.deleteGroup(groupId, userDetails.getUserId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{groupId}/tasks")
  @Transactional
  public ResponseEntity<PagedModel<GroupTaskResponseDTO>> getGroupTasks(
          @PathVariable UUID groupId,
          @RequestParam(defaultValue = "deadline") String sortBy,
          @RequestParam(defaultValue = "asc") String direction,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @AuthenticationPrincipal CustomUserDetails userDetails){

    groupService.getByIdAndOrganization(groupId, userDetails.getOrganization());

    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<GroupTaskResponseDTO> tasksPage = taskService.getTasksByGroupId(groupId, pageable)
            .map(GroupTaskResponseDTO::fromTaskEntity);

    return ResponseEntity.ok(new PagedModel<>(tasksPage));
  }



}
