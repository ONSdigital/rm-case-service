package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

/**
 * This bean will have the caseDistributor injected into it by spring on
 * constructions. It will then schedule the running of the distributor using
 * details from the AppConfig
 */
@Named
@Slf4j
public class CaseDistributionScheduler implements HealthIndicator {

  @Override
  public Health health() {
    return Health.up()
        .withDetail("activationInfo", distribInfo)
        .build();
  }

  @Inject
  private CaseDistributor caseDistributorImpl;
  
  @Inject
  private AppConfig applicationConfig;

  private CaseDistributionInfo distribInfo = new CaseDistributionInfo();

  /**
   * Create the scheduler for the Case Distributor
   *
   * @param applicationConfig injected app config needs injecting as cannot use
   *          the class appConfig - is not injected until this class created -
   *          chicken meet egg
   */
  @PostConstruct
  public void run() {
    final Runnable distributorRunnable = new Runnable() {
      @Override public void run() {
        try {
          distribInfo = caseDistributorImpl.distribute();
        } catch (Exception e) {
          log.error("Exception in case distributor", e);
        }
      }
    };

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleWithFixedDelay(distributorRunnable,
        applicationConfig.getCaseDistribution().getInitialDelayMilliSeconds(),
        applicationConfig.getCaseDistribution().getSubsequentDelayMilliSeconds(), MILLISECONDS);
  }
  
}
