package uk.gov.ons.ctp.response.casesvc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.service.CaseReportService;

@Service
@Slf4j
public class CaseReportServiceImpl implements CaseReportService {

    @Override
    public void createReport() {
        log.debug("Entering createReport...");
        // TODO define a ReportRepository reportRepository and invoke stored proc
    }
}
