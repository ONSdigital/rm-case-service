package uk.gov.ons.ctp.response.lib.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** RestUtility Configuration */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestUtilityConfig {
  @Builder.Default private String scheme = "http";
  @Builder.Default private String host = "localhost";
  @Builder.Default private String port = "8080";
  private String username;
  private String password;
}
