package com.chieftain.services;

import com.chieftain.enums.TaskStatus;
import com.chieftain.exceptions.SubtaskNotEligible;
import com.chieftain.exceptions.TaskNotFoundException;
import com.chieftain.models.TaskEntity;
import com.chieftain.models.TaskStatusEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.TaskRepository;
import com.chieftain.repositories.TaskStatusRepository;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
  private final TaskRepository taskRepository;
  private final TaskStatusRepository taskStatusRepository;

  public TaskService(TaskRepository taskRepository, TaskStatusRepository taskStatusRepository) {
    this.taskRepository = taskRepository;
    this.taskStatusRepository = taskStatusRepository;
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

  public TaskEntity assignUser(TaskEntity task, UserEntity user) {

    if(!task.getAssignees().contains(user)){
      task.getAssignees().add(user);
      return save(task);
    }

    return task;
  }

  public TaskEntity save(@Nonnull TaskEntity task) {
    return taskRepository.save(task);
  }
}
