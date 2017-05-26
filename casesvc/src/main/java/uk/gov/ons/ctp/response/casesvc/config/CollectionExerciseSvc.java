package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import uk.gov.ons.ctp.common.rest.RestClientConfig;

@Data
public class CollectionExerciseSvc {
  private RestClientConfig connectionConfig;
  private String scheme;
  private String host;
  private String port;
  private String collectionExercisePath;


}