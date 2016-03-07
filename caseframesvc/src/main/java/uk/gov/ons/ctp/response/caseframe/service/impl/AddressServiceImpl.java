package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Address;
import uk.gov.ons.ctp.response.caseframe.domain.repository.AddressRepository;
import uk.gov.ons.ctp.response.caseframe.service.AddressService;

/**
 * An implementation of the AddressService using JPA Repository class(es) The
 * business logic for the application should reside here.
 */
@Named
@Slf4j
public final class AddressServiceImpl implements AddressService {

  @Inject
  AddressRepository addressRepository;

  @Override
  public Address findByUprn(Long uprn) {
    log.debug("Entering findByUprn with {}", uprn);
    return addressRepository.findByUprn(uprn);
  }

  @Override
  public List<Address> findByPostcode(String postcode) {
    log.debug("Entering findByPostcode with {}", postcode);
    return addressRepository.findByPostcode(formatPostcode(postcode));
  }

  private String formatPostcode(String postcode) {
    StringBuilder fullPostcode = null;

    String trimmedPostcode = postcode.trim();
    int trimmedPostcodeLength = trimmedPostcode.length();
    if (trimmedPostcodeLength < 8) {
      // case where the space was forgotten in the middle of the postcode
      String postcodeStart = trimmedPostcode.substring(0, trimmedPostcodeLength - 3);
      fullPostcode = new StringBuilder(postcodeStart);
      fullPostcode.append(" ");
      String lastThreeChracters = trimmedPostcode.substring(trimmedPostcodeLength - 3, trimmedPostcodeLength);
      fullPostcode.append(lastThreeChracters);
    } else {
      fullPostcode = new StringBuilder(trimmedPostcode);
    }

    log.debug("fullPostcode = {}", fullPostcode);
    return fullPostcode.toString();
  }
}
