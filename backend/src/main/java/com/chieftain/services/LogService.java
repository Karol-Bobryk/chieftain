package com.chieftain.services;

import com.chieftain.enums.LogSeverity;
import com.chieftain.models.*;
import com.chieftain.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogService {

  private final LogSeverityRepository severityRepository;
  private final UserLogRepository userLogRepository;
  private final TaskLogRepository taskLogRepository;
  private final GroupLogRepository groupLogRepository;
  private final GroupPrivilegeLogRepository groupPrivilegeLogRepository;
    private final OrganizationLogRepository organizationLogRepository;

  public LogService(
          LogSeverityRepository severityRepository,
          UserLogRepository userLogRepository,
          TaskLogRepository taskLogRepository,
          GroupLogRepository groupLogRepository,
          GroupPrivilegeLogRepository groupPrivilegeLogRepository, OrganizationService organizationService, OrganizationLogRepository organizationLogRepository) {
    this.severityRepository = severityRepository;
    this.userLogRepository = userLogRepository;
    this.taskLogRepository = taskLogRepository;
    this.groupLogRepository = groupLogRepository;
    this.groupPrivilegeLogRepository = groupPrivilegeLogRepository;
    this.organizationLogRepository = organizationLogRepository;
  }

  private LogSeverityEntity getSeverityEntity(LogSeverity severityEnum) {
    return severityRepository
        .findByLogSeverityName(severityEnum)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    severityEnum + " does not exist in log_severity_dictionary"));
  }

  @Async
  @Transactional
  public void logUserAction(
      UserEntity user, LogSeverity severityEnum, String action, String description) {
    UserLogEntity log = new UserLogEntity();
    log.setUser(user);
    log.setSeverity(getSeverityEntity(severityEnum));
    log.setAction(action);
    log.setDescription(description);

    userLogRepository.save(log);
  }

  @Async
  @Transactional
  public void logTaskAction(
      TaskEntity task, LogSeverity severityEnum, String action, String description) {
    TaskLogEntity log = new TaskLogEntity();
    log.setTask(task);
    log.setSeverity(getSeverityEntity(severityEnum));
    log.setAction(action);
    log.setDescription(description);

    taskLogRepository.save(log);
  }

  @Async
  @Transactional
  public void logGroupAction(
      GroupEntity group, LogSeverity severityEnum, String action, String description) {
    GroupLogEntity log = new GroupLogEntity();
    log.setGroup(group);
    log.setSeverity(getSeverityEntity(severityEnum));
    log.setAction(action);
    log.setDescription(description);

    groupLogRepository.save(log);
  }

  @Async
  @Transactional
  public void logOrganizationAction(
          OrganizationEntity organization, LogSeverity severityEnum, String action, String description) {
    OrganizationLogEntity log = new OrganizationLogEntity();
    log.setOrganization(organization);
    log.setSeverity(getSeverityEntity(severityEnum));
    log.setAction(action);
    log.setDescription(description);

    organizationLogRepository.save(log);
  }

  @Async
  @Transactional
  public void logGroupPrivilegeAction(
      GroupEntity group,
      UserEntity targetUser,
      LogSeverity severityEnum,
      String action,
      String description) {
    GroupPrivilegeLogEntity log = new GroupPrivilegeLogEntity();

    log.setGroup(group);
    log.setUser(targetUser);

    log.setSeverity(getSeverityEntity(severityEnum));
    log.setAction(action);
    log.setDescription(description);

    groupPrivilegeLogRepository.save(log);
  }
}
