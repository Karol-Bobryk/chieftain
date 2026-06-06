package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "blacklisted_refresh_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistedRefreshTokensEntity {
  @Id
  @Nonnull
  @Column(name = "jti", nullable = false, updatable = false)
  private UUID refreshTokenJti;

  @Nonnull
  @Column(name = "expiration_time", nullable = false, updatable = false)
  private Instant expirationTime;
}
