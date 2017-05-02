package uk.gov.ons.ctp.response.casesvc.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Sample;
import uk.gov.ons.ctp.response.casesvc.representation.GeographyDTO;
import uk.gov.ons.ctp.response.casesvc.representation.SampleDTO;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;

import javax.validation.Valid;

/**
 * Sample endpoint including functionality to create cases for a given sample
 * ID, geography type and geography code
 */
@RestController
@RequestMapping(value = "/samples", produces = "application/json")
@Slf4j
public final class SampleEndpoint implements CTPEndpoint {

  @Autowired
  private SampleService sampleService;

  @Qualifier("caseSvcBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve a sample by its id
   * @param sampleId the id of the sample
   * @return the sample representation
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{sampleid}", method = RequestMethod.GET)
  public SampleDTO findSampleBySampleId(@PathVariable("sampleid") final Integer sampleId) throws CTPException {
    log.info("Entering findSampleBySampleId with {}", sampleId);
    Sample sample = sampleService.findSampleBySampleId(sampleId);
    if (sample == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Sample not found for id %s", sampleId);
    }
    return mapperFacade.map(sample, SampleDTO.class);
  }

  /**
   * the PUT endpoint to create cases
   * @param sampleId the id of the sample
   * @param geography the geography
   * @return the sample representation
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{sampleid}", method = RequestMethod.PUT, consumes = "application/json")
  public SampleDTO createCases(@PathVariable("sampleId") final int sampleId,
                               @RequestBody @Valid final GeographyDTO geography, BindingResult bindingResult)
          throws CTPException {
    log.info("Creating cases ");
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for update action: ", bindingResult);
    }

    Sample sample = sampleService.findSampleBySampleId(sampleId);
    if (sample == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Sample not found for id %s", sampleId);
    }
    sampleService.generateCases(sampleId, geography.getType(), geography.getCode());
    return mapperFacade.map(sample, SampleDTO.class);
  }
}
