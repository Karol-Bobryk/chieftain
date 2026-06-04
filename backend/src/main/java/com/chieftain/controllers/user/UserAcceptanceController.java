package com.chieftain.controllers.user;

import com.chieftain.controllers.user.dto.AcceptUserRequestDTO;
import com.chieftain.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
            @RequestBody AcceptUserRequestDTO request){

        userService.acceptUser(id, request.getRole());
        return new ResponseEntity<>("User accepted successfully", HttpStatus.OK);
    }
}
