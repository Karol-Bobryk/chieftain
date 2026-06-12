package com.chieftain.controllers.organization.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

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
