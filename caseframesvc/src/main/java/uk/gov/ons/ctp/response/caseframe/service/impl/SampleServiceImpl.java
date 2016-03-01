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
 * An implementation of the AddressService using JPA Repository class(es)
 * The business logic for the application should reside here.
 */
@Named
@Slf4j
public class SampleServiceImpl implements SampleService {
  
  @Inject
  private SampleRepository sampleRepo;
  
  public List<Sample> findSamples() {
    log.debug("Entering findSamples");
    return sampleRepo.findAll();
  }

  public Sample findSampleBySampleId(Integer sampleId) {
    log.debug("Entering findSampleBySampleId with {}", sampleId);
    return sampleRepo.findOne(sampleId);
  }
  
  /**
   * Generate new cases for given sample ID, geography type and geography code
  */
  @Async
  public Boolean generate_cases(Integer sampleId, String goegraphyType, String geographyCode) {
    log.debug("Entering generateCases with sampleId {} - goegraphyType {} - geographyCode {}", sampleId, goegraphyType, geographyCode);
    return sampleRepo.generateCases(sampleId, goegraphyType, geographyCode);
  }
  
}
