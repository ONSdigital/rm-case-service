package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.common.health.ScheduledHealthInfo;

/**
 * info regarding the last action distribution to handlers
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CaseDistributionInfo extends ScheduledHealthInfo {

  private long casesSucceeded;
  private long casesFailed;

}
