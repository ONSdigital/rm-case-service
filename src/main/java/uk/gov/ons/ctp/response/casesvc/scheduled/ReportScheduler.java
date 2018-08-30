package uk.gov.ons.ctp.response.casesvc.scheduled;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.distributed.DistributedInstanceManager;
import uk.gov.ons.ctp.common.distributed.DistributedLatchManager;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.response.casesvc.service.CaseReportService;

/**
 * The scheduler to trigger reports creation based on a cron expression defined in application.yml
 */
@Component
public class ReportScheduler {
  private static final Logger log = LoggerFactory.getLogger(ReportScheduler.class);

  private static final String DISTRIBUTED_OBJECT_KEY_REPORT_LATCH = "reportlatch";
  private static final String DISTRIBUTED_OBJECT_KEY_INSTANCE_COUNT = "reportscheduler";
  private static final String DISTRIBUTED_OBJECT_KEY_REPORT = "report";

  @Autowired private CaseReportService caseReportService;

  @Autowired private DistributedLockManager reportDistributedLockManager;

  @Autowired private DistributedInstanceManager reportDistributedInstanceManager;

  @Autowired private DistributedLatchManager reportDistributedLatchManager;

  /** Initialise report scheduler */
  @PostConstruct
  public void init() {
    reportDistributedInstanceManager.incrementInstanceCount(DISTRIBUTED_OBJECT_KEY_INSTANCE_COUNT);
    log.with(
            "instance_count",
            reportDistributedInstanceManager.getInstanceCount(
                DISTRIBUTED_OBJECT_KEY_INSTANCE_COUNT))
        .info("Redis instance(s) running");
  }

  /** Clean up report scheduler on bean destruction */
  @PreDestroy
  public void cleanUp() {
    reportDistributedInstanceManager.decrementInstanceCount(DISTRIBUTED_OBJECT_KEY_INSTANCE_COUNT);
    reportDistributedLockManager.unlockInstanceLocks();
    log.with(
            "instance_count",
            reportDistributedInstanceManager.getInstanceCount(
                DISTRIBUTED_OBJECT_KEY_INSTANCE_COUNT))
        .info("Redis instance(s) running");
  }

  /** The method triggering report creation. */
  @Scheduled(cron = "#{appConfig.reportSettings.cronExpression}")
  public void createReport() {
    log.debug("Entering createReport...");

    reportDistributedLatchManager.setCountDownLatch(
        DISTRIBUTED_OBJECT_KEY_REPORT_LATCH,
        reportDistributedInstanceManager.getInstanceCount(DISTRIBUTED_OBJECT_KEY_INSTANCE_COUNT));

    if (!reportDistributedLockManager.isLocked(DISTRIBUTED_OBJECT_KEY_REPORT)) {
      if (reportDistributedLockManager.lock(DISTRIBUTED_OBJECT_KEY_REPORT)) {
        caseReportService.createReport();
      }
    }

    try {
      reportDistributedLatchManager.countDown(DISTRIBUTED_OBJECT_KEY_REPORT_LATCH);
      if (!reportDistributedLatchManager.awaitCountDownLatch(DISTRIBUTED_OBJECT_KEY_REPORT_LATCH)) {
        log.with(
                "instance_count",
                reportDistributedInstanceManager.getInstanceCount(
                    DISTRIBUTED_OBJECT_KEY_INSTANCE_COUNT))
            .error("Report run error countdownlatch timed out");
      }
    } catch (InterruptedException e) {
      log.error("Report run error waiting for countdownlatch", e);
    } finally {
      reportDistributedLockManager.unlock(DISTRIBUTED_OBJECT_KEY_REPORT);
      reportDistributedLatchManager.deleteCountDownLatch(DISTRIBUTED_OBJECT_KEY_REPORT_LATCH);
    }
  }
}
