package com.chieftain.services;

import com.chieftain.controllers.auth.dto.CreateUserWithOrganizationRequestDTO;
import com.chieftain.enums.SystemRole;
import com.chieftain.exceptions.*;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final OrganizationService organizationService;

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      OrganizationService organizationService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.organizationService = organizationService;
  }

  public UserEntity save(UserEntity userEntity)
      throws UserSecretNotProvidedException, EmailIsAlreadyTakenException {

    if (userRepository.existsByEmailAddress(userEntity.getEmailAddress())) {
      throw new EmailIsAlreadyTakenException(
          "Failed to create user: " + userEntity.getEmailAddress() + " already exists");
    }

    String hashedSecret = passwordEncoder.encode(userEntity.getSecretHash());
    if (hashedSecret == null) {
      throw new UserSecretNotProvidedException(
          "Failed to create user: " + userEntity.getPkUserId() + " no secret provided");
    }

    userEntity.setSecretHash(hashedSecret);
    return userRepository.save(userEntity);
  }

  public UserEntity isPasswordMatchingForEmailAddress(String emailAddress, String password)
      throws InvalidUserSecretProvidedException {
    UserEntity userEntity =
        userRepository
            .findByEmailAddress(emailAddress)
            .orElseThrow(
                () ->
                    new EmailAddressNotFoundException(
                        "No user is associated with email: " + emailAddress));

    if (!passwordEncoder.matches(password, userEntity.getSecretHash())) {
      throw new InvalidUserSecretProvidedException(
          "Failed to authenticate user: " + emailAddress + " invalid secret");
    }

    return userEntity;
  }

  @Transactional
  public void createUserWithOrganization(CreateUserWithOrganizationRequestDTO request) {
    OrganizationEntity organization =
        organizationService.createByName(request.getOrganizationName());

    UserEntity userEntity = new UserEntity();
    userEntity.setEmailAddress(request.getEmailAddress());
    userEntity.setSecretHash(request.getPassword());
    userEntity.setName(request.getName());
    userEntity.setSurname(request.getSurname());
    userEntity.setJobTitle(request.getJobTitle());
    userEntity.setRole(SystemRole.OWNER);
    userEntity.setBlocked(false);
    userEntity.setOrganization(organization);

    save(userEntity);
  }

  public UserEntity getUserById(UUID userId) {
    return userRepository
        .findByPkUserId(userId)
        .orElseThrow(() -> new UserIdNotFoundException("No user with Id: " + userId));
  }

  @Transactional
  public void acceptUser(UUID userId, String roleName){
    UserEntity user = getUserById(userId);
    if (user.getAccepted()){
      throw new IllegalStateException("User " + userId + "is already accepted.");
    }
    user.setAccepted(true);
    user.setRole(SystemRole.valueOf(roleName.toUpperCase()));
    userRepository.save(user);
  }
}
