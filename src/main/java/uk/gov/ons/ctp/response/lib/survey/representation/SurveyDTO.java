package uk.gov.ons.ctp.response.lib.survey.representation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Survey API representation */
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SurveyDTO {
  private String id;
  private String shortName;
  private String longName;
  private String surveyRef;
  private String legalBasis;
}
