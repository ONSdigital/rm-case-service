package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.Data;

/**
 * Geography type and code for generate_cases StoredProcedure
 *
 */
@Data
public class GeographyDTO {

  private String geographyType;
  private String geographyCode;

}
