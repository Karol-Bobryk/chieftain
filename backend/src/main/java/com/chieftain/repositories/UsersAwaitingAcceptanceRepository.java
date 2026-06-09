package com.chieftain.repositories;

import com.chieftain.models.UserEntity;
import com.chieftain.models.UsersAwaitingAcceptanceEntity;
import com.chieftain.models.UsersAwaitingAcceptanceId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersAwaitingAcceptanceRepository
    extends JpaRepository<UsersAwaitingAcceptanceEntity, UsersAwaitingAcceptanceId> {
  Optional<UsersAwaitingAcceptanceEntity> findByUser(UserEntity user);
}
