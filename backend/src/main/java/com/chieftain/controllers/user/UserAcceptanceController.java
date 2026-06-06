package com.chieftain.controllers.user;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.user.dto.AcceptUserRequestDTO;
import com.chieftain.services.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserAcceptanceController {

  private final UserService userService;

  public UserAcceptanceController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/{id}/accept")
  @PreAuthorize("hasAnyAuthority('OWNER', 'TASK_MASTER')")
  public ResponseEntity<String> acceptUser(
      @PathVariable UUID id,
      @Valid @RequestBody AcceptUserRequestDTO request) {

    userService.acceptUser(id, request.getRole());
    return new ResponseEntity<>("User accepted successfully", HttpStatus.OK);
  }
}
