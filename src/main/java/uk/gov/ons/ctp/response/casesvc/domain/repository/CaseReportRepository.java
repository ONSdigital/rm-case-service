package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseReport;

/** The repository used to trigger stored procedure execution. */
public interface CaseReportRepository extends JpaRepository<CaseReport, UUID> {
  /**
   * To execute generate_response_chasing_report
   *
   * @return boolean whether report has been created successfully
   */
  @Modifying
  @Transactional
  @Procedure(name = "CaseReport.chasingReport")
  Boolean chasingReportStoredProcedure();

  /**
   * To execute generate_case_events_report
   *
   * @return boolean whether report has been created successfully
   */
  @Modifying
  @Transactional
  @Procedure(name = "CaseReport.caseEventsReport")
  Boolean caseEventsReportStoredProcedure();
}
