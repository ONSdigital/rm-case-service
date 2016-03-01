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
public class LocalAuthorityEndpoint implements CTPEndpoint {

  @Inject
  private LocalAuthorityService localAuthorityService;

  @Inject
  private MapperFacade mapperFacade;

  @GET
  @Path("/{ladid}")
  public LocalAuthorityDTO findLadById(@PathParam("ladid") String ladId) throws CTPException {
    log.debug("Entering findLadById with {}", ladId);
    LocalAuthority localAuthority = localAuthorityService.findById(ladId);
    if (localAuthority == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "LAD not found for id %s", ladId);
    }
    return mapperFacade.map(localAuthority, LocalAuthorityDTO.class);
  }

  @GET
  @Path("/{ladid}/msoas")
  public List<MsoaDTO> findAllMsoasForLadid(@PathParam("ladid") String ladId) {
    log.debug("Entering findAllMsoasForLadid with {}", ladId);
    List<Msoa> msoas = localAuthorityService.findAllMsoasByLadid(ladId);
    List<MsoaDTO> msoaDTOs = mapperFacade.mapAsList(msoas, MsoaDTO.class);
    return CollectionUtils.isEmpty(msoaDTOs) ? null : msoaDTOs;
  }
}
