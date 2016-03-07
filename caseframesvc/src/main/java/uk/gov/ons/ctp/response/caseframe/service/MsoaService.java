package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.AddressSummary;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;

/**
 * The MSOA Service interface defines all business behaviours for operations on
 * the Mid Layer Super Output Area entity model.
 */
public interface MsoaService extends CTPService {

  /**
   * Find MSOA entity by unique Id.
   *
   * @param msoaid Unique MSOA Id
   * @return Msoa object or null
   */
  Msoa findById(String msoaid);

  /**
   * Find Address summary entities associated with an MSOA.
   *
   * @param msoaid MSOA Id Integer
   * @return List of AddressSummary entities or empty List
   */
  List<AddressSummary> findAllAddressSummariesByMsoaid(String msoaid);
}
