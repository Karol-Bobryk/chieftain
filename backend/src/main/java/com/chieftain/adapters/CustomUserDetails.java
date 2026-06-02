package com.chieftain.adapters;

import com.chieftain.models.UserEntity;
import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private final UserEntity userEntity;

  public CustomUserDetails(UserEntity userEntity) {
    this.userEntity = userEntity;
  }

  @Nonnull
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(); // TODO: APPLY ROLES ENUM HERE
  }

  @Override
  public @Nullable String getPassword() {
    return userEntity.getSecretHash();
  }

  @Nonnull
  @Override
  public String getUsername() {
    return userEntity.getEmailAddress();
  }

  public UUID getUserId() {
    return userEntity.getPkUserId();
  }

  // TODO: refactor to use user entity blocked etc.
  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }
}
