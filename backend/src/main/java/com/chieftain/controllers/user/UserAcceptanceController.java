package com.chieftain.controllers.user;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.user.dto.AcceptUserRequestDTO;
import com.chieftain.controllers.user.dto.GroupDisplayDTO;
import com.chieftain.controllers.user.dto.UserDisplayDTO;
import com.chieftain.enums.SystemRole;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.GroupRepository;
import com.chieftain.services.UserService;
import com.chieftain.services.UsersAwaitingAcceptanceService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserAcceptanceController {

  private final UserService userService;
  private final UsersAwaitingAcceptanceService usersAwaitingAcceptanceService;
  private final GroupRepository groupRepository;

  public UserAcceptanceController(
          UserService userService, UsersAwaitingAcceptanceService usersAwaitingAcceptanceService, GroupRepository groupRepository) {
    this.userService = userService;
    this.usersAwaitingAcceptanceService = usersAwaitingAcceptanceService;
    this.groupRepository = groupRepository;
  }

  @GetMapping("/groups")
  @Transactional
  public ResponseEntity<PagedModel<GroupDisplayDTO>> getUserGroups(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    UUID userId = userDetails.getUserId();
    Pageable pageable = PageRequest.of(page, size);
    UserEntity user = userService.getUserById(userId);

    Page<GroupEntity> groupPage = userService.getGroupsForUsers(user, pageable);
    Page<GroupDisplayDTO> groupDisplay = groupPage.map(GroupDisplayDTO::fromGroupEntity);

    return ResponseEntity.ok(new PagedModel<>(groupDisplay));
  }

  @PostMapping("/{id}/accept")
  @PreAuthorize("hasAnyAuthority('OWNER', 'TASK_MASTER')")
  public ResponseEntity<String> acceptUser(
      @PathVariable UUID id,
      @Valid @RequestBody AcceptUserRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    UserEntity userToAccept = userService.getUserById(id);
    if(!userToAccept.getOrganization().getPkOrganizationId().equals(userDetails.getOrganization().getPkOrganizationId())){
      throw  new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    userService.acceptUser(id, request.getRole(), userDetails.getRole());
    return new ResponseEntity<>("User accepted successfully", HttpStatus.OK);
  }

  @GetMapping("/awaiting-acceptance")
  @PreAuthorize("hasAnyAuthority('OWNER', 'TASK_MASTER')")
  public ResponseEntity<PagedModel<UserDisplayDTO>> getUsersAwaitingAcceptance(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Pageable pageable = PageRequest.of(page, size);

    Page<UserDisplayDTO> usersPage =
        usersAwaitingAcceptanceService
            .getUsersInQueue(userDetails.getOrganization(), pageable)
            .map(
                (e) ->
                    new UserDisplayDTO(
                        e.getId().getUserId(), e.getUser().getName(), e.getUser().getSurname()));

    return ResponseEntity.ok(new PagedModel<>(usersPage));
  }

  @GetMapping("/{userId}/block")
  @PreAuthorize("hasAnyAuthority('OWNER', 'TASK_MASTER','SITE_ADMIN')")
  @Transactional
  public ResponseEntity<Void> blockUser(
      @PathVariable UUID userId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<SystemRole> issuerAuthorities =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(SystemRole::valueOf)
            .toList();

    SystemRole userAuthorities = userService.getUserById(userId).getRole().getRoleName();

    UserEntity userToBlock = userService.getUserById(userId);

    if (!issuerAuthorities.contains(SystemRole.SITE_ADMIN)) {
      if(!userToBlock.getOrganization().getPkOrganizationId().equals(userDetails.getOrganization().getPkOrganizationId())){
        throw  new ResponseStatusException(HttpStatus.FORBIDDEN);
      }
    }

    if (issuerAuthorities.contains(SystemRole.TASK_MASTER)) {
      if (userAuthorities == SystemRole.GROUP_USER) {
        userService.blockUserById(userId);
      }
    } else if (issuerAuthorities.contains(SystemRole.OWNER)) {
      if (userAuthorities == SystemRole.GROUP_USER || userAuthorities == SystemRole.TASK_MASTER) {
        userService.blockUserById(userId);
      }
    } else if (issuerAuthorities.contains(SystemRole.SITE_ADMIN)) {
      if (userAuthorities == SystemRole.GROUP_USER
          || userAuthorities == SystemRole.TASK_MASTER
          || userAuthorities == SystemRole.OWNER) {
        userService.blockUserById(userId);
      }
    }

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{userId}/unblock")
  @PreAuthorize("hasAnyAuthority('OWNER', 'TASK_MASTER','SITE_ADMIN')")
  @Transactional
  public ResponseEntity<Void> unblockUser(
      @PathVariable UUID userId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<SystemRole> issuerAuthorities =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(SystemRole::valueOf)
            .toList();

    SystemRole userAuthorities = userService.getUserById(userId).getRole().getRoleName();

    UserEntity userToUnblock = userService.getUserById(userId);

    if (!issuerAuthorities.contains(SystemRole.SITE_ADMIN)) {
      if(!userToUnblock.getOrganization().getPkOrganizationId().equals(userDetails.getOrganization().getPkOrganizationId())){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }
    }

    if (issuerAuthorities.contains(SystemRole.TASK_MASTER)) {
      if (userAuthorities == SystemRole.GROUP_USER) {
        userService.unblockUserById(userId);
      }
    } else if (issuerAuthorities.contains(SystemRole.OWNER)) {
      if (userAuthorities == SystemRole.GROUP_USER || userAuthorities == SystemRole.TASK_MASTER) {
        userService.unblockUserById(userId);
      }
    } else if (issuerAuthorities.contains(SystemRole.SITE_ADMIN)) {
      if (userAuthorities == SystemRole.GROUP_USER
          || userAuthorities == SystemRole.TASK_MASTER
          || userAuthorities == SystemRole.OWNER) {
        userService.unblockUserById(userId);
      }
    }

    return ResponseEntity.noContent().build();
  }
}
