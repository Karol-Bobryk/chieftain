package com.chieftain.controllers.group;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.group.dto.AddGroupMemberRequestDTO;
import com.chieftain.controllers.group.dto.GroupCreateRequestDTO;
import com.chieftain.controllers.group.dto.GroupCreateResponseDTO;
import com.chieftain.enums.GroupUserPermission;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.GroupRepository;
import com.chieftain.services.GroupService;
import com.chieftain.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/groups")
public class GroupController {
  private final GroupService groupService;
  private final UserService userService;
  private final GroupRepository groupRepository;

  public GroupController(GroupService groupService, UserService userService, GroupRepository groupRepository) {
    this.groupService = groupService;
    this.userService = userService;
    this.groupRepository = groupRepository;
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

    GroupCreateResponseDTO response = new GroupCreateResponseDTO(groupEntity.getId());
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }


  @PutMapping("/{groupId}/members")
  @Transactional
  public ResponseEntity<Void> addGroupMembers(
          @PathVariable UUID groupId,
          @Valid @RequestBody AddGroupMemberRequestDTO request,
          @AuthenticationPrincipal CustomUserDetails userDetails){

     GroupEntity group = groupService.getByIdAndOrganization(groupId, userDetails.getOrganization());

    List<UserEntity> newMembers = userService.getUsersByIds(request.getMemberIds());
      boolean differentOrganizationMembers = newMembers.stream()
              .anyMatch(d -> !d.getOrganization().getPkOrganizationId()
                      .equals(group.getOrganization().getPkOrganizationId()));
      if(differentOrganizationMembers){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add users from different organization");
      }
    groupService.addGroupMembers(group, newMembers, request.getPermissions());

    return ResponseEntity.noContent().build();

  }

  @DeleteMapping("/{groupId}/members/{userId}")
  @Transactional
  public ResponseEntity<Void> addGroupMembers(
          @PathVariable UUID groupId,
          @PathVariable UUID userId,
          @AuthenticationPrincipal CustomUserDetails userDetails) {

    groupService.getByIdAndOrganization(groupId, userDetails.getOrganization());
    groupService.removeMember(groupId,userId);

    return ResponseEntity.noContent().build();

  }
}
