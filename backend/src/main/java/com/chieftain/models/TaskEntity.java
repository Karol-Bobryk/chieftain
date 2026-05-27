package com.chieftain.models;

import com.chieftain.enums.TaskStatus;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @Nonnull
    @Column(name = "pk_task_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany
    @Nonnull
    @JoinColumn(name = "fk_creator_user_id")
    private UserEntity creatorUser;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private TaskEntity parentTask;

    @ManyToOne
    @Nonnull
    @JoinColumn(name = "fk_group_id")
    private GroupEntity group;

    @Column(name = "name")
    private String name;

    @Nonnull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Nonnull
    @Column(name= "done_at")
    private LocalDateTime doneAt;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "description")
    private String description;

}
