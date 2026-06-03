package com.chieftain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UsersAwaitingAcceptanceId implements Serializable {
  @Column(name = "fk_organization_id")
  private UUID organizationId;

  @Column(name = "fk_user_id")
  private UUID userId;
}
