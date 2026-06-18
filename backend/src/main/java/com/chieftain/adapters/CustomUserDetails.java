package com.chieftain.adapters;

import com.chieftain.enums.SystemRole;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.UserEntity;
import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private final UserEntity userEntity;

  public CustomUserDetails(UserEntity userEntity) {
    this.userEntity = userEntity;
  }

  @Nonnull
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(userEntity.getRole().getRoleName().name()));
  }

  public OrganizationEntity getOrganization() {
    return userEntity.getOrganization();
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

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !userEntity.getBlocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return !userEntity.getBlocked();
  }

  public SystemRole getRole(){
    return userEntity.getRole().getRoleName();
  }
}
