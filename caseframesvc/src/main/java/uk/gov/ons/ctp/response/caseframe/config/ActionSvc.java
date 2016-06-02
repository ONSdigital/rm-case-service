package uk.gov.ons.ctp.response.caseframe.config;

import lombok.Data;

/**
 * Application Config bean for the connection details to the Action Service
 *
 */
@Data
public class ActionSvc {
  private String scheme;
  private String host;
  private String port;
  private String actionsPath;
  private String cancelActionsPath;
}
