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
  private String scheme = "http";
  private String host = "localhost";
  private String port = "8080";
  private String username;
  private String password;
}
