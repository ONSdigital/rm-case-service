package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Address;
import uk.gov.ons.ctp.response.caseframe.domain.repository.AddressRepository;
import uk.gov.ons.ctp.response.caseframe.service.AddressService;

/**
 * An AddressService implementation which encapsulates all business logic
 * operating on the Address entity model.
 */
@Named
@Slf4j
public final class AddressServiceImpl implements AddressService {

  private static final int INWARD_POSTCODE = 3;
  private static final int POSTCODE_LENGTH = 8;

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
   * Format a postcode, uppercase, pad to 8 characters allowing for different
   * postcode formats.
   *
   * @param postcode A Postcode string
   * @return Formatted postcode or null
   */
  private String formatPostcode(final String postcode) {
    StringBuilder fullPostcode = null;

    String trimmedPostcode = postcode.trim().toUpperCase();
    int trimmedPostcodeLength = trimmedPostcode.length();
    if (trimmedPostcodeLength < POSTCODE_LENGTH) {
      // Pad spaces in centre of postcode
      String outwardPostCode = trimmedPostcode.substring(0, trimmedPostcodeLength - INWARD_POSTCODE);
      String inwardPostCode = trimmedPostcode.substring(trimmedPostcodeLength - INWARD_POSTCODE, trimmedPostcodeLength);
      fullPostcode = new StringBuilder(outwardPostCode);
      fullPostcode.append(String.format("%1$" + (POSTCODE_LENGTH - outwardPostCode.length()) + "s", inwardPostCode));
    } else {
      fullPostcode = new StringBuilder(trimmedPostcode);
    }

    log.debug("fullPostcode = {}", fullPostcode);
    return fullPostcode.toString();
  }
}
