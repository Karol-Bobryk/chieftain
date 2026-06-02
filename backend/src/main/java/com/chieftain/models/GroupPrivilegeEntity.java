package com.chieftain.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_privileges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPrivilegeEntity {

    @Nonnull
    @EmbeddedId
    private GroupPrivilegeId id = new GroupPrivilegeId();

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

}
