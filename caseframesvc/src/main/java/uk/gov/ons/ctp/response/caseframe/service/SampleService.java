package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Sample;

/**
 * Created by Martin.Humphrey on 17/2/2016.
 */
public interface SampleService extends CTPService {

  List<Sample> findSamples();

  Sample findSampleBySampleId(Integer sampleId);
  
  Boolean generate_cases(Integer sampleId, String goegraphyType, String geographyCode);

}
