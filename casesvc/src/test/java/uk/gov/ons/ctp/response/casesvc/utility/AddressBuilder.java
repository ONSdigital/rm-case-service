package uk.gov.ons.ctp.response.casesvc.utility;

import uk.gov.ons.ctp.response.casesvc.domain.model.Address;

public final class AddressBuilder {

  public static final Integer ADDRESS_HTC = 11;
  public static final Long ADDRESS_UPRN = 123L;
  public static final Long ADDRESS_NON_EXISTING_UPRN = 999L;
  public static final Long ADDRESS_WITH_UPRN_CHECKED_EXCEPTION = 666L;
  public static final Double ADDRESS_LATITUDE = 100000d;
  public static final Double ADDRESS_LONGITUDE = 300000d;
  public static final String ADDRESS_ESTABLISH_TYPE = "EX";
  public static final String ADDRESS_ORG_NAME = "Tescos";
  public static final String ADDRESS_LOCALITY = "Somewhere";
  public static final String ADDRESS_LINE1 = "Segensworth Road";
  public static final String ADDRESS_LINE2 = "Business Park";
  public static final String ADDRESS_TOWN_NAME = "Fareham";
  public static final String ADDRESS_OUTPUT_AREA = "outputA";
  public static final String ADDRESS_LSOA = "lsoa";
  public static final String ADDRESS_TYPE = "CE";
  public static final String ADDRESS_LAD = "lad123";
  public static final String ADDRESS_MSOA = "msoa123";
  public static final String ADDRESS_REGION_CODE = "reg123";
  public static final String ADDRESS_POSTCODE = "PO15 5RR";
  public static final String ADDRESS_NON_EXISTING_POSTCODE = "PORANDOM";

  private Long uprn;
  private String postcode;

  /**
   * hidden constructor
   */
  private AddressBuilder() {
  }

  /**
   * builder method taking postcode
   * @param theUprn uprn
   * @return the builder
   */
  public AddressBuilder uprn(final Long theUprn) {
    this.uprn = theUprn;
    return this;
  }

  /**
   * builder method taking postcode
   * @param thePostcode the postcode
   * @return the builder
   */
  public AddressBuilder postcode(final String thePostcode) {
    this.postcode = thePostcode;
    return this;
  }

  /**
   * builder
   * @return the builder
   */
  public Address buildAddress() {
    Address address = new Address();
    address.setUprn(this.uprn);
    address.setPostcode(this.postcode);
    address.setMsoa(ADDRESS_MSOA);
    address.setLad(ADDRESS_LAD);
    address.setRegion(ADDRESS_REGION_CODE);
    address.setOrganisationName(ADDRESS_ORG_NAME);
    address.setLocality(ADDRESS_LOCALITY);
    address.setEstabType(ADDRESS_ESTABLISH_TYPE);
    address.setLine1(ADDRESS_LINE1);
    address.setLine2(ADDRESS_LINE2);
    address.setTownName(ADDRESS_TOWN_NAME);
    address.setOa(ADDRESS_OUTPUT_AREA);
    address.setLsoa(ADDRESS_LSOA);
    address.setType(ADDRESS_TYPE);
    address.setHtc(ADDRESS_HTC);
    address.setLatitude(ADDRESS_LATITUDE);
    address.setLongitude(ADDRESS_LONGITUDE);
    return address;
  }

  /**
   * builder method
   * @return the builder
   */
  public static AddressBuilder address() {
    return new AddressBuilder();
  }
}
