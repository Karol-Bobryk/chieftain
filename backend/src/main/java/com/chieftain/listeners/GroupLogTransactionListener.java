package com.chieftain.listeners;

import com.chieftain.events.GroupLogEvent;
import com.chieftain.models.GroupEntity;
import com.chieftain.services.GroupService;
import com.chieftain.services.LogService;
import com.chieftain.services.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class GroupLogTransactionListener extends LogTransactionListener<GroupLogEvent> {
  private final GroupService groupService;

  public GroupLogTransactionListener(
      LogService logService, UserService userService, GroupService groupService) {
    super(logService);
    this.groupService = groupService;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLog(GroupLogEvent event) {
    GroupEntity group = groupService.getGroupById(event.getGroupId());
    logService.logGroupAction(
        group, event.getSeverity(), event.getAction(), event.getDescription());
  }
}
