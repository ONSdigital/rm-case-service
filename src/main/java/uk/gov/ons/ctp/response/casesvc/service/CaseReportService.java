package uk.gov.ons.ctp.response.casesvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseReportRepository;

/**
 * A CaseReportService implementation which encapsulates all business logic operating on the
 * CaseReport entity model.
 */
@Service
public class CaseReportService {
  private static final Logger log = LoggerFactory.getLogger(CaseReportService.class);

  @Autowired private CaseReportRepository caseReportRepository;

  /** Creates a Report */
  public void createReport() {
    log.debug("Entering createReport...");

    boolean reportResult = caseReportRepository.chasingReportStoredProcedure();
    log.with("report_result", reportResult).debug("Just ran the chasing report");

    reportResult = caseReportRepository.caseEventsReportStoredProcedure();
    log.with("report_result", reportResult).debug("Just ran the case events report");
  }
}
