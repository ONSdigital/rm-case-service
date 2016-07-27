package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.service.impl.QuestionnaireServiceImpl.OPERATION_FAILED;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.casesvc.representation.QuestionnaireDTO;
import uk.gov.ons.ctp.response.casesvc.service.QuestionnaireService;

/**
 * A RESTFul Endpoint controller for the CaseSvc product for all actions
 * relating to Questionnaires.
 */

@Path("/questionnaires")
@Produces({ "application/json" })
@Slf4j
public final class QuestionnaireEndpoint implements CTPEndpoint {

  /**
   * The Questionnaire business service.
   */
  @Inject
  private QuestionnaireService questionnaireService;

  /**
   * Orika service translating domain Entity objects to DTO representation
   * passed to view.
   */

  @Inject
  private MapperFacade mapperFacade;

  /**
   * Web Service to return a Questionnaire object for the supplied Internet
   * Access Code.
   * @param iac the IAC to find by
   * @return QuestionnaireDTO object or CTPException fault code
   *         RESOURCE_NOT_FOUND
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/iac/{iac}")
  public QuestionnaireDTO findByIac(@PathParam("iac") final String iac) throws CTPException {
    log.debug("Entering findByIac with {}", iac);
    Questionnaire questionnaire = questionnaireService.findQuestionnaireByIac(iac);
    if (questionnaire == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Cannot find Questionnaire for iac %s", iac);
    }
    QuestionnaireDTO result = mapperFacade.map(questionnaire, QuestionnaireDTO.class);

    return result;
  }

  /**
   * Web Service to return List of Questionnaire objects for the specified Case.
   *
   * @param caseId to find
   * @return List of Questionnaire objects or null
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/case/{caseid}")
  public List<QuestionnaireDTO> findByCaseId(@PathParam("caseid") final Integer caseId) throws CTPException {
    log.debug("Entering findByCaseId with {}", caseId);
    List<Questionnaire> questionnaires = questionnaireService.findQuestionnairesByCaseId(caseId);
    List<QuestionnaireDTO> questionnaireDTOs = mapperFacade.mapAsList(questionnaires, QuestionnaireDTO.class);
    return CollectionUtils.isEmpty(questionnaireDTOs) ? null : questionnaireDTOs;
  }

  /**
   * Web service to update a Questionnaire and Case object to record a response
   * has been received in the Survey Data Exchange.
   *
   * @param questionnaireId id of response received
   * @return javax.ws.rs.core.Response with 200 OK on success
   * @throws CTPException on operation failure
   */
  @PUT
  @Path("/{questionnaireid}/response")
  public Response responseOperation(@PathParam("questionnaireid") final Integer questionnaireId) throws CTPException {
    log.debug("Entering responseOperation with {}", questionnaireId);
    Questionnaire questionnaire = questionnaireService.recordResponse(questionnaireId);
    if (questionnaire == null) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "%s %s", OPERATION_FAILED, questionnaireId);
    }
    return Response.status(Response.Status.NO_CONTENT).build();
  }
}
