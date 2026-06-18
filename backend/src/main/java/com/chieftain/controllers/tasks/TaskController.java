package com.chieftain.controllers.tasks;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.group.dto.GetMembersResponseDTO;
import com.chieftain.controllers.tasks.dto.TaskUpdateRequestDTO;
import com.chieftain.controllers.tasks.dto.CreateTaskRequestDTO;
import com.chieftain.controllers.tasks.dto.CreateTaskResponseDTO;
import com.chieftain.controllers.tasks.dto.UpdateTaskStatusRequestDTO;
import com.chieftain.controllers.user.dto.UserDisplayDTO;
import com.chieftain.enums.GroupUserPermission;
import com.chieftain.enums.LogSeverity;
import com.chieftain.enums.TaskStatus;
import com.chieftain.events.TaskLogEvent;
import com.chieftain.exceptions.SubtaskNotEligible;
import com.chieftain.exceptions.UserNotEligible;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.TaskEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.GroupPrivilegeRepository;
import com.chieftain.repositories.TaskRepository;
import com.chieftain.services.GroupService;
import com.chieftain.services.TaskService;
import com.chieftain.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.chieftain.enums.GroupUserPermission.EDIT_TASK;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  // TODO: add logs

  private final TaskService taskService;
  private final UserService userService;
  private final GroupService groupService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TaskRepository taskRepository;
  private final GroupPrivilegeRepository groupPrivilegeRepository;

  public TaskController(
          TaskService taskService,
          UserService userService,
          GroupService groupService,
          ApplicationEventPublisher applicationEventPublisher, TaskRepository taskRepository, GroupPrivilegeRepository groupPrivilegeRepository) {
    this.taskService = taskService;
    this.userService = userService;
    this.groupService = groupService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.taskRepository = taskRepository;
    this.groupPrivilegeRepository = groupPrivilegeRepository;
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

  @PutMapping("/{taskId}/assignees/{userId}")
  @Transactional
  public ResponseEntity<Void> assignToTask(
      @PathVariable UUID taskId,
      @PathVariable UUID userId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    UserEntity issuer = userService.getUserById(userDetails.getUserId());

    TaskEntity task = taskService.getTaskById(taskId);

    UserEntity user = userService.getUserById(userId);

    if (groupService.isUserNotInGroup(task.getGroup(), issuer)) {
      applicationEventPublisher.publishEvent(
          new TaskLogEvent(
              task.getId(),
              LogSeverity.WARNING,
              "TASK_USER_ASSIGNED",
              "Issuer with id: " + issuer.getPkUserId() + " is not in the group"));
      throw new UserNotEligible("cannot assign user to a task, issuer is not in the group");
    }

    if (groupService.isUserNotInGroup(task.getGroup(), user)) {
      applicationEventPublisher.publishEvent(
          new TaskLogEvent(
              task.getId(),
              LogSeverity.WARNING,
              "TASK_USER_ASSIGNED",
              "User with id: " + user.getPkUserId() + " is not in the group"));
      throw new UserNotEligible("cannot assign user to a task, user is not in the group");
    }

    taskService.assignUser(task, user);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{taskId}/delete")
  @Transactional
  public ResponseEntity<Void> deleteTask(
      @PathVariable UUID taskId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    TaskEntity task = taskService.getTaskById(taskId);
    if (!groupService.isUserEligible(
        userService.getUserById(userDetails.getUserId()),
        task.getGroup(),
        GroupUserPermission.REMOVE_TASK)) {
      throw new UserNotEligible("user doesn't meet the requirements to delete tasks in this group");
    }

    taskService.delete(task);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{taskId}/status")
  @Transactional
  public ResponseEntity<Void> updateTaskStatus(
      @PathVariable UUID taskId,
      @RequestBody UpdateTaskStatusRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    TaskEntity task = taskService.getTaskById(taskId);
    UserEntity requester = userService.getUserById(userDetails.getUserId());

    boolean isAssignee = task.getAssignees().contains(requester);
    boolean hasPermission =
        groupService.isUserEligible(requester, task.getGroup(), EDIT_TASK);

    if (!isAssignee && !hasPermission) {
      throw new UserNotEligible(
          "User must be an assignee or have edit task permission to change task status");
    }
    taskService.updateStatus(task, request.getStatus());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{taskId}")
  @Transactional
  public ResponseEntity<Void> updateTask(
          @PathVariable UUID taskId,
          @Valid @RequestBody TaskUpdateRequestDTO request,
          @AuthenticationPrincipal CustomUserDetails userDetails) {

    UserEntity issuer = userService.getUserById(userDetails.getUserId());
    GroupEntity group = groupService.getByIdAndOrganization(taskService.getTaskById(taskId).getGroup().getId(), userDetails.getOrganization());

    if(groupService.isUserNotInGroup(group, issuer)) {
      throw new UserNotEligible("User is not in group");
    }

    if(!groupService.isUserEligible(issuer, group, EDIT_TASK)) {
      throw new UserNotEligible("User cannot edit tasks in group");
    }

    taskService.updateTaskById(taskId, request);

    return ResponseEntity.noContent().build();

  }
}
