package com.chieftain.controllers.organization.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationDetailsResponseDTO {
  UUID organizationId;
  String name;
  String joinToken;
  LocalDateTime createdAt;
}
