package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtilityConfig;

/** App config POJO for survey service access - host location and endpoint locations */
@Data
public class SurveySvc {
  private RestUtilityConfig connectionConfig;
  private String requestSurveyPath;
  private String multipleFormTypeSupportedSurveysIds;
  private String multipleFormTypeSupported;
}
