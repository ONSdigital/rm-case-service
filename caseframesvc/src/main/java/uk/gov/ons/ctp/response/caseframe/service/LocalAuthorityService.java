package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;

/**
 * The LocalAuthority Service interface defines all business behaviours for
 * operations on the LAD entity model.
 */
public interface LocalAuthorityService extends CTPService {

  /**
   * Find Local Authority entity by unique Id.
   *
   * @param ladid Unique LAD Id
   * @return LocalAuthority entity or null
   */
  LocalAuthority findById(String ladid);

  /**
   * Returns all MSOAs for a given LAD Id sorted by MSOA name ascending.
   *
   * @param ladid Unique LAD Id
   * @return List of MSOA entities or empty List
   */
  List<Msoa> findAllMsoasByLadid(String ladid);
}
