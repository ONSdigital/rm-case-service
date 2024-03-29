package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtilityConfig;

/** App config POJO for CollectionExercise service access */
@Data
public class CollectionExerciseSvc {
  private RestUtilityConfig connectionConfig;
  private String collectionExercisePath;
  private String collectionExerciseSurveyPath;
  private String collectionExercisesPath;
}
