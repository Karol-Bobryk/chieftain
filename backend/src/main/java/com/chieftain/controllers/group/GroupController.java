package com.chieftain.controllers.group;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.group.dto.GroupCreateRequestDTO;
import com.chieftain.controllers.group.dto.GroupCreateResponseDTO;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.services.CustomUserDetailsService;
import com.chieftain.services.GroupService;
import com.chieftain.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/groups")
public class GroupController {
  private final GroupService groupService;
  private final UserService userService;

  public GroupController(
      GroupService groupService,
      UserService userService,
      CustomUserDetailsService customUserDetailsService) {
    this.groupService = groupService;
    this.userService = userService;
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

    members.add(userService.getUserById(userDetails.getUserId()));

    GroupEntity groupEntity = new GroupEntity();
    groupEntity.setName(request.getName());
    groupEntity.setMembers(members);
    groupEntity.setOrganization(userDetails.getOrganization());

    groupEntity = groupService.save(groupEntity);

    GroupCreateResponseDTO response = new GroupCreateResponseDTO(groupEntity.getId());
    // TODO: add privs
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
