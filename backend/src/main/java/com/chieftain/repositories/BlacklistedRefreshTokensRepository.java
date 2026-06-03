package com.chieftain.repositories;

import com.chieftain.models.BlacklistedRefreshTokensEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedRefreshTokensRepository
    extends JpaRepository<BlacklistedRefreshTokensEntity, UUID> {
  boolean existsByRefreshTokenJti(UUID refreshTokenJti);
}
