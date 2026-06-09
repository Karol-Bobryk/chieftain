package com.chieftain.listeners;

import com.chieftain.events.UserLogEvent;
import com.chieftain.models.UserEntity;
import com.chieftain.services.LogService;
import com.chieftain.services.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserLogTransactionListener extends LogTransactionListener<UserLogEvent> {
  private final UserService userService;

  public UserLogTransactionListener(LogService logService, UserService userService ) {
    super(logService);
    this.userService = userService;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLog(UserLogEvent event) {
    UserEntity user = userService.getUserById(event.getUserId());
    logService.logUserAction(user, event.getSeverity(), event.getAction(), event.getDescription());
  }
}
