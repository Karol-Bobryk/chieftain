package com.chieftain.configs;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.enums.LogSeverity;
import com.chieftain.enums.SystemRole;
import com.chieftain.enums.TaskStatus;
import com.chieftain.models.*;
import com.chieftain.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupPrivilegeRepository groupPrivilegeRepository;
    private final GroupUserPermissionRepository permissionRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskLogRepository taskLogRepository;
    private final UserLogRepository userLogRepository;
    private final GroupLogRepository groupLogRepository;
    private final OrganizationLogRepository organizationLogRepository;
    private final GroupPrivilegeLogRepository groupPrivilegeLogRepository;
    private final UsersAwaitingAcceptanceRepository usersAwaitingAcceptanceRepository;
    private final LogSeverityRepository logSeverityRepository;

    private final Faker faker = new Faker(new Locale("pl"));
    private final Faker enFaker = new Faker(Locale.ENGLISH);
    private final Random random = new Random();

    private static final int BATCH_SIZE = 500;
    private static final int ORG_COUNT = 100;
    private static final int GROUPS_PER_ORG = 5;
    private static final int USERS_PER_ORG = 100;
    private static final int TASKS_PER_GROUP = 20;

    @Override
    public void run(ApplicationArguments args) {
        log.info("DataSeeder.run()");
        log.info("Args: {}", args.getOptionNames());

        if (!args.containsOption("seed")) {
            log.info("No --seed argument, skipping");
            return;
        }

        if (organizationRepository.count() > 0 && !args.containsOption("force")) {
            log.warn("Database already has data, add --force to override");
            return;
        }

        Map<SystemRole, RoleEntity> roles = loadRoles();
        Map<GroupUserPermission, GroupUserPermissionEntity> permissions = loadPermissions();
        Map<TaskStatus, TaskStatusEntity> statuses = loadStatuses();
        Map<LogSeverity, LogSeverityEntity> severities = loadSeverities();
        String hashedPassword = passwordEncoder.encode("maslo123");

        List<OrganizationEntity> orgs = seedOrganizations();
        List<UserEntity> users = seedUsers(orgs, roles, hashedPassword);
        List<GroupEntity> groups = seedGroups(orgs, users, permissions);
        List<TaskEntity> tasks = seedTasks(groups, statuses);

        seedAwaitingAcceptance(users);
        seedTaskLogs(tasks, severities);
        seedUserLogs(users, severities);
        seedGroupLogs(groups, severities);
        seedOrganizationLogs(orgs, severities);
        seedGroupPrivilegeLogs(groups, severities);

        log.info("Seed completed");
    }

    private List<OrganizationEntity> seedOrganizations() {
        String[] suffixes = {
                "Ltd", "Inc", "Corp", "Group", "Solutions",
                "Technologies", "Systems", "Consulting", "Services", "Global"
        };

        List<OrganizationEntity> orgs = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();

        int attempts = 0;
        while (orgs.size() < ORG_COUNT && attempts < ORG_COUNT * 10) {
            attempts++;
            String baseName = enFaker.company().name()
                    .replaceAll("[^a-zA-Z0-9 ]", "")
                    .trim();
            String fullName = baseName + " " + suffixes[orgs.size() % suffixes.length];

            if (usedNames.contains(fullName)) continue;
            usedNames.add(fullName);

            OrganizationEntity org = new OrganizationEntity();
            org.setName(fullName);
            org.setBlocked(false);
            orgs.add(org);
        }

        orgs = organizationRepository.saveAll(orgs);
        log.info("Saved organizations");
        return orgs;
    }

    private List<UserEntity> seedUsers(List<OrganizationEntity> orgs, Map<SystemRole, RoleEntity> roles, String hashedPassword) {
        List<UserEntity> allUsers = new ArrayList<>();
        List<UserEntity> batch = new ArrayList<>();
        int counter = 0;

        for (OrganizationEntity org : orgs) {
            batch.add(buildUser(org, roles.get(SystemRole.OWNER), hashedPassword, counter++));

            for (int i = 0; i < 2; i++) {
                batch.add(buildUser(org, roles.get(SystemRole.TASK_MASTER), hashedPassword, counter++));
            }

            for (int i = 0; i < USERS_PER_ORG - 3; i++) {
                batch.add(buildUser(org, roles.get(SystemRole.GROUP_USER), hashedPassword, counter++));
            }

            if (batch.size() >= BATCH_SIZE) {
                allUsers.addAll(userRepository.saveAll(batch));
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            allUsers.addAll(userRepository.saveAll(batch));
        }

        log.info("Saved users");
        return allUsers;
    }

    private UserEntity buildUser(OrganizationEntity org, RoleEntity role,
                                 String hashedPassword, int index) {
        String firstName = faker.name().firstName().replaceAll("[^a-zA-Z]", "");
        String lastName = faker.name().lastName().replaceAll("[^a-zA-Z]", "");

        String orgDomain = org.getName()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "")
                .trim();
        if (orgDomain.isEmpty()) orgDomain = "company";

        String email = (firstName + "." + lastName + index).toLowerCase() + "@" + orgDomain + ".com";

        UserEntity user = new UserEntity();
        user.setName(firstName);
        user.setSurname(lastName);
        user.setEmailAddress(email);
        user.setSecretHash(hashedPassword);
        user.setJobTitle(enFaker.job().title());
        user.setBlocked(false);
        user.setOrganization(org);
        user.setRole(role);
        return user;
    }

    private List<GroupEntity> seedGroups(List<OrganizationEntity> orgs,
                                           List<UserEntity> allUsers,
                                           Map<GroupUserPermission, GroupUserPermissionEntity> permissions) {
        Map<UUID, List<UserEntity>> usersByOrg = new HashMap<>();
        for (UserEntity user : allUsers) {
            usersByOrg.computeIfAbsent(
                    user.getOrganization().getPkOrganizationId(),
                    _ -> new ArrayList<>()).add(user);
        }

        String[] groupSuffixes = {"Team", "Squad", "Unit", "Department"};

        List<GroupEntity> allGroups = new ArrayList<>();
        List<GroupPrivilegeEntity> privBatch = new ArrayList<>();

        for (OrganizationEntity org : orgs) {
            List<UserEntity> orgUsers = usersByOrg.getOrDefault(org.getPkOrganizationId(), List.of());
            if (orgUsers.isEmpty()) continue;

            for (int i = 0; i < GROUPS_PER_ORG; i++) {
                String groupName = enFaker.job().field()
                        .replaceAll("[^a-zA-Z0-9 ]", "").trim()
                        + " " + groupSuffixes[i % groupSuffixes.length];

                GroupEntity group = new GroupEntity();
                group.setName(groupName);
                group.setOrganization(org);

                int memberCount = 5 + random.nextInt(16);
                List<UserEntity> shuffled = new ArrayList<>(orgUsers);
                Collections.shuffle(shuffled);
                List<UserEntity> members = new ArrayList<>(
                        shuffled.subList(0, Math.min(memberCount, shuffled.size())));

                group.setMembers(members);
                group = groupRepository.save(group);
                allGroups.add(group);

                for (UserEntity member : members) {
                    for (GroupUserPermission perm : getPermissionsForUser(member)) {
                        GroupUserPermissionEntity permEntity = permissions.get(perm);
                        if (permEntity == null) continue;

                        GroupPrivilegeEntity priv = new GroupPrivilegeEntity();
                        priv.setGroup(group);
                        priv.setUser(member);
                        priv.setPermission(permEntity);
                        privBatch.add(priv);
                    }
                }

                if (privBatch.size() >= BATCH_SIZE) {
                    groupPrivilegeRepository.saveAll(privBatch);
                    privBatch.clear();
                }
            }
        }

        if (!privBatch.isEmpty()) {
            groupPrivilegeRepository.saveAll(privBatch);
        }

        log.info("Saved groups");
        return allGroups;
    }

    private List<GroupUserPermission> getPermissionsForUser(UserEntity user) {
        return switch (user.getRole().getRoleName()) {
            case OWNER, TASK_MASTER -> List.of(GroupUserPermission.values());
            case GROUP_USER -> {
                List<GroupUserPermission> all = new ArrayList<>(List.of(GroupUserPermission.values()));
                Collections.shuffle(all);
                yield all.subList(0, 1 + random.nextInt(all.size()));
            }
            default -> List.of(GroupUserPermission.ADD_TASK);
        };
    }

    private List<TaskEntity> seedTasks(List<GroupEntity> groups,
                                       Map<TaskStatus, TaskStatusEntity> statuses) {
        List<TaskStatus> statusKeys = new ArrayList<>(statuses.keySet());
        List<TaskEntity> batch = new ArrayList<>();
        List<TaskEntity> allTasks = new ArrayList<>();
        List<List<UserEntity>> taskMembers = new ArrayList<>();
        List<List<UserEntity>> batchMembers = new ArrayList<>();

        String[] taskPrefixes = {
                "Fix", "Implement", "Review", "Deploy", "Refactor",
                "Test", "Update", "Analyze", "Document", "Optimize"
        };

        for (GroupEntity group : groups) {
            List<UserEntity> members = new ArrayList<>(group.getMembers());
            if (members.isEmpty()) continue;

            for (int i = 0; i < TASKS_PER_GROUP; i++) {
                LocalDateTime startedAt = LocalDateTime.now().minusDays(random.nextInt(60));
                LocalDateTime deadline = LocalDateTime.now().plusDays(1 + random.nextInt(89));

                String taskName = taskPrefixes[random.nextInt(taskPrefixes.length)]
                        + " " + enFaker.app().name()
                        .replaceAll("[^a-zA-Z0-9 ]", "").trim();

                TaskEntity task = new TaskEntity();
                task.setName(taskName);
                task.setDescription(faker.lorem().sentence(10));
                task.setGroup(group);
                task.setStartedAt(startedAt);
                task.setDeadline(deadline);
                task.setCreatorUser(members.get(random.nextInt(members.size())));

                TaskStatus status = statusKeys.get(random.nextInt(statusKeys.size()));
                task.setStatus(statuses.get(status));

                if (status == TaskStatus.FINISHED) {
                    long daysBetween = Math.max(1,
                            Duration.between(startedAt, deadline).toDays());
                    task.setDoneAt(startedAt.plusDays(random.nextInt((int) daysBetween)));
                }

                task.setVersion(0L);

                int assigneeCount = random.nextInt(4);
                if (assigneeCount > 0) {
                    List<UserEntity> shuffled = new ArrayList<>(members);
                    Collections.shuffle(shuffled);
                    task.setAssignees(new ArrayList<>(
                            shuffled.subList(0, Math.min(assigneeCount, shuffled.size()))));
                } else {
                    task.setAssignees(new ArrayList<>());
                }

                batch.add(task);
                batchMembers.add(members);

                if (batch.size() == BATCH_SIZE) {
                    allTasks.addAll(taskRepository.saveAll(batch));
                    taskMembers.addAll(batchMembers);
                    batch.clear();
                    batchMembers.clear();
                }
            }
        }

        if (!batch.isEmpty()) {
            allTasks.addAll(taskRepository.saveAll(batch));
            taskMembers.addAll(batchMembers);
        }

        log.info("Saved tasks");
        seedSubtasks(allTasks, statuses, taskMembers);
        return allTasks;
    }

    private void seedSubtasks(List<TaskEntity> parentTasks,
                              Map<TaskStatus, TaskStatusEntity> statuses,
                              List<List<UserEntity>> taskMembers) {
        List<TaskStatus> statusKeys = new ArrayList<>(statuses.keySet());
        List<TaskEntity> batch = new ArrayList<>();
        List<TaskEntity> eligibleParents = new ArrayList<>();
        List<List<UserEntity>> eligibleMembers = new ArrayList<>();

        for (int i = 0; i < parentTasks.size(); i++) {
            TaskEntity t = parentTasks.get(i);
            if (t.getParentTask() == null && random.nextDouble() < 0.3) {
                eligibleParents.add(t);
                eligibleMembers.add(taskMembers.get(i));
            }
        }

        for (int p = 0; p < eligibleParents.size(); p++) {
            TaskEntity parent = eligibleParents.get(p);
            List<UserEntity> members = eligibleMembers.get(p);
            if (members.isEmpty()) continue;

            int subCount = 1 + random.nextInt(3);

            for (int i = 0; i < subCount; i++) {
                long parentDays = Math.max(1,
                        Duration.between(parent.getStartedAt(), parent.getDeadline()).toDays());

                LocalDateTime subStartedAt = parent.getStartedAt()
                        .plusDays(random.nextInt((int) parentDays));
                LocalDateTime subDeadline = subStartedAt
                        .plusDays(1 + random.nextInt(
                                (int) Math.max(1, Duration.between(subStartedAt, parent.getDeadline()).toDays())));

                if (subDeadline.isAfter(parent.getDeadline())) {
                    subDeadline = parent.getDeadline();
                }

                TaskStatus status = statusKeys.get(random.nextInt(statusKeys.size()));

                TaskEntity subtask = new TaskEntity();
                subtask.setName("Subtask: " + parent.getName());
                subtask.setDescription(faker.lorem().sentence(8));
                subtask.setGroup(parent.getGroup());
                subtask.setParentTask(parent);
                subtask.setStartedAt(subStartedAt);
                subtask.setDeadline(subDeadline);
                subtask.setCreatorUser(members.get(random.nextInt(members.size())));
                subtask.setStatus(statuses.get(status));
                subtask.setVersion(0L);

                if (status == TaskStatus.FINISHED) {
                    long days = Math.max(1, Duration.between(subStartedAt, subDeadline).toDays());
                    subtask.setDoneAt(subStartedAt.plusDays(random.nextInt((int) days)));
                }

                subtask.setAssignees(new ArrayList<>());
                batch.add(subtask);

                if (batch.size() == BATCH_SIZE) {
                    taskRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) {
            taskRepository.saveAll(batch);
        }

        log.info("Saved subtasks");
    }

    private void seedAwaitingAcceptance(List<UserEntity> users) {
        List<UsersAwaitingAcceptanceEntity> waiting = new ArrayList<>();

        for (UserEntity user : users) {
            if (user.getRole().getRoleName() != SystemRole.GROUP_USER) continue;
            if (random.nextDouble() > 0.05) continue;

            UsersAwaitingAcceptanceEntity entity = new UsersAwaitingAcceptanceEntity();
            entity.setUser(user);
            entity.setOrganization(user.getOrganization());
            waiting.add(entity);
        }

        usersAwaitingAcceptanceRepository.saveAll(waiting);
        log.info("Saved users awaiting acceptance");
    }

    private void seedTaskLogs(List<TaskEntity> tasks,
                              Map<LogSeverity, LogSeverityEntity> severities) {
        LogSeverity[] severityValues = LogSeverity.values();
        List<TaskLogEntity> batch = new ArrayList<>();

        for (TaskEntity task : tasks) {
            int count = 1 + random.nextInt(5);
            for (int i = 0; i < count; i++) {
                TaskLogEntity taskLog = new TaskLogEntity();
                taskLog.setTask(task);
                taskLog.setSeverity(severities.get(severityValues[random.nextInt(severityValues.length)]));
                taskLog.setAction("TASK_UPDATED");
                taskLog.setDescription(faker.lorem().sentence());
                batch.add(taskLog);

                if (batch.size() == BATCH_SIZE) {
                    taskLogRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) taskLogRepository.saveAll(batch);
        log.info("Saved task logs");
    }

    private void seedUserLogs(List<UserEntity> users,
                              Map<LogSeverity, LogSeverityEntity> severities) {
        LogSeverity[] severityValues = LogSeverity.values();
        List<UserLogEntity> batch = new ArrayList<>();

        for (UserEntity user : users) {
            int count = random.nextInt(4);
            for (int i = 0; i < count; i++) {
                UserLogEntity userLog = new UserLogEntity();
                userLog.setUser(user);
                userLog.setSeverity(severities.get(severityValues[random.nextInt(severityValues.length)]));
                userLog.setAction("USER_ACTION");
                userLog.setDescription(faker.lorem().sentence());
                batch.add(userLog);

                if (batch.size() == BATCH_SIZE) {
                    userLogRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) userLogRepository.saveAll(batch);
        log.info("Saved user logs");
    }

    private void seedGroupLogs(List<GroupEntity> groups,
                               Map<LogSeverity, LogSeverityEntity> severities) {
        LogSeverity[] severityValues = LogSeverity.values();
        List<GroupLogEntity> batch = new ArrayList<>();

        for (GroupEntity group : groups) {
            int count = 1 + random.nextInt(4);
            for (int i = 0; i < count; i++) {
                GroupLogEntity groupLog = new GroupLogEntity();
                groupLog.setGroup(group);
                groupLog.setSeverity(severities.get(severityValues[random.nextInt(severityValues.length)]));
                groupLog.setAction("GROUP_UPDATED");
                groupLog.setDescription(faker.lorem().sentence());
                batch.add(groupLog);

                if (batch.size() == BATCH_SIZE) {
                    groupLogRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) groupLogRepository.saveAll(batch);
        log.info("Saved group logs");
    }

    private void seedOrganizationLogs(List<OrganizationEntity> organizations,
                                      Map<LogSeverity, LogSeverityEntity> severities) {
        LogSeverity[] severityValues = LogSeverity.values();
        List<OrganizationLogEntity> batch = new ArrayList<>();

        for (OrganizationEntity organization : organizations) {
            int count = 1 + random.nextInt(5);
            for (int i = 0; i < count; i++) {
                OrganizationLogEntity orgLog = new OrganizationLogEntity();
                orgLog.setOrganization(organization);
                orgLog.setSeverity(severities.get(severityValues[random.nextInt(severityValues.length)]));
                orgLog.setAction("ORG_UPDATED");
                orgLog.setDescription(enFaker.company().buzzword());
                batch.add(orgLog);

                if (batch.size() == BATCH_SIZE) {
                    organizationLogRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) organizationLogRepository.saveAll(batch);
        log.info("Saved organization logs");
    }

    private void seedGroupPrivilegeLogs(List<GroupEntity> groups,
                                        Map<LogSeverity, LogSeverityEntity> severities) {
        LogSeverity[] severityValues = LogSeverity.values();
        List<GroupPrivilegeLogEntity> batch = new ArrayList<>();

        for (GroupEntity group : groups) {
            List<UserEntity> members = group.getMembers();
            if (members.isEmpty()) continue;

            int count = 1 + random.nextInt(3);
            for (int i = 0; i < count; i++) {
                UserEntity user = members.get(random.nextInt(members.size()));

                GroupPrivilegeLogEntity pLog = new GroupPrivilegeLogEntity();
                pLog.setGroup(group);
                pLog.setUser(user);
                pLog.setSeverity(severities.get(severityValues[random.nextInt(severityValues.length)]));
                pLog.setAction("PRIVILEGE_CHANGED");
                pLog.setDescription(faker.lorem().sentence());
                batch.add(pLog);

                if (batch.size() == BATCH_SIZE) {
                    groupPrivilegeLogRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) groupPrivilegeLogRepository.saveAll(batch);
        log.info("Saved group privilege logs");
    }

    private Map<SystemRole, RoleEntity> loadRoles() {
        Map<SystemRole, RoleEntity> map = new HashMap<>();
        roleRepository.findAll().forEach(r -> map.put(r.getRoleName(), r));

        if (!map.containsKey(SystemRole.OWNER))
            throw new IllegalStateException("OWNER role not found in database");
        if (!map.containsKey(SystemRole.TASK_MASTER))
            throw new IllegalStateException("TASK_MASTER role not found in database");
        if (!map.containsKey(SystemRole.GROUP_USER))
            throw new IllegalStateException("GROUP_USER role not found in database");

        return map;
    }

    private Map<GroupUserPermission, GroupUserPermissionEntity> loadPermissions() {
        Map<GroupUserPermission, GroupUserPermissionEntity> map = new HashMap<>();
        permissionRepository.findAll().forEach(p -> map.put(p.getPermissionName(), p));
        return map;
    }

    private Map<TaskStatus, TaskStatusEntity> loadStatuses() {
        Map<TaskStatus, TaskStatusEntity> map = new HashMap<>();
        taskStatusRepository.findAll().forEach(s -> map.put(s.getStatusName(), s));
        return map;
    }

    private Map<LogSeverity, LogSeverityEntity> loadSeverities() {
        Map<LogSeverity, LogSeverityEntity> map = new HashMap<>();
        logSeverityRepository.findAll().forEach(s -> map.put(s.getLogSeverityName(), s));
        return map;
    }
}