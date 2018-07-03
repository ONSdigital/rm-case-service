package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;

/** Jpa Repository for CaseIacAudit table */
@Repository
public interface CaseIacAuditRepository extends JpaRepository<CaseIacAudit, Integer> {}
