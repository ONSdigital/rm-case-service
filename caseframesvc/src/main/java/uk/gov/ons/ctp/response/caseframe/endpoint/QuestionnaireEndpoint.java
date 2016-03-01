package uk.gov.ons.ctp.response.caseframe.endpoint;

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
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.representation.QuestionnaireDTO;
import uk.gov.ons.ctp.response.caseframe.service.QuestionnaireService;

/**
 * The REST endpoint controller for CaseFrame Questionnaires
*/
@Path("/questionnaires")
@Produces({"application/json"})
@Slf4j
public class QuestionnaireEndpoint implements CTPEndpoint {

  public static final String OPERATION_FAILED = "Response operation failed for questionnaireid";

  @Inject
  private QuestionnaireService questionnaireService;

  @Inject
  private MapperFacade mapperFacade;
  
  @GET
  @Path("/iac/{iac}")
  public QuestionnaireDTO findByIac(@PathParam("iac") String iac) throws CTPException {
    log.debug("Entering findByIac with {}", iac);
    Questionnaire questionnaire = questionnaireService.findQuestionnaireByIac(iac);
    if (questionnaire == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Cannot find Questionnaire for iac %s", iac);
    }
    QuestionnaireDTO result = mapperFacade.map(questionnaire, QuestionnaireDTO.class);

    return result;
  }

  @GET
  @Path("/case/{caseid}")
  public List<QuestionnaireDTO> findByCaseId(@PathParam("caseid") Integer caseId) throws CTPException {
    log.debug("Entering findByCaseId with {}", caseId);
    List<Questionnaire> questionnaires = questionnaireService.findQuestionnairesByCaseId(caseId);
    List<QuestionnaireDTO> questionnaireDTOs = mapperFacade.mapAsList(questionnaires, QuestionnaireDTO.class);
    return CollectionUtils.isEmpty(questionnaireDTOs) ? null : questionnaireDTOs;
  }

  @PUT
  @Path("/{questionnaireid}/response")
  public Response responseOperation(@PathParam("questionnaireid") Integer questionnaireid) throws CTPException {
    log.debug("Entering responseOperation with {}", questionnaireid);
    int nbOfUpdatedQuestionnaires = questionnaireService.updateResponseTime(questionnaireid);
    int nbOfUpdatedCases = questionnaireService.closeParentCase(questionnaireid);
    if (!(nbOfUpdatedQuestionnaires == 1 && nbOfUpdatedCases == 1)) {
      log.error("{} {} - nbOfUpdatedQuestionnaires = {} - nbOfUpdatedCases = {}", OPERATION_FAILED, questionnaireid, nbOfUpdatedQuestionnaires, nbOfUpdatedCases);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "%s %s", OPERATION_FAILED, questionnaireid);
    }
    return Response.status(Response.Status.OK).build();
  }
}
