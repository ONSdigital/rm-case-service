package uk.gov.ons.ctp.response.caseframe.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.domain.model.Sample;
import uk.gov.ons.ctp.response.caseframe.representation.GeographyDTO;
import uk.gov.ons.ctp.response.caseframe.representation.SampleDTO;
import uk.gov.ons.ctp.response.caseframe.service.SampleService;

/**
 * Sample endpoint including functionality to create cases for a given sample
 * ID, geography type and geography code
 */

@Path("/samples")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@Slf4j
public class SampleEndpoint implements CTPEndpoint {

  @Inject
  private SampleService sampleService;

  @Inject
  private MapperFacade mapperFacade;

  @GET
  @Path("/")
  public List<SampleDTO> findSamples() {
    log.debug("Entering findSamples...");
    List<Sample> samples = sampleService.findSamples();
    List<SampleDTO> sampleDTOs = mapperFacade.mapAsList(samples, SampleDTO.class);
    return CollectionUtils.isEmpty(sampleDTOs) ? null : sampleDTOs;
  }

  @GET
  @Path("/{sampleid}")
  public SampleDTO findSampleBySampleId(@PathParam("sampleid") Integer sampleId) throws CTPException {
    log.debug("Entering findSampleBySampleId with {}", sampleId);
    Sample sample = sampleService.findSampleBySampleId(sampleId);
    if (sample == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Sample not found for id %s", sampleId);
    }
    return mapperFacade.map(sample, SampleDTO.class);
  }

  @PUT
  @Path("/{sampleId}")
  public void createCases(@PathParam("sampleId") int sampleId, GeographyDTO geography) {
    log.debug("Creating cases ");
    sampleService.generateCases(sampleId, geography.getGeographyType(), geography.getGeographyCode());
  }
}
