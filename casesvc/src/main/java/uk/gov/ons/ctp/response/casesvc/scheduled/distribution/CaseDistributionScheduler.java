package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

/**
 * This bean will have the caseDistributor injected into it by spring on
 * constructions. It will then schedule the running of the distributor using
 * details from the AppConfig
 */
@Named
public class CaseDistributionScheduler implements HealthIndicator {

  @Override
  public Health health() {
    return Health.up()
        .withDetail("activationInfo", distribInfo)
        .build();
  }

  @Inject
  private CaseDistributor caseDistributorImpl;

  private CaseDistributionInfo distribInfo = new CaseDistributionInfo();

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  /**
   * Create the scheduler for the Case Distributor
   *
   * @param applicationConfig injected app config needs injecting as cannot use
   *          the class appConfig - is not injected until this class created -
   *          chicken meet egg
   */
  @Inject
  public CaseDistributionScheduler(AppConfig applicationConfig) {
    final Runnable distributorRunnable = new Runnable() {
      @Override public void run() {
       //distribInfo = caseDistributorImpl.distribute();
      }
    };

    scheduler.scheduleAtFixedRate(distributorRunnable,
        applicationConfig.getCaseDistribution().getInitialDelaySeconds(),
        applicationConfig.getCaseDistribution().getSubsequentDelaySeconds(), SECONDS);
  }
}
