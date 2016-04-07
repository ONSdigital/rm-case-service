package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.Data;

/**
 */
@Data
public class SampleDTO {

  private Integer sampleId;

  private String sampleName;

  private String description;

  private String addressCriteria;

  private Integer caseTypeId;

  private Integer surveyId;

}
