package com.chieftain.events;

import com.chieftain.enums.LogSeverity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class LogEvent {
        private LogSeverity severity;
        private String action;
        private String description;
}