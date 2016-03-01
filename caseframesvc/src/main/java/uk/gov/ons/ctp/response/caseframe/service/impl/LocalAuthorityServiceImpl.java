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
 * An implementation of the AddressService using JPA Repository class(es)
 * The business logic for the application should reside here.
 */
@Named
@Slf4j
public class LocalAuthorityServiceImpl implements LocalAuthorityService {

  @Inject
  private LocalAuthorityRepository localAuthorityRepository;

  @Inject
  private MsoaRepository msoaRepository;

  public LocalAuthority findById(String ladid) {
    log.debug("entering findById with {}", ladid);
    return localAuthorityRepository.findOne(ladid);
  }

  public List<Msoa> findAllMsoasByLadid(String ladid){
    log.debug("entering findAllMsoasByLadid with {}", ladid);
    return msoaRepository.findByLad12cdOrderByMsoa11nm(ladid);
  }
}
