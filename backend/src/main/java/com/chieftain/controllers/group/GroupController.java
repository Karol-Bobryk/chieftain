package com.chieftain.controllers.group;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.group.dto.GroupCreateRequestDTO;
import com.chieftain.controllers.group.dto.GroupCreateResponseDTO;
import com.chieftain.enums.GroupUserPermission;
import com.chieftain.enums.LogSeverity;
import com.chieftain.events.GroupLogEvent;
import com.chieftain.events.GroupPrivilegeLogEvent;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.services.GroupService;
import com.chieftain.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
  private final GroupService groupService;
  private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

  public GroupController(
          GroupService groupService, UserService userService, ApplicationEventPublisher applicationEventPublisher) {
    this.groupService = groupService;
    this.userService = userService;
      this.applicationEventPublisher = applicationEventPublisher;
  }

  @PutMapping("/create")
  @Transactional
  public ResponseEntity<GroupCreateResponseDTO> createGroup(
      @Valid @RequestBody GroupCreateRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    List<UserEntity> members = new ArrayList<>();

    if (request.getMembers() != null) {
      members.addAll(userService.getUsersByIds(request.getMembers()));
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
        "Group '" + groupEntity.getName() + "' was created by: " + groupOwner.getEmailAddress()));

    applicationEventPublisher.publishEvent( new GroupPrivilegeLogEvent(
        groupEntity.getId(),
        groupOwner.getPkUserId(),
        LogSeverity.INFO,
        "GROUP_OWNER_PRIVILEGES_GRANTED",
        "User " + groupOwner.getEmailAddress() + " received full owner permissions for the group"));

    GroupCreateResponseDTO response = new GroupCreateResponseDTO(groupEntity.getId());
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
