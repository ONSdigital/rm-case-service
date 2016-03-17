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
 * A RegionService implementation which encapsulates all business logic
 * operating on the Region entity model.
 */
@Named
@Slf4j
public final class RegionServiceImpl implements RegionService {

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
  public Region findByRegionId(final String regionid) {
    log.debug("Entering findById with {}", regionid);
    return regionRepository.findOne(regionid);
  }

  @Override
  public List<LocalAuthority> findAllLadsByRegionid(final String regionid) {
    log.debug("Entering findAllLadsByRegionid with {}", regionid);
    Region region = regionRepository.findOne(regionid);
    if (region != null) {
      return localAuthorityRepository.findByRgn11cdOrderByLad12nm(regionid);
    } else {
      return null;
    }
  }
}
