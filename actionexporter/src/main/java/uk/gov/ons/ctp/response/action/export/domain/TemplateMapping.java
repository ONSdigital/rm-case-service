package uk.gov.ons.ctp.response.action.export.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object used to extract template mappings.
 */
@Data
@NoArgsConstructor
public class TemplateMapping {

  private String actionType;
  private String template;
  private String file;

}
