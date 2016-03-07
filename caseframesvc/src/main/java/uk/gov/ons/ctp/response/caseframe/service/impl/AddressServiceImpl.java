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

  public static final int INWARD_POSTCODE = 3;
  public static final int POSTCODE_LENGTH = 8;

  @Inject
  AddressRepository addressRepository;

  @Override
  public Address findByUprn(final Long uprn) {
    log.debug("Entering findByUprn with {}", uprn);
    return addressRepository.findByUprn(uprn);
  }

  @Override
  public List<Address> findByPostcode(final String postcode) {
    log.debug("Entering findByPostcode with {}", postcode);
    return addressRepository.findByPostcode(formatPostcode(postcode));
  }

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
