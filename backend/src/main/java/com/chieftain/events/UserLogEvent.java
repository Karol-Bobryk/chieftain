package com.chieftain.events;

import com.chieftain.enums.LogSeverity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLogEvent extends LogEvent {
        public UUID userId;
        public UserLogEvent(UUID userId, LogSeverity severity, String action, String description){
                super(severity, action, description);
                this.userId = userId;
        }

}