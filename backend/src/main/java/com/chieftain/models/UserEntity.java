package com.chieftain.models;

import com.chieftain.enums.SystemRole;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  @Nonnull
  @Column(name = "pk_user_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID pkUserId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_organization_id")
  private OrganizationEntity organization;

  @Column(name = "email_address", nullable = false, unique = true)
  @Nonnull
  private String emailAddress;

  @Column(name = "secret_hash", nullable = false)
  @Nonnull
  private String secretHash;

  @Column(name = "name", nullable = false)
  @Nonnull
  private String name;

  @Column(name = "surname", nullable = false)
  @Nonnull
  private String surname;

  @Column(name = "job_title", nullable = false)
  @Nonnull
  private String jobTitle;

  @Column(name = "blocked", nullable = false)
  @Nonnull
  private Boolean blocked = false;

  @Column(name = "accepted", nullable = false)
  @Nonnull
  private Boolean accepted = false;

  @CreationTimestamp
  @Nonnull
  @Column(name = "joined_at", nullable = false, updatable = false)
  private LocalDateTime joinedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  @Nonnull
  private SystemRole role;
}
