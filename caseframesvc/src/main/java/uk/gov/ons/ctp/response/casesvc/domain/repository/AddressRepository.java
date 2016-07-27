package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.Address;

/**
 * JPA Data Repository.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
  /**
   * find the Address by UPRN.
   * @param uprn to find by
   * @return the address or null if not found
   */
  Address findByUprn(Long uprn);

  /**
   * find the Address by postcode.
   * @param postcode to find by
   * @return the address or null if not found
   */
  List<Address> findByPostcodeOrderByLine2Asc(String postcode);
}
