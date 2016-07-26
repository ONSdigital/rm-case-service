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
import uk.gov.ons.ctp.response.casesvc.domain.model.QuestionSet;
import uk.gov.ons.ctp.response.casesvc.representation.QuestionSetDTO;
import uk.gov.ons.ctp.response.casesvc.service.QuestionSetService;

/**
 * The REST endpoint controller for CaseSvc QuestionSets
 */
@Path("/questionsets")
@Produces({ "application/json" })
@Slf4j
public final class QuestionSetEndpoint implements CTPEndpoint {

  @Inject
  private QuestionSetService questionSetService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve the list of all question sets
   * @return the list of all question sets
   */
  @GET
  @Path("/")
  public List<QuestionSetDTO> findQuestionSets() {
    log.debug("Entering findQuestionSets...");
    List<QuestionSet> questionSets = questionSetService.findQuestionSets();
    List<QuestionSetDTO> questionSetDTOs = mapperFacade.mapAsList(questionSets, QuestionSetDTO.class);
    return CollectionUtils.isEmpty(questionSetDTOs) ? null : questionSetDTOs;
  }

  /**
   * the GET endpoint to retrieve a question set by name
   * @param questionSetName the name of eth question set to fetch
   * @return the question set representation
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{questionset}")
  public QuestionSetDTO findQuestionSetByQuestionSet(@PathParam("questionset") final String questionSetName)
      throws CTPException {
    log.debug("Entering findQuestionSetByQuestionSet with {}", questionSetName);
    QuestionSet questionSet = questionSetService.findQuestionSetByQuestionSet(questionSetName);
    if (questionSet == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "QuestionSet not found for id %s", questionSetName);
    }
    return mapperFacade.map(questionSet, QuestionSetDTO.class);
  }
}
