package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.AddressSummary;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;
import uk.gov.ons.ctp.response.caseframe.domain.repository.AddressSummaryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.MsoaRepository;
import uk.gov.ons.ctp.response.caseframe.service.MsoaService;

/**
 * A MSOAService implementation which encapsulates all business logic operating
 * on the MSOA entity model.
 */
@Named
@Slf4j
public final class MsoaServiceImpl implements MsoaService {

  /**
   * Spring Data Repository for MSOA entities.
   */
  @Inject
  private MsoaRepository msoaRepository;

  /**
   * Spring Data Repository for AddressSummary entities.
   */
  @Inject
  private AddressSummaryRepository addressSummaryRepository;

  /**
   * Find MSOA entity by unique Id.
   *
   * @param msoaid Unique MSOA Id
   * @return Msoa object or null
   */
  @Override
  public Msoa findById(final String msoaid) {
    log.debug("Entering findById with {}", msoaid);
    return msoaRepository.findOne(msoaid);
  }

  /**
   * Find Address summary entities associated with an MSOA.
   *
   * @param msoaid MSOA Id Integer
   * @return List of AddressSummary entities or empty List
   */
  @Override
  public List<AddressSummary> findAllAddressSummariesByMsoaid(final String msoaid) {
    log.debug("Entering findAllAddressSummariesByMsoaid with {}", msoaid);
    Msoa msoa = msoaRepository.findOne(msoaid);
    if (msoa != null) {
      return addressSummaryRepository.findByMsoa11cd(msoaid);
    } else {
      return null;
    }
  }
}
