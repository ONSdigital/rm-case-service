package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.scheduling.annotation.Async;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Sample;
import uk.gov.ons.ctp.response.caseframe.domain.repository.SampleRepository;
import uk.gov.ons.ctp.response.caseframe.service.SampleService;

/**
 * An implementation of the AddressService using JPA Repository class(es) The
 * business logic for the application should reside here.
 */
@Named
@Slf4j
public final class SampleServiceImpl implements SampleService {

  @Inject
  private SampleRepository sampleRepo;

  @Override
  public List<Sample> findSamples() {
    log.debug("Entering findSamples");
    return sampleRepo.findAll();
  }

  @Override
  public Sample findSampleBySampleId(Integer sampleId) {
    log.debug("Entering findSampleBySampleId with {}", sampleId);
    return sampleRepo.findOne(sampleId);
  }

  /**
   * Generate new cases for given sample ID, geography type and geography code
   */
  @Async
  @Override
  public Boolean generateCases(Integer sampleId, String geographyType, String geographyCode) {
    log.debug("Entering generateCases with sampleId {} - geographyType {} - geographyCode {}", sampleId, geographyType,
        geographyCode);
    return sampleRepo.generateCases(sampleId, geographyType, geographyCode);
  }

}
