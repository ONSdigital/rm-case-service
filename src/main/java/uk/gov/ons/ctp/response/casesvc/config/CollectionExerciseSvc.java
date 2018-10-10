package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.common.rest.RestUtilityConfig;

/** App config POJO for CollectionExercise service access */
@Data
@CoverageIgnore
public class CollectionExerciseSvc {
  private RestUtilityConfig connectionConfig;
  private String collectionExercisePath;
  private String collectionExerciseSurveyPath;
  private String collectionExercisesPath;
}
