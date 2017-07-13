package uk.gov.ons.ctp.response.casesvc.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.service.CaseReportService;

/**
 * The scheduler to trigger reports creation based on a cron expression defined in application.yml
 */
@Component
@Slf4j
public class ReportScheduler {

    @Autowired
    private CaseReportService caseReportService;

    @Scheduled(cron = "#{appConfig.reportSettings.cronExpression}")
    public void createReport() {
        log.debug("Entering createReport...");
        // TODO Ensure only 1 instance does create the report
        caseReportService.createReport();
    }
}
