package com.chieftain.services;

import com.chieftain.controllers.admin.dto.SystemLogDTO;
import com.chieftain.repositories.AdminLogJdbcRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminLogService {

  private final AdminLogJdbcRepository adminLogJdbcRepository;

  public AdminLogService(AdminLogJdbcRepository adminLogJdbcRepository) {
    this.adminLogJdbcRepository = adminLogJdbcRepository;
  }

  public Page<SystemLogDTO> getSystemLogs(String domain, String severity, Pageable pageable) {

    return adminLogJdbcRepository.findFilteredLogs(domain, severity, pageable);
  }
}
