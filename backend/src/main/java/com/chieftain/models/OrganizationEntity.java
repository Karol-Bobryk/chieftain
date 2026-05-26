package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationEntity {
    @Id
    @Nonnull
    @Column(name = "pk_organization_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID pkOrganizationId;

    @Nonnull
    @Column(name = "name")
    private String name;

    @Nonnull
    @Column(name = "join_token")
    private String joinToken;

    @Nonnull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Nonnull
    @Column(name = "blocked")
    private Boolean blocked;

    @OneToMany(mappedBy = "organization")
    private List<UserEntity> users;
}
