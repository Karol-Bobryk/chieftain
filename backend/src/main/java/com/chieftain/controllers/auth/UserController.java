package com.chieftain.controllers.auth;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.auth.dto.CreateUserRequestDTO;
import com.chieftain.controllers.auth.dto.LoginUserRequestDTO;
import com.chieftain.controllers.auth.dto.LoginUserResponseDTO;
import com.chieftain.exceptions.InvalidUserSecretProvidedException;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.services.JwtService;
import com.chieftain.services.OrganizationService;
import com.chieftain.services.UserService;
import com.chieftain.services.UsersAwaitingAcceptanceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth/user")
public class UserController {

  private final UserService userService;
  private final OrganizationService organizationService;
  private final UsersAwaitingAcceptanceService usersAwaitingAcceptanceService;

  @Autowired
  public UserController(UserService userService, OrganizationService organizationService, UsersAwaitingAcceptanceService usersAwaitingAcceptanceService) {
    this.userService = userService;
    this.organizationService = organizationService;
    this.usersAwaitingAcceptanceService = usersAwaitingAcceptanceService;
  }

  // TODO: add an endpoint to create user with new organization
  @PostMapping("/create")
  @ResponseBody
  public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
    UserEntity userEntity = new UserEntity();

    userEntity.setEmailAddress(request.getEmailAddress());
    userEntity.setSecretHash(request.getPassword());
    userEntity.setName(request.getName());
    userEntity.setSurname(request.getSurname());
    userEntity.setJobTitle(request.getJobTitle());
    userEntity.setRole(request.getRole());

    OrganizationEntity organization = organizationService.getByToken(request.getOrganizationToken());

    userEntity.setOrganization(organization);

    userEntity.setBlocked(false);
    usersAwaitingAcceptanceService.save(userEntity, organization);

    userService.save(userEntity);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/login")
  @ResponseBody
  public ResponseEntity<LoginUserResponseDTO> loginUser(
      @Valid @RequestBody LoginUserRequestDTO request) throws InvalidUserSecretProvidedException {
    log.info(request.getPassword());
    UserEntity userEntity =
        userService.isPasswordMatchingForEmailAddress(
            request.getEmailAddress(), request.getPassword());
    CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

    String token = JwtService.createJwsToken(customUserDetails);

    LoginUserResponseDTO loginUserResponseDTO = new LoginUserResponseDTO();
    loginUserResponseDTO.setAccessToken(token);

    return new ResponseEntity<>(loginUserResponseDTO, HttpStatus.OK);
  }
}
