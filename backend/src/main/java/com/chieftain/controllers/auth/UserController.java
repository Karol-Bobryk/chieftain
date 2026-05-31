package com.chieftain.controllers.auth;

import com.chieftain.controllers.auth.dto.CreateUserRequestDTO;
import com.chieftain.models.UserEntity;
import com.chieftain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
