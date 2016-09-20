package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import uk.gov.ons.ctp.common.rest.RestClientConfig;

/**
 * App config POJO for Case service access - host/location and endpoint locations
 *
 */
@Data
public class InternetAccessCodeSvc {
  private RestClientConfig connectionConfig;
  private String iacPostPath;
}
