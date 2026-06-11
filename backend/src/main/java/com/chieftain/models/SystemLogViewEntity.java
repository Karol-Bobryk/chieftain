package com.chieftain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "v_system_logs")
@Getter
public class SystemLogViewEntity {

    @Id
    @Column(name = "log_id")
    private String logId;

    @Column(name = "domain")
    private String domain;

    @Column(name = "severity")
    private String severity;

    @Column(name = "action")
    private String action;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}