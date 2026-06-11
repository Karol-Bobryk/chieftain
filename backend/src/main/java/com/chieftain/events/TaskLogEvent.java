package com.chieftain.events;

import com.chieftain.enums.LogSeverity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskLogEvent extends LogEvent {
        public UUID taskId;
        public TaskLogEvent(UUID taskId, LogSeverity severity, String action, String description){
                super(severity, action, description);
                this.taskId = taskId;
        }

}