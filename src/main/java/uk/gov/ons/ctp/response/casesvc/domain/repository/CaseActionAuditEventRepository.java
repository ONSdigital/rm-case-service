package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionAuditEvent;
import uk.gov.ons.ctp.response.casesvc.representation.action.ActionTemplateDTO.Handler;

@Repository
@Transactional(readOnly = true)
public interface CaseActionAuditEventRepository
    extends JpaRepository<CaseActionAuditEvent, Integer> {
  CaseActionAuditEvent findByCaseIdAndTypeAndHandlerAndTagAndStatus(
      UUID caseId,
      String type,
      Handler handler,
      String tag,
      CaseActionAuditEvent.ActionEventStatus status);

  List<CaseActionAuditEvent> findByStatus(CaseActionAuditEvent.ActionEventStatus status);
}
