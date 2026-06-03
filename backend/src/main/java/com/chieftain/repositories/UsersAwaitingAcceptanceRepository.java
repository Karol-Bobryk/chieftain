package com.chieftain.repositories;

import com.chieftain.models.UsersAwaitingAcceptanceEntity;
import com.chieftain.models.UsersAwaitingAcceptanceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersAwaitingAcceptanceRepository
    extends JpaRepository<UsersAwaitingAcceptanceEntity, UsersAwaitingAcceptanceId> {}
