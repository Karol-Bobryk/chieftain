package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users_awaiting_acceptance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersAwaitingAcceptanceEntity {

  @Nonnull @EmbeddedId private UsersAwaitingAcceptanceId id = new UsersAwaitingAcceptanceId();

  @Nonnull
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("organizationId")
  @JoinColumn(name = "fk_organization_id", nullable = false)
  private OrganizationEntity organization;

  @Nonnull
  @OneToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "fk_user_id", nullable = false)
  private UserEntity user;
}
