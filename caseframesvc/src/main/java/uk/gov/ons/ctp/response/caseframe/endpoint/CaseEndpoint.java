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
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.representation.CaseDTO;
import uk.gov.ons.ctp.response.caseframe.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;

/**
 * The REST endpoint controller for CaseFrame Cases
 */
@Path("/cases")
@Produces({"application/json"})
@Slf4j
public class CaseEndpoint implements CTPEndpoint {

  @Inject
  private CaseService caseService;

  @Inject
  private MapperFacade mapperFacade;
  
  @GET
  @Path("/uprn/{uprn}")
  public List<CaseDTO> findCasesByUprn(@PathParam("uprn") Integer uprn) {
    log.debug("Entering findCasesByUprn with {}", uprn);
    List<Case> cases = caseService.findCasesByUprn(uprn);
    List<CaseDTO> caseDTOs = mapperFacade.mapAsList(cases, CaseDTO.class);
    return CollectionUtils.isEmpty(caseDTOs) ? null : caseDTOs;
  }

  @GET
  @Path("/questionnaire/{qid}")
  public CaseDTO findCaseByQuestionnaireId(@PathParam("qid") Integer qid) throws CTPException {
    log.debug("Entering findCaseByQuestionnaireId with {}", qid);
    Case caseObj = caseService.findCaseByQuestionnaireId(qid);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Case not found for id %s", qid);
    }
    return mapperFacade.map(caseObj, CaseDTO.class);
  }

  @GET
  @Path("/{caseId}")
  public CaseDTO findCasesByCaseId(@PathParam("caseId") Integer caseId) throws CTPException {
    log.debug("Entering findCasesByCaseId with {}", caseId);
    Case caseObj = caseService.findCaseByCaseId(caseId);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Case not found for id %s", caseId);
    }
    return mapperFacade.map(caseObj, CaseDTO.class);
  }

  @GET
  @Path("/{caseId}/events")
  public List<CaseEventDTO> findCasesEventsByCaseId(@PathParam("caseId") Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId with {}", caseId);
    List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseId(caseId);
    List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
    return CollectionUtils.isEmpty(caseEventDTOs) ? null : caseEventDTOs;
  }
}
