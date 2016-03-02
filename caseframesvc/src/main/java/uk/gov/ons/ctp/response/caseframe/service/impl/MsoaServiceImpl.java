package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.AddressSummary;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;
import uk.gov.ons.ctp.response.caseframe.domain.repository.AddressRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.AddressSummaryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.MsoaRepository;
import uk.gov.ons.ctp.response.caseframe.service.MsoaService;

/**
 * An implementation of the MsoaService using JPA Repository class(es) The
 * business logic for the application should reside here.
 */
@Named
@Slf4j
public class MsoaServiceImpl implements MsoaService {

  @Inject
  MsoaRepository msoaRepository;

  @Inject
  AddressRepository addressRepository;

  @Inject
  AddressSummaryRepository addressSummaryRepository;
  
  @Override
  public Msoa findById(String msoaid) {
    log.debug("Entering findById with {}", msoaid);
    return msoaRepository.findOne(msoaid);
  }

  @Override
  public List<AddressSummary> findAllAddressSummariesByMsoaid(String msoaid) {
    log.debug("Entering findAllAddressSummariesByMsoaid with {}", msoaid);
    return addressSummaryRepository.findByMsoa11cd(msoaid);
  }
}
