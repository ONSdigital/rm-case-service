package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;

/** JPA Data Repository for CaseIacAudit table. */
@Repository
@Transactional(readOnly = true)
public interface CaseIacAuditRepository extends JpaRepository<CaseIacAudit, Integer> {

  /** Find the latest case iac */
  CaseIacAudit findTop1ByCaseFKOrderByCreatedDateTimeDesc(Integer caseFK);

  /** Find the CaseIacAudit by iac */
  CaseIacAudit findByIac(String iac);
}
