package com.chieftain.models;

import com.chieftain.enums.SystemRole;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  @Nonnull
  @Column(name = "pk_user_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID pkUserId;

  @ManyToOne
  @JoinColumn(name = "fk_organization_id", nullable = false)
  private OrganizationEntity organization;

  @Column(name = "email_address", nullable = false)
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
  private Boolean blocked;

  @Column(name = "accepted", nullable = false)
  @Nonnull
  private Boolean accepted;

  @Column(name = "joined_at", nullable = false)
  @Nonnull
  private LocalDateTime joinedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  @Nonnull
  private SystemRole role;
}
