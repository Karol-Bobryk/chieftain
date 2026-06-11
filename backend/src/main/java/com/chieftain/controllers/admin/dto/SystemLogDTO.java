package com.chieftain.controllers.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SystemLogDTO {
    private String domain;
    private String severity;
    private String action;
    private String description;
    private LocalDateTime createdAt;
}