package com.chieftain.models;

import com.chieftain.enums.LogSeverity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "user_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLogEntity {

  @Id
  @Nonnull
  @Column(name = "pk_log_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Nonnull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_user_id", nullable = false)
  private UserEntity user;

  @Nonnull
  @Enumerated(EnumType.STRING)
  @Column(name = "severity", nullable = false)
  private LogSeverity severity;

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
