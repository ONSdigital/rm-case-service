package uk.gov.ons.ctp.response.caseframe.config;

import lombok.Data;

@Data
public class ActionSvc {
  private String scheme;
  private String host;
  private String port;
  private String actionsPath;
}
