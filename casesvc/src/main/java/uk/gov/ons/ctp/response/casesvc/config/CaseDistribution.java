package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;

/**
 * Config POJO for distribition params
 *
 */
@Data
public class CaseDistribution {
  private Integer iacMax;
  private Integer retrievalMax;
  private Integer distributionMax;
  private Integer retrySleepSeconds;
  private Integer initialDelaySeconds;
  private Integer subsequentDelaySeconds;
}
