package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;
import uk.gov.ons.ctp.response.caseframe.domain.repository.LocalAuthorityRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.MsoaRepository;
import uk.gov.ons.ctp.response.caseframe.service.LocalAuthorityService;

/**
 * A LocalAuthorityService implementation which encapsulates all business logic
 * operating on the LocalAuthority entity model.
 */
@Named
@Slf4j
public final class LocalAuthorityServiceImpl implements LocalAuthorityService {

  /**
   * Spring Data Repository for LocalAuthoritye entities.
   */
  @Inject
  private LocalAuthorityRepository localAuthorityRepository;

  /**
   * Spring Data Repository for MSOA entities.
   */
  @Inject
  private MsoaRepository msoaRepository;

  /**
   * Find Local Authority entity by unique Id.
   *
   * @param ladid Unique LAD Id
   * @return LocalAuthority entity or null
   */
  @Override
  public LocalAuthority findById(final String ladid) {
    log.debug("entering findById with {}", ladid);
    return localAuthorityRepository.findOne(ladid);
  }

  /**
   * Returns all MSOAs for a given LAD Id sorted by MSOA name ascending.
   *
   * @param ladid Unique LAD Id
   * @return List of MSOA entities or empty List
   */
  @Override
  public List<Msoa> findAllMsoasByLadid(final String ladid) {
    log.debug("entering findAllMsoasByLadid with {}", ladid);
    LocalAuthority lad = localAuthorityRepository.findOne(ladid);
    if (lad != null) {
      return msoaRepository.findByLad12cdOrderByMsoa11nm(ladid);
    } else {
      return null;
    }
  }
}
