package com.chieftain.models;

import com.chieftain.enums.GroupUserPermission;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GroupPrivilegeId implements Serializable {

  @Nonnull
  @Column(name = "fk_group_id", nullable = false)
  private UUID groupId;

  @Nonnull
  @Column(name = "fk_user_id", nullable = false)
  private UUID userId;

}
