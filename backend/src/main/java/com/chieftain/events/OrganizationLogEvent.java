package com.chieftain.events;

import com.chieftain.enums.LogSeverity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationLogEvent extends LogEvent {
        public UUID organizationId;
        public OrganizationLogEvent(UUID organizationId, LogSeverity severity, String action, String description){
                super(severity, action, description);
                this.organizationId = organizationId;
        }

}