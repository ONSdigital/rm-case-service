package uk.gov.ons.ctp.response.casesvc.scheduled.distribution;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.common.health.ScheduledHealthInfo;

/** info regarding the last case distribution to handlers */
@CoverageIgnore
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CaseDistributionInfo extends ScheduledHealthInfo {

  private long casesSucceeded;
  private long casesFailed;
}
