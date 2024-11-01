package uk.gov.ons.ctp.response.lib.action;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model object for representation. */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ActionDTO {
  /** enum for action state */
  public enum ActionState {
    SUBMITTED,
    PENDING,
    ACTIVE,
    COMPLETED,
    DECLINED,
    CANCEL_SUBMITTED,
    CANCEL_PENDING,
    CANCELLING,
    CANCELLED,
    ABORTED;
  }

  /** enum for action event */
  public enum ActionEvent {
    REQUEST_DISTRIBUTED,
    REQUEST_FAILED,
    REQUEST_ACCEPTED,
    REQUEST_COMPLETED,
    REQUEST_DECLINED,
    REQUEST_COMPLETED_DEACTIVATE,
    REQUEST_COMPLETED_DISABLE,
    REQUEST_CANCELLED,
    CANCELLATION_DISTRIBUTED,
    CANCELLATION_FAILED,
    CANCELLATION_ACCEPTED,
    CANCELLATION_COMPLETED
  }

  private UUID id;

  @NotNull private UUID caseId;

  private UUID actionPlanId;

  private boolean activeEnrolment;

  private UUID actionRuleId;

  @NotNull private String actionTypeName;

  @NotNull private String createdBy;

  private Boolean manuallyCreated;

  private Integer priority = 3;

  private String situation;

  private ActionState state;

  private Date createdDateTime;

  private Date updatedDateTime;
}
