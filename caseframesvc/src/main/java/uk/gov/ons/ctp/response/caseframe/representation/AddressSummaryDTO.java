package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.Data;

/**
 */
@Data
public class AddressSummaryDTO {

  private Long uprn;

  private String addressType;

  private String estabType;

  private String addressLine1;

  private String addressLine2;

  private String townName;

  private String postcode;

}
