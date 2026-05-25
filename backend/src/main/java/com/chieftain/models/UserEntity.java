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
  @Nonnull
  private UUID pk_user_id;

  //    @ForeignKey
  //    @Nonnull
  //    private UUID fk_organization_id;

  @Column(name = "email_address")
  @Nonnull
  private String email_address;

  @Column(name = "secret_hash")
  @Nonnull
  private String secret_hash;

  @Column(name = "name")
  @Nonnull
  private String name;

  @Column(name = "surname")
  @Nonnull
  private String surname;

  @Column(name = "job_title")
  @Nonnull
  private String job_title;

  @Column(name = "blocked")
  @Nonnull
  private Boolean blocked;

  @Column(name = "accepted")
  @Nonnull
  private Boolean accepted;

  @Column(name = "joined_at")
  @Nonnull
  private LocalDateTime joined_at;
  // TODO: add roles
}
