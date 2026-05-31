package com.chieftain.services;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.exceptions.UserIdNotFoundException;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired private UserRepository userRepository;

  @Nonnull
  @Override
  public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

    Optional<UserEntity> userEntity = userRepository.findByEmailAddress(username);
    if (userEntity.isEmpty()) throw new UsernameNotFoundException("User not found: " + username);

    return new CustomUserDetails(userEntity.get());
  }

  public CustomUserDetails loadUserById(UUID userId) throws UserIdNotFoundException {

    UserEntity userEntity =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserIdNotFoundException("No user with Id: " + userId));

    return new CustomUserDetails(userEntity);
  }
}
