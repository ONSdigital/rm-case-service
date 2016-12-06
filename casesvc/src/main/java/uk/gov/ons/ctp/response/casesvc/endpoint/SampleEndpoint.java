package uk.gov.ons.ctp.response.casesvc.endpoint;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Sample;
import uk.gov.ons.ctp.response.casesvc.representation.GeographyDTO;
import uk.gov.ons.ctp.response.casesvc.representation.SampleDTO;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;

/**
 * Sample endpoint including functionality to create cases for a given sample
 * ID, geography type and geography code
 */

@Path("/samples")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@Slf4j
public final class SampleEndpoint implements CTPEndpoint {

  @Inject
  private SampleService sampleService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve a sample by its id
   * @param sampleId the id of the sample
   * @return the sample representation
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{sampleid}")
  public Response findSampleBySampleId(@PathParam("sampleid") final Integer sampleId) throws CTPException {
    log.info("Entering findSampleBySampleId with {}", sampleId);
    Sample sample = sampleService.findSampleBySampleId(sampleId);
    if (sample == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Sample not found for id %s", sampleId);
    }
    return Response.ok(mapperFacade.map(sample, SampleDTO.class)).build();
  }

  /**
   * the PUT endpoint to create cases
   * @param sampleId the id of the sample
   * @param geography the geography
   * @return the sample representation
   * @throws CTPException something went wrong
   */
  @PUT
  @Path("/{sampleId}")
  public Response createCases(@PathParam("sampleId") final int sampleId, final GeographyDTO geography)
          throws CTPException {
    log.info("Creating cases ");
    Sample sample = sampleService.findSampleBySampleId(sampleId);
    if (sample == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Sample not found for id %s", sampleId);
    }
    sampleService.generateCases(sampleId, geography.getType(), geography.getCode());
    return Response.ok(mapperFacade.map(sample, SampleDTO.class)).build();
  }
}
