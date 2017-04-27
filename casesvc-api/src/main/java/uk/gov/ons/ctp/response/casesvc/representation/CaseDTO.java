package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.ons.ctp.common.time.DateTimeUtil.DATE_FORMAT_IN_JSON;

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

  private String caseRef;

  private CaseState state;

  private Integer caseTypeId;

  private Integer actionPlanMappingId;

  @JsonFormat(pattern = DATE_FORMAT_IN_JSON)
  private Date createdDateTime;

  private String createdBy;

  private String iac;
  
  private ResponseDTO[] responses;

  private ContactDTO contact;

}
