package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroupStatusAudit;

@Repository
public interface CaseGroupStatusAuditRepository
    extends JpaRepository<CaseGroupStatusAudit, Integer> {}
