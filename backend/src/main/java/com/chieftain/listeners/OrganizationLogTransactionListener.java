package com.chieftain.listeners;

import com.chieftain.events.OrganizationLogEvent;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.services.LogService;
import com.chieftain.services.OrganizationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrganizationLogTransactionListener
    extends LogTransactionListener<OrganizationLogEvent> {

  private final OrganizationService organizationService;

  public OrganizationLogTransactionListener(
      LogService logService, OrganizationService organizationService) {
    super(logService);
    this.organizationService = organizationService;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLog(OrganizationLogEvent event) {
    OrganizationEntity organization =
        organizationService.getOrganizationById(event.getOrganizationId());
    logService.logOrganizationAction(
        organization, event.getSeverity(), event.getAction(), event.getDescription());
  }
}
