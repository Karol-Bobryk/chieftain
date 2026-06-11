package com.chieftain.services;

import com.chieftain.exceptions.UserNotFoundInQueueException;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.models.UsersAwaitingAcceptanceEntity;
import com.chieftain.repositories.UsersAwaitingAcceptanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  public void removeFromQueue(UserEntity user) {
    UsersAwaitingAcceptanceEntity awaitingRecord =
        usersAwaitingAcceptanceRepository
            .findByUser(user)
            .orElseThrow(
                () -> new UserNotFoundInQueueException("User is not in the awaiting queue."));

    usersAwaitingAcceptanceRepository.delete(awaitingRecord);
  }

  public Page<UsersAwaitingAcceptanceEntity> getUsersInQueue(
      OrganizationEntity organization, Pageable pageable) {
    return usersAwaitingAcceptanceRepository.findAllByOrganization(organization, pageable);
  }

  public boolean isUserAwaiting(OrganizationEntity organization, UserEntity user){
    return usersAwaitingAcceptanceRepository.existsByOrganizationAndUser(organization, user);
  }
}
