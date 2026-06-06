package com.chieftain.controllers.group.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupCreateRequestDTO {
    @NotBlank(message = "Group name cannot be blank")
    String name;

    List<UUID> members;
}
