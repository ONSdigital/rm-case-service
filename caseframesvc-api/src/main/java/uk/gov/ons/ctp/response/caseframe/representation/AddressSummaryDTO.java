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

  private String addressType;

  private String estabType;

  private String locality;

  private String organisationName;

  private String addressLine1;

  private String addressLine2;

  private String townName;

  private String postcode;

}
