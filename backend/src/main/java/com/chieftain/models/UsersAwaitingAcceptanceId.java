package com.chieftain.models;

import com.chieftain.enums.GroupUserPermission;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UsersAwaitingAcceptanceId implements Serializable{
        @Nonnull
        @Column(name = "fk_group_id", nullable = false)
        private UUID groupId;

        @Nonnull
        @Column(name = "fk_user_id", nullable = false)
        private UUID userId;
}
