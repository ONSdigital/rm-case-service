package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Region;
import uk.gov.ons.ctp.response.caseframe.domain.repository.LocalAuthorityRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.RegionRepository;
import uk.gov.ons.ctp.response.caseframe.service.RegionService;

/**
 * An implementation of the RegionService using JPA Repository class(es) The
 * business logic for the application should reside here.
 */
@Named
@Slf4j
public class RegionServiceImpl implements RegionService {

  @Inject
  private RegionRepository regionRepository;

  @Inject
  private LocalAuthorityRepository localAuthorityRepository;

  @Override
  public List<Region> findAll() {
    log.debug("Entering findAll");
    return regionRepository.findAllByOrderByRgn11cd();
  }

  @Override
  public Region findById(String regionid) {
    log.debug("Entering findById with {}", regionid);
    return regionRepository.findOne(regionid);
  }

  @Override
  public List<LocalAuthority> findAllLadsByRegionid(String regionid) {
    log.debug("Entering findAllLadsByRegionid with {}", regionid);
    return localAuthorityRepository.findByRgn11cdOrderByLad12nm(regionid);
  }
}
