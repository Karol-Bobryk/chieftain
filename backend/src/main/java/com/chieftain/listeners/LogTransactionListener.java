package com.chieftain.listeners;

import com.chieftain.events.LogEvent;
import com.chieftain.events.UserLogEvent;
import com.chieftain.models.UserEntity;
import com.chieftain.services.LogService;
import com.chieftain.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public abstract class LogTransactionListener<T extends LogEvent> {
  protected final LogService logService;

  public LogTransactionListener(LogService logService) {
    this.logService = logService;
  }

  public abstract void handleLog(T event);
}
