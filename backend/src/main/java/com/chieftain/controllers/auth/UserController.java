package com.chieftain.controllers.auth;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.auth.dto.CreateUserRequestDTO;
import com.chieftain.controllers.auth.dto.CreateUserWithOrganizationRequestDTO;
import com.chieftain.controllers.auth.dto.LoginUserRequestDTO;
import com.chieftain.controllers.auth.dto.LoginUserResponseDTO;
import com.chieftain.enums.LogSeverity;
import com.chieftain.enums.SystemRole;
import com.chieftain.events.UserLogEvent;
import com.chieftain.exceptions.InvalidUserSecretProvidedException;
import com.chieftain.exceptions.UserIsBarredException;
import com.chieftain.exceptions.UserIsNotAcceptedException;
import com.chieftain.exceptions.UserNotEligible;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.RoleEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.RoleRepository;
import com.chieftain.repositories.UserRepository;
import com.chieftain.services.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
  private final ApplicationEventPublisher applicationEventPublisher;
  private final UserRepository userRepository;

  @Autowired
  public UserController(
          UserService userService,
          OrganizationService organizationService,
          UsersAwaitingAcceptanceService usersAwaitingAcceptanceService,
          RoleRepository roleRepository,
          ApplicationEventPublisher applicationEventPublisher, UserRepository userRepository) {
    this.userService = userService;
    this.organizationService = organizationService;
    this.usersAwaitingAcceptanceService = usersAwaitingAcceptanceService;
    this.roleRepository = roleRepository;
    this.applicationEventPublisher = applicationEventPublisher;
    this.userRepository = userRepository;
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

    applicationEventPublisher.publishEvent(
        new UserLogEvent(
            userEntity.getPkUserId(),
            LogSeverity.INFO,
            "USER_REGISTERED_WITH_TOKEN",
            "User registered via join token and is awaiting acceptance in organization"));
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
  @Transactional
  public ResponseEntity<LoginUserResponseDTO> loginUser(
      @Valid @RequestBody LoginUserRequestDTO request) throws InvalidUserSecretProvidedException {
    UserEntity userEntity =
        userService.isPasswordMatchingForEmailAddress(
            request.getEmailAddress(), request.getPassword());

    if (userEntity.getBlocked()) {
      throw new UserIsBarredException("User is barred");
    }

    if (usersAwaitingAcceptanceService.isUserAwaiting(userEntity.getOrganization(),userEntity) || userEntity.getBlocked()){

      applicationEventPublisher.publishEvent(
              new UserLogEvent(
                      userEntity.getPkUserId(),
                      LogSeverity.WARNING,
                      "USER_LOGGED_IN",
                      "User user login blocked: user is still awaiting acceptance"));
      throw new UserIsNotAcceptedException("User is not accepted");
    }

    CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

    String accessToken = JwtService.createJwsAccessToken(customUserDetails);
    String refreshToken = JwtService.createJwsRefreshToken(customUserDetails);

    applicationEventPublisher.publishEvent(
        new UserLogEvent(
            userEntity.getPkUserId(),
            LogSeverity.INFO,
            "USER_LOGGED_IN",
            "User successfully logged into the system"));

    LoginUserResponseDTO loginUserResponseDTO = new LoginUserResponseDTO();
    loginUserResponseDTO.setAccessToken(accessToken);
    loginUserResponseDTO.setRefreshToken(refreshToken);

    return new ResponseEntity<>(loginUserResponseDTO, HttpStatus.OK);
  }
}
