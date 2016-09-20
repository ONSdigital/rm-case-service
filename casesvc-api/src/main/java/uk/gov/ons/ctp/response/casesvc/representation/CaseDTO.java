package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseDTO {

  /**
   * enum for case state
   */
  public enum CaseState {
    INIT, ACTIVE, INACTIVE, RESPONDED;
  }

  /**
   * enum for Case event
   */
  public enum CaseEvent {
    ACTIVATED, DEACTIVATED, RESPONSE_RECEIVED
  }

  private Integer caseId;

  private Long uprn;

  private CaseState state;

  private Integer caseTypeId;

  private Date createdDateTime;

  private String createdBy;

  private Integer sampleId;

  private Integer actionPlanId;

  private Integer surveyId;

  private String questionSet;

}
