package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtilityConfig;

/** App config POJO for IAC service access - host/location and endpoint locations */
@Data
public class InternetAccessCodeSvc {
  private RestUtilityConfig connectionConfig;
  private String iacGetPath;
  private String iacPostPath;
  private String iacPutPath;
}
