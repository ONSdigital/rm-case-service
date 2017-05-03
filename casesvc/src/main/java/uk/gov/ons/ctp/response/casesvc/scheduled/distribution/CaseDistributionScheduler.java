package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This bean will have the caseDistributor injected into it by spring on
 * constructions. It will then schedule the running of the distributor using
 * details from the AppConfig
 */
@Component
@Slf4j
public class CaseDistributionScheduler implements HealthIndicator {

  @Override
  public Health health() {
    return Health.up()
        .withDetail("activationInfo", distribInfo)
        .build();
  }

  @Autowired
  private CaseDistributor caseDistributorImpl;

  private CaseDistributionInfo distribInfo = new CaseDistributionInfo();

  /**
   * Create the scheduler for the Case Distributor
   *
   */

  @Scheduled(fixedDelayString = "#{appConfig.caseDistribution.delayMilliSeconds}")
  public void run() {
    try {
      distribInfo = caseDistributorImpl.distribute();
    } catch (Exception e) {
      log.error("Exception in case distributor", e);
    }
  }

}
