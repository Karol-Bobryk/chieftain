package com.chieftain.controllers.user;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.user.dto.AcceptUserRequestDTO;
import com.chieftain.controllers.user.dto.GetUserGroupsResponseDTO;
import com.chieftain.controllers.user.dto.GroupDisplayDTO;
import com.chieftain.controllers.user.dto.UserDisplayDTO;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.UserEntity;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserAcceptanceController {

  private final UserService userService;
  private final UsersAwaitingAcceptanceService usersAwaitingAcceptanceService;

  public UserAcceptanceController(
      UserService userService, UsersAwaitingAcceptanceService usersAwaitingAcceptanceService) {
    this.userService = userService;
    this.usersAwaitingAcceptanceService = usersAwaitingAcceptanceService;
  }

  @GetMapping("/groups")
  @Transactional
  public ResponseEntity<GetUserGroupsResponseDTO> getUserGroups(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    UUID userId = userDetails.getUserId();
    Pageable pageable = PageRequest.of(page, size);
    UserEntity user = userService.getUserById(userId);

    Page<GroupEntity> groupPage = userService.getGroupsForUsers(user, pageable);
    List<GroupDisplayDTO> groupDisplay =
        groupPage.map(GroupDisplayDTO::fromGroupEntity).stream().toList();

    return ResponseEntity.ok(new GetUserGroupsResponseDTO(groupDisplay));
  }

  @PostMapping("/{id}/accept")
  @PreAuthorize("hasAnyAuthority('OWNER', 'TASK_MASTER')")
  public ResponseEntity<String> acceptUser(
      @PathVariable UUID id, @Valid @RequestBody AcceptUserRequestDTO request) {

    userService.acceptUser(id, request.getRole());
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
}
