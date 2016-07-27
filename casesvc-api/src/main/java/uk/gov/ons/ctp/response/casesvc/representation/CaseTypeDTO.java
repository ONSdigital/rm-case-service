package uk.gov.ons.ctp.response.casesvc.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseTypeDTO {

  /**
   * enum for case type name
   */
  public enum CaseTypeName {
    HH, CH, HGH
  }

  private Integer caseTypeId;

  private String name;

  private String description;

  private Integer actionPlanId;

  private String questionSet;

}
