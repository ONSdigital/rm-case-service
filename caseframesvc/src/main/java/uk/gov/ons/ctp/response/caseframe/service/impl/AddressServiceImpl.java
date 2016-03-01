package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Address;
import uk.gov.ons.ctp.response.caseframe.domain.repository.AddressRepository;
import uk.gov.ons.ctp.response.caseframe.service.AddressService;
/**
 * An implementation of the AddressService using JPA Repository class(es)
 * The business logic for the application should reside here.
 */
@Named
@Slf4j
public class AddressServiceImpl implements AddressService {

  @Inject
  AddressRepository addressRepository;

  public Address findByUprn(Long uprn) {
    log.debug("Entering findByUprn with {}", uprn);
    return addressRepository.findByUprn(uprn);
  }

  public List<Address> findByPostcode(String postcode) {
    log.debug("Entering findByPostcode with {}", postcode);
    return addressRepository.findByPostcode(postcode);
  }
}
