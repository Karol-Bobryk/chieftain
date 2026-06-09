package com.chieftain.controllers.tasks;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.tasks.dto.CreateTaskRequestDTO;
import com.chieftain.controllers.tasks.dto.CreateTaskResponseDTO;
import com.chieftain.enums.GroupUserPermission;
import com.chieftain.enums.TaskStatus;
import com.chieftain.exceptions.SubtaskNotEligible;
import com.chieftain.exceptions.UserNotEligible;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.TaskEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.services.GroupService;
import com.chieftain.services.TaskService;
import com.chieftain.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  //TODO: add logs

  private final TaskService taskService;
  private final UserService userService;
  private final GroupService groupService;

  public TaskController(
      TaskService taskService, UserService userService, GroupService groupService) {
    this.taskService = taskService;
    this.userService = userService;
    this.groupService = groupService;
  }

  @PutMapping("/create")
  @Transactional
  public ResponseEntity<CreateTaskResponseDTO> createTask(
      @Valid @RequestBody CreateTaskRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    TaskEntity task = new TaskEntity();

    task.setStartedAt(LocalDateTime.ofInstant(request.getStartedAt(), ZoneId.systemDefault()));

    task.setDeadline(LocalDateTime.ofInstant(request.getDeadline(), ZoneId.systemDefault()));

    GroupEntity group = groupService.getGroupById(request.getGroupId());

    UserEntity taskCreator = userService.getUserById(userDetails.getUserId());

    if (!groupService.isUserEligible(taskCreator, group, GroupUserPermission.ADD_TASK)) {
      throw new UserNotEligible("user doesn't meet the requirements to create tasks in this group");
    }

    task.setGroup(group);

    task.setCreatorUser(taskCreator);

    task.setName(request.getName());

    task.setDoneAt(null);

    task.setStatus(taskService.getTaskStatusEntity(TaskStatus.CREATED));

    if (request.getDescription() != null) {
      task.setDescription(request.getDescription());
    }

    if (request.getAssignees() != null) {
      task.setAssignees(userService.getUsersByIds(request.getAssignees()));
    }

    UUID parentTaskId = request.getParentTaskId();

    if (parentTaskId != null) {
      TaskEntity parentTask = taskService.getTaskById(parentTaskId);

      try {
        taskService.isSubtaskEligible(parentTask, task);
      } catch (SubtaskNotEligible e) {
        throw new SubtaskNotEligible("Couldn't create a subtask: " + e.getMessage());
      }

      task.setParentTask(parentTask);
    }

    task = taskService.save(task);

    return ResponseEntity.ok(new CreateTaskResponseDTO(task.getId()));
  }

  @PutMapping("/api/tasks/{taskId}/assignees/{userId}")
  @Transactional
  public ResponseEntity<Void> assignToTask(
      @PathVariable UUID taskId,
      @PathVariable UUID userId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    UserEntity issuer = userService.getUserById(userDetails.getUserId());

    TaskEntity task = taskService.getTaskById(taskId);

    UserEntity user = userService.getUserById(userId);

    if (groupService.isUserNotInGroup(task.getGroup(), issuer)) {
      throw new UserNotEligible("cannot assign user to a task, issuer is not in the group");
    }

    if (groupService.isUserNotInGroup(task.getGroup(), user)) {
      throw new UserNotEligible("cannot assign user to a task, user is not in the group");
    }

    taskService.assignUser(task, user);

    return ResponseEntity.noContent().build();
  }
}
