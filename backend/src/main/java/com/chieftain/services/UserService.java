package com.chieftain.services;

import com.chieftain.exceptions.EmailIsAlreadyTakenException;
import com.chieftain.exceptions.UserSecretNotProvidedException;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public void save(UserEntity userEntity)
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
    userRepository.save(userEntity);
  }
}
