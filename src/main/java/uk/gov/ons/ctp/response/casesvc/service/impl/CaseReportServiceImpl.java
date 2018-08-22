package uk.gov.ons.ctp.response.casesvc.service.impl;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseReportRepository;
import uk.gov.ons.ctp.response.casesvc.service.CaseReportService;

/**
 * A CaseReportService implementation which encapsulates all business logic operating on the
 * CaseReport entity model.
 */
@Service
public class CaseReportServiceImpl implements CaseReportService {
  private static final Logger log = LoggerFactory.getLogger(CaseReportServiceImpl.class);

  @Autowired private CaseReportRepository caseReportRepository;

  /** Creates a Report */
  @Override
  public void createReport() {
    log.debug("Entering createReport...");

    boolean reportResult = caseReportRepository.chasingReportStoredProcedure();
    log.debug("Just ran the chasing report and result is {}", reportResult);

    reportResult = caseReportRepository.caseEventsReportStoredProcedure();
    log.debug("Just ran the case events report and result is {}", reportResult);
  }
}
