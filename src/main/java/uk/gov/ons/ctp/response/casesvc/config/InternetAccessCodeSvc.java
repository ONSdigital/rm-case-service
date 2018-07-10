package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.common.rest.RestUtilityConfig;

/** App config POJO for IAC service access - host/location and endpoint locations */
@Data
@CoverageIgnore
public class InternetAccessCodeSvc {
  private RestUtilityConfig connectionConfig;
  private String iacGetPath;
  private String iacPostPath;
  private String iacPutPath;
}
