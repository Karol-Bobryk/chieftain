package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_privileges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPrivilegeEntity {

  @Nonnull @EmbeddedId private GroupPrivilegeId id = new GroupPrivilegeId();

  @Nonnull
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("groupId")
  @JoinColumn(name = "fk_group_id", nullable = false)
  private GroupEntity group;

  @Nonnull
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "fk_user_id", nullable = false)
  private UserEntity user;

  @Nonnull
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "group_privilege_permissions",
          joinColumns = {
              @JoinColumn(name = "fk_group_id", referencedColumnName = "fk_group_id"),
              @JoinColumn(name = "fk_user_id", referencedColumnName = "fk_user_id" )
          },
          inverseJoinColumns = {
                  @JoinColumn(name = "fk_permission_id", referencedColumnName = "pk_permission_id")
          }

  )
  private List<GroupUserPermissionEntity> permissions = new ArrayList<>();
}
