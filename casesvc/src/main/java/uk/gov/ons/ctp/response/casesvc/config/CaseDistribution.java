package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;

/**
 * Config POJO for distribition params
 *
 */
@Data
@CoverageIgnore
public class CaseDistribution {
  private Integer iacMax;
  private Integer retrievalMax;
  private Integer distributionMax;
  private Integer retrySleepSeconds;
  private Long delayMilliSeconds;
}
