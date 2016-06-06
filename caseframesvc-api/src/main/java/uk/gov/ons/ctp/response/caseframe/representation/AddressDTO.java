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
public class AddressDTO {

  private Long uprn;

  private String type;

  private String estabType;

  private String category;

  private String locality;

  private String organisationName;

  private String line1;

  private String line2;

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
