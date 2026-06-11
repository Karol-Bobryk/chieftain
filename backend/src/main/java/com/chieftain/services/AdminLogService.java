package com.chieftain.services;

import com.chieftain.models.SystemLogViewEntity;
import com.chieftain.repositories.SystemLogViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminLogService {

    private final SystemLogViewRepository systemLogViewRepository;

    public AdminLogService(SystemLogViewRepository systemLogViewRepository) {
        this.systemLogViewRepository = systemLogViewRepository;
    }

    public Page<SystemLogViewEntity> getSystemLogs(Pageable pageable) {

        return systemLogViewRepository.findAll(pageable);
    }
}