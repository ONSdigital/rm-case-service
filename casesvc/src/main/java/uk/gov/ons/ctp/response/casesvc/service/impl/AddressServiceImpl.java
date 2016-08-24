package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.Address;
import uk.gov.ons.ctp.response.casesvc.domain.repository.AddressRepository;
import uk.gov.ons.ctp.response.casesvc.service.AddressService;

/**
 * An AddressService implementation which encapsulates all business logic
 * operating on the Address entity model.
 */
@Named
@Slf4j
public class AddressServiceImpl implements AddressService {

  private static final int INWARD_POSTCODE = 3;

  /**
   * Spring Data Repository for Address entities.
   */
  @Inject
  private AddressRepository addressRepository;

  /**
   * Find Address entity by Unique Property Reference No.
   *
   * @param uprn A UPRN Long
   * @return Address object or null
   */
  @Override
  public Address findByUprn(final Long uprn) {
    log.debug("Entering findByUprn with {}", uprn);
    return addressRepository.findByUprn(uprn);
  }

  /**
   * Find Address entities by postcode.
   *
   * @param postcode A Postcode string
   * @return List of Questionnaire entities or empty List
   */
  @Override
  public List<Address> findByPostcode(final String postcode) {
    log.debug("Entering findByPostcode with {}", postcode);
    return addressRepository.findByPostcodeOrderByLine2Asc(formatPostcode(postcode));
  }

  /**
   * Format a postcode into the format used in the database.
   *
   * - convert the input to uppercase
   * - strip leading, inner and trailing whitespace from the input
   * - chop off the last three chars to be used as the inward component
   * - tack that onto the end of the remainder of the input with a single space between
   *
   * @param postcode A Postcode string
   * @return Formatted postcode
   */
  private String formatPostcode(final String postcode) {
    String formattedPostcode = null;

    String trimmedPostcode = postcode.trim().toUpperCase();
    trimmedPostcode = trimmedPostcode.replaceAll("[ ]*", "");
    int trimmedPostcodeLength = trimmedPostcode.length();
    String outwardPostCode = trimmedPostcode.substring(0, trimmedPostcodeLength - INWARD_POSTCODE);
    String inwardPostCode = trimmedPostcode.substring(trimmedPostcodeLength - INWARD_POSTCODE, trimmedPostcodeLength);
    formattedPostcode = outwardPostCode + " " + inwardPostCode;

    log.debug("fullPostcode = {}", formattedPostcode);
    return formattedPostcode;
  }
}
