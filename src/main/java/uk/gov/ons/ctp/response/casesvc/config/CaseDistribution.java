package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;

/** Config POJO for distribution params */
@Data
@CoverageIgnore
public class CaseDistribution {
  private Integer retrievalMax;
  private Integer retrySleepSeconds;
  private Long delayMilliSeconds;
}
