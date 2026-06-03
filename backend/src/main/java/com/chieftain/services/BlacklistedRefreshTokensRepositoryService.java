package com.chieftain.services;

import com.chieftain.models.BlacklistedRefreshTokensEntity;
import com.chieftain.repositories.BlacklistedRefreshTokensRepository;
import org.springframework.stereotype.Service;

@Service
public class BlacklistedRefreshTokensRepositoryService {
  private final BlacklistedRefreshTokensRepository blacklistedRefreshTokensRepository;

  public BlacklistedRefreshTokensRepositoryService(
      BlacklistedRefreshTokensRepository blacklistedRefreshTokensRepository) {
    this.blacklistedRefreshTokensRepository = blacklistedRefreshTokensRepository;
  }

  public BlacklistedRefreshTokensEntity save(
      BlacklistedRefreshTokensEntity blacklistedTokenEntity) {
    return blacklistedRefreshTokensRepository.save(blacklistedTokenEntity);
  }

  public boolean isBlacklisted(String token) {
    return blacklistedRefreshTokensRepository.existsByRefreshTokenJti(
        JwtService.getTokenJti(token));
  }
}
