package com.chieftain.controllers.auth;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.auth.dto.CreateUserRequestDTO;
import com.chieftain.controllers.auth.dto.LoginUserRequestDTO;
import com.chieftain.controllers.auth.dto.LoginUserResponseDTO;
import com.chieftain.exceptions.InvalidUserSecretProvidedException;
import com.chieftain.models.UserEntity;
import com.chieftain.services.JwtService;
import com.chieftain.services.UserService;
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

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/create")
  @ResponseBody
  public ResponseEntity<Void> createUser(@RequestBody CreateUserRequestDTO request) {
    UserEntity userEntity = new UserEntity();

    userEntity.setEmailAddress(request.getEmailAddress());
    userEntity.setSecretHash(request.getPassword());
    userEntity.setName(request.getName());
    userEntity.setSurname(request.getSurname());

    // TODO: we should call OrganizationService to create a request to add a user,
    //       user shall be unable to access protected endpoints until this request is accepted,
    //       furthermore, we need to check if it is a brand new organization, if it is the user
    //       becomes an owner

    userService.save(userEntity);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/login")
  @ResponseBody
  public ResponseEntity<LoginUserResponseDTO> loginUser(@RequestBody LoginUserRequestDTO request) throws InvalidUserSecretProvidedException {
      UserEntity userEntity = userService.isPasswordMatchingForEmailAddress(request.getEmailAddress(), request.getPassword());
      CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

      String token = JwtService.createJwsToken(customUserDetails);

      LoginUserResponseDTO loginUserResponseDTO = new LoginUserResponseDTO();
      loginUserResponseDTO.setAccessToken(token);

      return new ResponseEntity<>(loginUserResponseDTO, HttpStatus.OK);
  }
}