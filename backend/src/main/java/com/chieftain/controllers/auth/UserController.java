package com.chieftain.controllers.auth;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.auth.dto.CreateUserRequestDTO;
import com.chieftain.controllers.auth.dto.CreateUserWithOrganizationRequestDTO;
import com.chieftain.controllers.auth.dto.LoginUserRequestDTO;
import com.chieftain.controllers.auth.dto.LoginUserResponseDTO;
import com.chieftain.enums.SystemRole;
import com.chieftain.exceptions.InvalidUserSecretProvidedException;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.RoleEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.RoleRepository;
import com.chieftain.services.JwtService;
import com.chieftain.services.OrganizationService;
import com.chieftain.services.UserService;
import com.chieftain.services.UsersAwaitingAcceptanceService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/user")
public class UserController {

  private final UserService userService;
  private final OrganizationService organizationService;
  private final UsersAwaitingAcceptanceService usersAwaitingAcceptanceService;
  private final RoleRepository roleRepository;

  @Autowired
  public UserController(
      UserService userService,
      OrganizationService organizationService,
      UsersAwaitingAcceptanceService usersAwaitingAcceptanceService,
      RoleRepository roleRepository) {
    this.userService = userService;
    this.organizationService = organizationService;
    this.usersAwaitingAcceptanceService = usersAwaitingAcceptanceService;
    this.roleRepository = roleRepository;
  }

  @PostMapping("/create")
  @ResponseBody
  @Transactional
  public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
    UserEntity userEntity = new UserEntity();

    userEntity.setEmailAddress(request.getEmailAddress());
    userEntity.setSecretHash(request.getPassword());
    userEntity.setName(request.getName());
    userEntity.setSurname(request.getSurname());
    userEntity.setJobTitle(request.getJobTitle());

    RoleEntity userRole =
        roleRepository
            .findByRoleName(SystemRole.GROUP_USER)
            .orElseThrow(() -> new RuntimeException("Role not found in dictionary"));
    // His role can be changed only after getting accepted into the org
    userEntity.setRole(userRole);

    OrganizationEntity organization =
        organizationService.getByToken(request.getOrganizationToken());

    userEntity.setOrganization(organization);

    userEntity.setBlocked(false);
    userEntity = userService.save(userEntity);

    usersAwaitingAcceptanceService.createAndSave(userEntity, organization);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  // Creating user as well as organization, automatically user gets assigned as owner
  @PostMapping("/create-with-organization")
  @ResponseBody
  public ResponseEntity<Void> createUserAndOrganization(
      @Valid @RequestBody CreateUserWithOrganizationRequestDTO request) {
    userService.createUserWithOrganization(request);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/login")
  @ResponseBody
  public ResponseEntity<LoginUserResponseDTO> loginUser(
      @Valid @RequestBody LoginUserRequestDTO request) throws InvalidUserSecretProvidedException {
    UserEntity userEntity =
        userService.isPasswordMatchingForEmailAddress(
            request.getEmailAddress(), request.getPassword());
    CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

    String accessToken = JwtService.createJwsAccessToken(customUserDetails);
    String refreshToken = JwtService.createJwsRefreshToken(customUserDetails);

    LoginUserResponseDTO loginUserResponseDTO = new LoginUserResponseDTO();
    loginUserResponseDTO.setAccessToken(accessToken);
    loginUserResponseDTO.setRefreshToken(refreshToken);

    return new ResponseEntity<>(loginUserResponseDTO, HttpStatus.OK);
  }
}
