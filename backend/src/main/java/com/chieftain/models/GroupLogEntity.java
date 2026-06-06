package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_log_severity_id", nullable = false)
  private LogSeverityEntity severity;

  @CreationTimestamp
  @Nonnull
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Nonnull
  @Column(name = "action", nullable = false)
  private String action;

  @Column(name = "description")
  private String description;
}
