package com.chieftain.services;
import com.chieftain.enums.LogSeverity;
import com.chieftain.models.LogSeverityEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.models.UserLogEntity;
import com.chieftain.repositories.LogSeverityRepository;
import com.chieftain.repositories.UserLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final LogSeverityRepository severityRepository;
    private final UserLogRepository userLogRepository;

    public LogService(LogSeverityRepository severityRepository, UserLogRepository userLogRepository) {
        this.severityRepository = severityRepository;
        this.userLogRepository = userLogRepository;
    }

    @Transactional
    public void logUserAction(UserEntity user, LogSeverity severityEnum, String action, String description){

        LogSeverityEntity logSeverityEntity = severityRepository.findByLogSeverityName(severityEnum)
                .orElseThrow(() -> new IllegalStateException(severityEnum + " does not exist in log_severity_dictionary"));

        UserLogEntity log = new UserLogEntity();
        log.setUser(user);
        log.setSeverity(logSeverityEntity);
        log.setAction(action);
        log.setDescription(description);

        userLogRepository.save(log);
    }
}
