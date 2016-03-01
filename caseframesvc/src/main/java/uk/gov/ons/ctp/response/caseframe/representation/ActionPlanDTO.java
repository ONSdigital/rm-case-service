package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.Data;

/**
 * Domain model object
 */
@Data
public class ActionPlanDTO {


  private Integer actionPlanId;

  private String actionPlanName;

  private String description;

  private String rules;

}
