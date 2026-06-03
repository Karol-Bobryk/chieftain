package com.chieftain.services;

import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.models.UsersAwaitingAcceptanceEntity;
import com.chieftain.repositories.UsersAwaitingAcceptanceRepository;
import org.springframework.stereotype.Service;

@Service
public class UsersAwaitingAcceptanceService {

  private final UsersAwaitingAcceptanceRepository usersAwaitingAcceptanceRepository;

  public UsersAwaitingAcceptanceService(
      UsersAwaitingAcceptanceRepository usersAwaitingAcceptanceRepository) {
    this.usersAwaitingAcceptanceRepository = usersAwaitingAcceptanceRepository;
  }

  public void createAndSave(UserEntity user, OrganizationEntity organization) {
    UsersAwaitingAcceptanceEntity usersAwaitingAcceptance = new UsersAwaitingAcceptanceEntity();
    usersAwaitingAcceptance.setUser(user);
    usersAwaitingAcceptance.setOrganization(organization);
    usersAwaitingAcceptanceRepository.save(usersAwaitingAcceptance);
  }
}
