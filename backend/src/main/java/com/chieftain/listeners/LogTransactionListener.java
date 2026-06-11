package com.chieftain.listeners;

import com.chieftain.events.LogEvent;
import com.chieftain.services.LogService;
import org.springframework.stereotype.Component;

@Component
public abstract class LogTransactionListener<T extends LogEvent> {
  protected final LogService logService;

  public LogTransactionListener(LogService logService) {
    this.logService = logService;
  }

  public abstract void handleLog(T event);
}
