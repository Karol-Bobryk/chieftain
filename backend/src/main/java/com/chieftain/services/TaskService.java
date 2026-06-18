package com.chieftain.services;

import com.chieftain.enums.LogSeverity;
import com.chieftain.enums.TaskStatus;
import com.chieftain.events.TaskLogEvent;
import com.chieftain.exceptions.SubtaskNotEligible;
import com.chieftain.exceptions.TaskNotFoundException;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.TaskEntity;
import com.chieftain.models.TaskStatusEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.TaskRepository;
import com.chieftain.repositories.TaskStatusRepository;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
  private final TaskRepository taskRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  public TaskService(
      TaskRepository taskRepository,
      TaskStatusRepository taskStatusRepository,
      ApplicationEventPublisher applicationEventPublisher) {
    this.taskRepository = taskRepository;
    this.taskStatusRepository = taskStatusRepository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public TaskEntity getTaskById(@Nonnull UUID taskId) {
    return taskRepository
        .findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException("No task found with id" + taskId));
  }

  public TaskStatusEntity getTaskStatusEntity(TaskStatus taskStatus) {
    return taskStatusRepository.findByStatusName(taskStatus);
  }

  @Transactional
  public boolean isSubtaskEligible(TaskEntity parentTask, TaskEntity childTask) {
    if (!taskRepository.existsById(parentTask.getId())) {
      throw new SubtaskNotEligible("parent task does not exist");
    }

    if (parentTask.getParentTask() != null) {
      throw new SubtaskNotEligible("child task nested too deeply");
    }

    if (!parentTask.getStartedAt().isBefore(childTask.getStartedAt())
        || !parentTask.getStartedAt().isBefore(childTask.getDeadline())) {
      throw new SubtaskNotEligible("child task scheduled before parent task started");
    }

    if (!parentTask.getDeadline().isAfter(childTask.getStartedAt())
        || !parentTask.getDeadline().isAfter(childTask.getDeadline())) {
      throw new SubtaskNotEligible("child task scheduled after parent task is finished");
    }
    return true;
  }

  @Transactional
  public TaskEntity assignUser(TaskEntity task, UserEntity user) {

    if (!task.getAssignees().contains(user)) {
      task.getAssignees().add(user);
      task = save(task);
      applicationEventPublisher.publishEvent(
          new TaskLogEvent(
              task.getId(),
              LogSeverity.INFO,
              "TASK_USER_ASSIGNED",
              "Assigned user with id: " + user.getPkUserId()));
      return task;
    }

    applicationEventPublisher.publishEvent(
        new TaskLogEvent(
            task.getId(),
            LogSeverity.WARNING,
            "TASK_USER_ASSIGNED",
            "User already assigned: " + user.getPkUserId()));

    return task;
  }

  public TaskEntity save(@Nonnull TaskEntity task) {
    task = taskRepository.save(task);
    applicationEventPublisher.publishEvent(
        new TaskLogEvent(
            task.getId(), LogSeverity.INFO, "TASK_CREATED", "Task successfully created"));
    return task;
  }

  public void delete(TaskEntity task) {
    taskRepository.delete(task);
  }

  List<TaskEntity> getParentTasksInGroupWithinTimePeriod(
      @Nonnull GroupEntity group,
      @Nonnull LocalDateTime timePeriodStart,
      @Nonnull LocalDateTime timePeriodEnd) {
    return taskRepository.findAllByGroupAndParentTaskIsNullAndStartedAtBetween(
        group, timePeriodStart, timePeriodEnd);
  }

  @Transactional
  public TaskEntity updateStatus(TaskEntity task, TaskStatus newStatus) {
    task.setStatus(getTaskStatusEntity(newStatus));
    task = taskRepository.save(task);
    applicationEventPublisher.publishEvent(
        new TaskLogEvent(
            task.getId(),
            LogSeverity.INFO,
            "TASK_STATUS_UPDATED",
            "Status changed to: " + newStatus.name()));
    return task;
  }
}
