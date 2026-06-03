package com.chieftain.models;

import com.chieftain.enums.TaskStatus;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

  @Id
  @Nonnull
  @Column(name = "pk_task_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @Nonnull
  @JoinColumn(name = "fk_creator_user_id", nullable = false)
  private UserEntity creatorUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_task_id")
  private TaskEntity parentTask;

  @ManyToOne(fetch = FetchType.LAZY)
  @Nonnull
  @JoinColumn(name = "fk_group_id")
  private GroupEntity group;

  @Column(name = "name", nullable = false)
  private String name;

  @CreationTimestamp
  @Nonnull
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "done_at")
  private LocalDateTime doneAt;

  @Column(name = "deadline", nullable = false)
  private LocalDateTime deadline;

  @Nonnull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "fk_status_id", nullable = false)
  private TaskStatusEntity status;


  @Column(name = "description")
  private String description;

  @ManyToMany
  @JoinTable(
      name = "task_assignees",
      joinColumns = @JoinColumn(name = "fk_task_id"),
      inverseJoinColumns = @JoinColumn(name = "fk_assignee_user_id"))
  private List<UserEntity> assignees = new ArrayList<>();

  @Version
  @Column(name = "version")
  private Long version;

  @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TaskEntity> subtasks = new ArrayList<>();
}
