package com.chieftain.listeners;

import com.chieftain.events.GroupPrivilegeLogEvent;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.services.GroupService;
import com.chieftain.services.LogService;
import com.chieftain.services.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class GroupPrivilegeLogTransactionListener
    extends LogTransactionListener<GroupPrivilegeLogEvent> {
  private final UserService userService;
  private final GroupService groupService;

  public GroupPrivilegeLogTransactionListener(
      LogService logService, UserService userService, GroupService groupService) {
    super(logService);
    this.userService = userService;
    this.groupService = groupService;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLog(GroupPrivilegeLogEvent event) {
    GroupEntity group = groupService.getGroupById(event.getGroupId());
    UserEntity user = userService.getUserById(event.getUserId());

    logService.logGroupPrivilegeAction(
        group, user, event.getSeverity(), event.getAction(), event.getDescription());
  }
}
