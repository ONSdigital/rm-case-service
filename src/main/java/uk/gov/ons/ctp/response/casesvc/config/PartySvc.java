package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtilityConfig;

/** App config POJO for Party service access - host/location and endpoint locations */
@Data
public class PartySvc {
  private RestUtilityConfig connectionConfig;
  private String partyBySampleUnitTypeAndIdPath;
}
