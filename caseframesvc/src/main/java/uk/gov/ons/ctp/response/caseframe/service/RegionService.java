package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Region;

/**
 * The interface defining the function of the Address service. The application
 * business logic should reside in it's implementation
 */
public interface RegionService extends CTPService {

  /**
   * Returns all regions sorted by region code ascending.
   *
   * @return List of Region entities or empty List
   */
  List<Region> findAll();

  /**
   * Find Region entity by Region Id.
   *
   * @param regionid Unique Region Id
   * @return Region object or null
   */
  Region findByRegionId(String regionid);

  /**
   * Returns all local authorities for a given Region Id sorted by LAD name
   * ascending.
   *
   * @param regionid Unique Region Id
   * @return List of LocalAuthority entities or null
   */
  List<LocalAuthority> findAllLadsByRegionid(String regionid);
}
