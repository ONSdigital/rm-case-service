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
 * A SampleService implementation which encapsulates all business logic
 * operating on the Sample entity model.
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
  public Sample findSampleBySampleId(final Integer sampleId) {
    log.debug("Entering findSampleBySampleId with {}", sampleId);
    return sampleRepo.findOne(sampleId);
  }

  /**
   * Generate new cases for given sample ID, geography type and geography code
   */
  @Async
  @Override
  public Boolean generateCases(final Integer sampleId, final String geographyType, final String geographyCode) {
    log.debug("Entering generateCases with sampleId {} - geographyType {} - geographyCode {}", sampleId, geographyType,
        geographyCode);
    return sampleRepo.generateCases(sampleId, geographyType, geographyCode);
  }

}
