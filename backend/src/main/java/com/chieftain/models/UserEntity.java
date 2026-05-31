package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  @Nonnull
  @Column(name = "pk_user_id")
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID pkUserId;

  @ManyToOne
  @JoinColumn(name = "fk_organization_id")
  private OrganizationEntity organization;

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
