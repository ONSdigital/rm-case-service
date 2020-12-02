package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtilityConfig;

/** Application Config bean for the connection details to the Action Service */
@Data
public class ActionSvc {
  private RestUtilityConfig connectionConfig;
  private String actionsPath;
  private String actionPlansPath;
}
