package com.chieftain.models;

import com.chieftain.enums.LogSeverity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupLogEntity {

    @Id
    @Nonnull
    @Column(name = "pk_log_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_group_id", nullable = false)
    private GroupEntity group;

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
