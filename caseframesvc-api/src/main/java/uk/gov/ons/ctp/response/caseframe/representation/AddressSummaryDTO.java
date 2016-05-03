package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AddressSummaryDTO {

  private Long uprn;

  private String type;

  private String estabType;

  private String locality;

  private String organisationName;

  private String line1;

  private String line2;

  private String townName;

  private String postcode;

}
