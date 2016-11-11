package uk.gov.ons.ctp.response.casesvc.endpoint;

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
import uk.gov.ons.ctp.response.casesvc.domain.model.Address;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.service.AddressService;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;

/**
 * The REST endpoint controller for CaseSvc Cases
 */
@Path("/casegroups")
@Produces({"application/json"})
@Slf4j
public final class CaseGroupEndpoint implements CTPEndpoint {

  public static final String ERRORMSG_CASEGROUPNOTFOUND = "CaseGroup not found for";
  public static final String ERRORMSG_ADDRESSNOTFOUND = "Address not found for";

  @Inject
  private CaseGroupService caseGroupService;

  @Inject
  private AddressService addressService;

  @Inject
  private MapperFacade mapperFacade;
 /**
   * the GET endpoint to find CaseGroups by uprn
   *
   * @param uprn to find by
   * @return the casegroups found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{caseGroupId}")
  public CaseGroupDTO findCaseGroupById(@PathParam("caseGroupId") final Integer caseGroupId)  throws CTPException {
    log.info("Entering findCaseGroupById with {}", caseGroupId);
    CaseGroup caseGroupObj = caseGroupService.findCaseGroupByCaseGroupId(caseGroupId);
    if (caseGroupObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format("%s casegroup id %s", ERRORMSG_CASEGROUPNOTFOUND, caseGroupId));
    }
    return mapperFacade.map(caseGroupObj, CaseGroupDTO.class);
  }
    
    
  /**
   * the GET endpoint to find CaseGroups by uprn
   *
   * @param uprn to find by
   * @return the casegroups found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/uprn/{uprn}")
  public List<CaseGroupDTO> findCaseGroupsByUprn(@PathParam("uprn") final Long uprn)  throws CTPException {
    log.info("Entering findCaseGroupsByUprn with {}", uprn);

    Address address = addressService.findByUprn(uprn);
    if (address == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format("%s UPRN %s", ERRORMSG_ADDRESSNOTFOUND, uprn));
    }
    List<CaseGroup> groups = caseGroupService.findCaseGroupsByUprn(uprn);
    List<CaseGroupDTO> groupDTOs = mapperFacade.mapAsList(groups, CaseGroupDTO.class);
    return CollectionUtils.isEmpty(groupDTOs) ? null : groupDTOs;
  }
}
