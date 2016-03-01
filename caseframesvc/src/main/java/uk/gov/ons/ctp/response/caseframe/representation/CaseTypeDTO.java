package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.Data;

/**
 */
@Data
public class CaseTypeDTO {


  private Integer caseTypeId;

  private String caseTypeName;

  private String description;

  private Integer actionPlanId;

  private String questionSet;

}


