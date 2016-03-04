package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Address;

/**
 * The interface defining the function of the Address service. The application
 * business logic should reside in it's implementation
 */
public interface AddressService extends CTPService {
  Address findByUprn(Long uprn);

  List<Address> findByPostcode(String postcode);
}
