package com.chieftain.models;

import com.chieftain.enums.LogSeverity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_privilege_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPrivilegeLogEntity {

    @Id
    @Nonnull
    @Column(name = "pk_log_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_group_id", nullable = false)
    private GroupEntity group;

    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    private UserEntity user;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private LogSeverity severity;

    @Nonnull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Nonnull
    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "description")
    private String description;
}
