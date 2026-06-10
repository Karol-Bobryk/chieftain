package com.chieftain.events;

import com.chieftain.enums.LogSeverity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPrivilegeLogEvent extends LogEvent {
        public UUID groupId;
        public UUID userId;
        public GroupPrivilegeLogEvent(UUID groupId,UUID userId, LogSeverity severity, String action, String description){
                super(severity, action, description);
                this.groupId = groupId;
                this.userId = userId;
        }

}