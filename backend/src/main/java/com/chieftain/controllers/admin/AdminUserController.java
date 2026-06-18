package com.chieftain.controllers.admin;

import com.chieftain.controllers.admin.dto.AdminUserDTO;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

  private final UserRepository userRepository;

  public AdminUserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthority('SITE_ADMIN')")
  public ResponseEntity<List<AdminUserDTO>> getAllUsers(@RequestParam(required = false, defaultValue = "") String search) {
    List<UserEntity> users;

    PageRequest pageable = PageRequest.of(0, 100);
    if (search == null || search.trim().isEmpty()) {
      users = userRepository.findAll(pageable).getContent();
    } else {
      users = userRepository.searchByFullName(search.trim(), pageable);
    }

    List<AdminUserDTO> dtos = users.stream()
        .map(user -> new AdminUserDTO(
            user.getPkUserId(),
            user.getName(),
            user.getSurname(),
            user.getEmailAddress(),
            user.getBlocked()
        ))
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }
}