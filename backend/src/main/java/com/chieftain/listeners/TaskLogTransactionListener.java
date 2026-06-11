package com.chieftain.listeners;

import com.chieftain.events.TaskLogEvent;
import com.chieftain.models.TaskEntity;
import com.chieftain.services.LogService;
import com.chieftain.services.TaskService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TaskLogTransactionListener extends LogTransactionListener<TaskLogEvent> {
  private final TaskService taskService;

  public TaskLogTransactionListener(LogService logService, TaskService taskService) {
    super(logService);
    this.taskService = taskService;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLog(TaskLogEvent event) {
    TaskEntity task = taskService.getTaskById(event.getTaskId());
    logService.logTaskAction(task, event.getSeverity(), event.getAction(), event.getDescription());
  }
}
