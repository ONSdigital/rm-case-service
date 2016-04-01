package uk.gov.ons.ctp.response.caseframe.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;
import uk.gov.ons.ctp.response.caseframe.representation.LocalAuthorityDTO;
import uk.gov.ons.ctp.response.caseframe.representation.MsoaDTO;
import uk.gov.ons.ctp.response.caseframe.service.LocalAuthorityService;

/**
 * The REST endpoint controller for CaseFrame LocalAuthorities
 */
@Path("/lads")
@Produces({ "application/json" })
@Slf4j
public final class LocalAuthorityEndpoint implements CTPEndpoint {

  @Inject
  private LocalAuthorityService localAuthorityService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve a LocalAuthority by id
   * @param ladId the id of the LAD to retrieve
   * @return the LAD representation or null if not found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{ladid}")
  public LocalAuthorityDTO findLadById(@PathParam("ladid") final String ladId) throws CTPException {
    log.debug("Entering findLadById with {}", ladId);
    LocalAuthority localAuthority = localAuthorityService.findById(ladId);
    if (localAuthority == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "LAD not found for id %s", ladId);
    }
    return mapperFacade.map(localAuthority, LocalAuthorityDTO.class);
  }

  /**
   * the GET endpoint to retrieve all MSOA for a given LAD
   * @param ladId the LAD id to fetch by
   * @return the list of MSOA
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{ladid}/msoas")
  public List<MsoaDTO> findAllMsoasForLadid(@PathParam("ladid") final String ladId) throws CTPException {
    log.debug("Entering findAllMsoasForLadid with {}", ladId);
    List<Msoa> msoas = localAuthorityService.findAllMsoasByLadid(ladId);
    if (msoas == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "LAD not found for id %s", ladId);
    } else {
      List<MsoaDTO> msoaDTOs = mapperFacade.mapAsList(msoas, MsoaDTO.class);
      return CollectionUtils.isEmpty(msoaDTOs) ? null : msoaDTOs;
    }
  }
}
