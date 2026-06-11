package com.chieftain.controllers.admin;

import com.chieftain.controllers.admin.dto.SystemLogDTO;
import com.chieftain.services.AdminLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminLogController {

    private final AdminLogService adminLogService;

    public AdminLogController(AdminLogService adminLogService) {
        this.adminLogService = adminLogService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SITE_ADMIN')")
    public ResponseEntity<Page<SystemLogDTO>> getLogsTimeline(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SystemLogDTO> logsPage = adminLogService
                .getSystemLogs(pageable)
                .map(entity -> new SystemLogDTO(
                        entity.getDomain(),
                        entity.getEntityId(),
                        entity.getSeverity(),
                        entity.getAction(),
                        entity.getDescription(),
                        entity.getCreatedAt()
                ));

        return ResponseEntity.ok(logsPage);
    }
}