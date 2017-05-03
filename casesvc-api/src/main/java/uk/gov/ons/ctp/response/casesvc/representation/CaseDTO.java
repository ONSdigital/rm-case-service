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
    SAMPLED_INIT, REPLACEMENT_INIT, ACTIONABLE, INACTIONABLE;
  }

  /**
   * enum for Case event 
   */
  public enum CaseEvent {
    ACTIVATED, REPLACED, DEACTIVATED, DISABLED
  }

  private Integer caseId;

  private Integer caseGroupId;

  private Integer actionPlanId;
  
  private String sampleUnitType;
  
  private String sampleUnitRef;
  
  private String partyId;

  private String caseRef;

  private CaseState state;

  private Date createdDateTime;

  private String createdBy;

  private String iac;
  
  private ResponseDTO[] responses;


}
