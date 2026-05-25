package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "pk_user_id")
  @Nonnull
  private UUID pkUserId;

  @ForeignKey
  @Nonnull
  private UUID fk_organization_id;

  @Column(name = "email_address")
  @Nonnull
  private String emailAddress;

  @Column(name = "secret_hash")
  @Nonnull
  private String secretHash;

  @Column(name = "name")
  @Nonnull
  private String name;

  @Column(name = "surname")
  @Nonnull
  private String surname;

  @Column(name = "job_title")
  @Nonnull
  private String jobTitle;

  @Column(name = "blocked")
  @Nonnull
  private Boolean blocked;

  @Column(name = "accepted")
  @Nonnull
  private Boolean accepted;

  @Column(name = "joined_at")
  @Nonnull
  private LocalDateTime joinedAt;
  // TODO: add roles
}
