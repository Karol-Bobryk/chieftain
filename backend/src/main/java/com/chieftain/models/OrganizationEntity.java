package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationEntity {
    @Id
    @Nonnull
    @Column(name = "pk_organization_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID pkOrganizationId;

    @Nonnull
    @Column(name = "name", nullable = false)
    private String name;

    @Nonnull
    @Column(name = "join_token", nullable = false)
    private String joinToken;

    @Nonnull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Nonnull
    @Column(name = "blocked", nullable = false)
    private Boolean blocked;

    @OneToMany(mappedBy = "organization")
    private List<UserEntity> users;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupEntity> groups = new ArrayList<>();

}
