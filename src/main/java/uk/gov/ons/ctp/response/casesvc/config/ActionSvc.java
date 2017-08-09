package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.common.rest.RestClientConfig;

/**
 * Application Config bean for the connection details to the Action Service
 *
 */
@Data
@CoverageIgnore
public class ActionSvc {
  private RestClientConfig connectionConfig;
  private String scheme;
  private String host;
  private String port;
  private String actionsPath;
}
