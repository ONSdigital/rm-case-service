package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.Address;

/**
 * The Address Service interface defines all business behaviours for operations
 * on the Address entity model.
 */
public interface AddressService extends CTPService {

  /**
   * Find Address entity by Unique Property Reference No.
   *
   * @param uprn A UPRN Long
   * @return Address object or null
   */
  Address findByUprn(Long uprn);

  /**
   * Find Address entities by postcode.
   *
   * @param postcode A Postcode string
   * @return List of Questionnaire entities or empty List
   */
  List<Address> findByPostcode(String postcode);
}
