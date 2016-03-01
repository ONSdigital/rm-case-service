package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.Data;

/**
 */
@Data
public class AddressDTO {

  private Long uprn;

  private String addressType;

  private String estabType;

  private String addressLine1;

  private String addressLine2;

  private String townName;

  private String postcode;

  private String outputArea;

  private String lsoaArea;

  private String msoaArea;

  private String ladCode;

  private String regionCode;

  private Integer eastings;

  private Integer northings;

  private Integer htc;

  private Double latitude;

  private Double longitude;


}
