package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.Sample;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseLifeCycleRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.SampleRepository;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;

/**
 * A SampleService implementation which encapsulates all business logic
 * operating on the Sample entity model.
 */
@Named
@Slf4j
public class SampleServiceImpl implements SampleService {

  private static final int TRANSACTION_TIMEOUT = 120;


  @Inject
  private SampleRepository sampleRepo;

  @Inject
  private CaseLifeCycleRepository caseLifeCycleRepo;

  @Override
  public List<Sample> findSamples() {
    log.debug("Entering findSamples");
    return sampleRepo.findAll();
  }

  @Override
  public Sample findSampleBySampleId(final Integer sampleId) {
    log.debug("Entering findSampleBySampleId with {}", sampleId);
    return sampleRepo.findOne(sampleId);
  }

  /**
   * Generate new cases for given sample ID, geography type and geography code
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public boolean generateCases(final Integer sampleId, final String geographyType, final String geographyCode) {
    log.debug("Entering generateCases with sampleId {} - geographyType {} - geographyCode {}", sampleId, geographyType,
        geographyCode);
    return caseLifeCycleRepo.generateCases(sampleId, geographyType, geographyCode);
  }

}
