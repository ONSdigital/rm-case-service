package uk.gov.ons.ctp.response.caseframe.utility;

import uk.gov.ons.ctp.response.caseframe.domain.model.Address;

/**
 * Created by philippe.brossier on 2/29/16.
 */
public final class AddressBuilder {

  public static final Integer ADDRESS_EASTINGS = 123456;
  public static final Integer ADDRESS_HTC = 11;
  public static final Integer ADDRESS_NORTHINGS = 345000;
  public static final Double ADDRESS_LATITUDE = 100000d;
  public static final Double ADDRESS_LONGITUDE = 300000d;
  public static final String ADDRESS_ESTABLISH_TYPE = "EX";
  public static final String ADDRESS_LINE1 = "Segensworth Road";
  public static final String ADDRESS_LINE2 = "Business Park";
  public static final String ADDRESS_TOWN_NAME = "Fareham";
  public static final String ADDRESS_OUTPUT_AREA = "outputA";
  public static final String ADDRESS_LSOA = "lsoa";
  public static final String ADDRESS_TYPE = "CE";
  public static final String ADDRESS_LAD = "lad123";
  public static final String ADDRESS_MSOA = "msoa123";
  public static final String ADDRESS_REGION_CODE = "reg123";

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
    address.setMsoa11cd(ADDRESS_MSOA);
    address.setLad12cd(ADDRESS_LAD);
    address.setRegion11cd(ADDRESS_REGION_CODE);
    address.setEstabType(ADDRESS_ESTABLISH_TYPE);
    address.setAddressLine1(ADDRESS_LINE1);
    address.setAddressLine2(ADDRESS_LINE2);
    address.setTownName(ADDRESS_TOWN_NAME);
    address.setOa11cd(ADDRESS_OUTPUT_AREA);
    address.setLsoa11cd(ADDRESS_LSOA);
    address.setAddressType(ADDRESS_TYPE);
    address.setEastings(ADDRESS_EASTINGS);
    address.setNorthings(ADDRESS_NORTHINGS);
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
