package com.chieftain.models;

import com.chieftain.enums.SystemRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "log_severity_dictionary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogSeverityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_log_severity_id", nullable = false)
    private Integer logSeverityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_severity_name", unique = true, nullable = false)
    private SystemRole logSeverityName;
}
