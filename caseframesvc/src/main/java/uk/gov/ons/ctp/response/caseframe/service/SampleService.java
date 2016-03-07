package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Sample;

/**
 * The Sample Service interface defines all business behaviours for operations
 * on the Sample entity model.
 */
public interface SampleService extends CTPService {

  /**
   * Returns all Samples.
   *
   * @return List of Sample entities or empty List
   */
  List<Sample> findSamples();

  /**
   * Find Sample entity by Sample Id.
   *
   * @param sampleId Sample Id Integer
   * @return Sample entity or null
   */
  Sample findSampleBySampleId(Integer sampleId);

  /**
   * Create the Cases for the supplied geographyType and associated area code.
   *
   * @param sampleId Unique Sample Isd for which to craete cases
   * @param goegraphyType Type of Geography area to which code relates
   * @param geographyCode Code for area
   * @return Boolean result of operation
   */
  Boolean generateCases(Integer sampleId, String goegraphyType, String geographyCode);

}
