package uk.gov.ons.ctp.response.iac.representation;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object for representation of the IAC data in the context of its associated case.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class InternetAccessCodeCaseContextDTO {

  @NotNull
  private Integer caseId;

  @NotNull
  private String iac;

  private Boolean active;

  private Long uprn;

  @NotNull
  private Integer sampleId;
  
  @NotNull
  private Integer surveyId;
  
  @NotNull
  private Integer parentCaseId;
  
  @NotNull
  private String questionSet;

}
