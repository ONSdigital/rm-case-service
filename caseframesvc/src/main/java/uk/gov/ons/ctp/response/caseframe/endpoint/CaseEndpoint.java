package uk.gov.ons.ctp.response.caseframe.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.*;

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
@Produces({ "application/json" })
@Slf4j
public final class CaseEndpoint implements CTPEndpoint {

  @Inject
  private CaseService caseService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to find Cases by postcode
   * @param uprn to find by
   * @return the cases found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/uprn/{uprn}")
  public final List<CaseDTO> findCasesByUprn(@PathParam("uprn") final Integer uprn) {
    log.debug("Entering findCasesByUprn with {}", uprn);
    List<Case> cases = caseService.findCasesByUprn(uprn);
    List<CaseDTO> caseDTOs = mapperFacade.mapAsList(cases, CaseDTO.class);
    return CollectionUtils.isEmpty(caseDTOs) ? null : caseDTOs;
  }
  /**
   * the GET endpoint to find a Case by questionnaire id
   * @param qid to find by
   * @return the case found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/questionnaire/{qid}")
  public final CaseDTO findCaseByQuestionnaireId(@PathParam("qid") final Integer qid) throws CTPException {
    log.debug("Entering findCaseByQuestionnaireId with {}", qid);
    Case caseObj = caseService.findCaseByQuestionnaireId(qid);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Case not found for id %s", qid);
    }
    return mapperFacade.map(caseObj, CaseDTO.class);
  }

  /**
   * the GET endpoint to find a Case by id
   * @param caseId to find by
   * @return the case found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{caseId}")
  public final CaseDTO findCaseByCaseId(@PathParam("caseId") final Integer caseId) throws CTPException {
    log.debug("Entering findCaseByCaseId with {}", caseId);
    Case caseObj = caseService.findCaseByCaseId(caseId);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Case not found for id %s", caseId);
    }
    return mapperFacade.map(caseObj, CaseDTO.class);
  }

  /**
   * the GET endpoint to find addresses by postcode
   * @param caseId to find by
   * @return the addresses found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{caseId}/events")
  public final List<CaseEventDTO> findCaseEventsByCaseId(@PathParam("caseId") final Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId with {}", caseId);
    List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseId(caseId);
    List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
    return CollectionUtils.isEmpty(caseEventDTOs) ? null : caseEventDTOs;
  }

  /**
   * To create a case event being given a parent case and json to describe the case event to be created
   * @param caseId the parent case
   * @param requestObject the CaseEventDTO describing the case event to be created
   * @return the created CaseEventDTO
   * @throws CTPException
   */
  @POST
  @Path("/{caseId}/events")
  public final CaseEventDTO createCaseEvent(@PathParam("caseId") final Integer caseId, final CaseEventDTO requestObject)
      throws CTPException {
    log.debug("Entering createCaseEvent with caseId {} and requestObject {}", caseId, requestObject);
    if (requestObject == null) {
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED, "Provided json is incorrect.");
    }

    CaseEvent createdCaseEvent = caseService.createCaseEvent(caseId, mapperFacade.map(requestObject, CaseEvent.class));
    if (createdCaseEvent == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Case not found for id %s", caseId);
    }
    return mapperFacade.map(createdCaseEvent, CaseEventDTO.class);
  }
}
