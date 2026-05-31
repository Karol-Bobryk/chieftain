package com.chieftain.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  @Column(name = "pk_user_id")
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID pkUserId;

  // TODO: as per design agreements, this one shouldn't be null
  @ManyToOne
  @JoinColumn(name = "fk_organization_id")
  private OrganizationEntity organization;

  @Column(name = "email_address", nullable = false, unique = true)
  private String emailAddress;

  @Column(name = "secret_hash")
  private String secretHash;

  @Column(name = "name")
  private String name;

  @Column(name = "surname")
  private String surname;

  @Column(name = "job_title")
  private String jobTitle;

  @Column(name = "blocked")
  private Boolean blocked = false;

  @Column(name = "accepted")
  private Boolean accepted = false;

  @CreationTimestamp
  @Column(name = "joined_at", nullable = false, updatable = false)
  private LocalDateTime joinedAt;

  @PrePersist
  private void prePersist() {
    if (blocked == null) {
      blocked = false;
    }
    if (accepted == null) {
      accepted = false;
    }
  }
  // TODO: add roles
}
