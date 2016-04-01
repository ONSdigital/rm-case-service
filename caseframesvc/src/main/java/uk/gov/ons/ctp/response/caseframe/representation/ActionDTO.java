package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * The object to represent an Action
 */
@Data
public class ActionDTO {

  /**
   * The enum for all possible action states
   */
  public enum ActionState {
    ACTIVE, CANCELLED, CANCELSUBMITTED, COMPLETED, FAILED, PENDING, SUBMITTED;
  }

  @NotNull
  private Integer caseId;

  @NotNull
  private String actionTypeName;

  @NotNull
  private String createdBy;

  private Integer actionId;

  private Integer actionPlanId;

  private Integer actionRuleId;

  private Boolean manuallyCreated;

  private Integer priority = 3;

  private String situation;

  private ActionState state;

  private Date createdDateTime;

  private Date updatedDateTime;
}
