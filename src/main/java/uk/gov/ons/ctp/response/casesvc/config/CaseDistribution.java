package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;

/** Config POJO for distribution params */
@Data
public class CaseDistribution {
  private Integer retrievalMax;
  private Integer retrySleepSeconds;
  private Long delayMilliSeconds;
}
