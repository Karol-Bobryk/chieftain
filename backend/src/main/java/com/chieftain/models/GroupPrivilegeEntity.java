package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_privileges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPrivilegeEntity {

    @Nonnull
    @EmbeddedId
    private GroupPrivilegeId id = new GroupPrivilegeId();

    @Nonnull
    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "fk_group_id", nullable = false)
    private GroupEntity group;

    @Nonnull
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "fk_user_id", nullable = false)
    private UserEntity user;

}
