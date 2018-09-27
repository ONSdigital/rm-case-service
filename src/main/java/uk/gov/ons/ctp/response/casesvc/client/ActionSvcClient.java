package uk.gov.ons.ctp.response.casesvc.client;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

/** The impl of the service which calls the action service via REST */
@Service
public class ActionSvcClient {
  private static final Logger log = LoggerFactory.getLogger(ActionSvcClient.class);

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Qualifier("actionServiceRestUtility")
  @Autowired
  private RestUtility restUtility;

  /**
   * Make use of the ActionService to post a new Action for a given caseId according to Category
   * actionType and CaseEvent createdBy values
   *
   * @param actionType action type
   * @param caseId the UUID caseId
   * @param createdBy who did this
   */
  public void postAction(String actionType, UUID caseId, String createdBy) {
    ActionDTO actionDTO = new ActionDTO();
    actionDTO.setCaseId(caseId);
    actionDTO.setActionTypeName(actionType);
    actionDTO.setCreatedBy(createdBy);
    HttpEntity<ActionDTO> httpEntity = restUtility.createHttpEntity(actionDTO);

    UriComponents uriComponents =
        restUtility.createUriComponents(appConfig.getActionSvc().getActionsPath(), null);

    log.with("case_id", caseId).debug("about to post to the Action SVC");
    restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, httpEntity, ActionDTO.class);
  }

  /**
   * Get actionplans from action service using action plan selectors
   *
   * @param collectionExerciseId Id of collection exercise to get action plans for
   * @param activeEnrolments selector to determine if actionplan for active enrolments(email) or
   *     not(letters)
   * @return List of action plans matching selectors
   */
  public List<ActionPlanDTO> getActionPlans(UUID collectionExerciseId, boolean activeEnrolments) {
    log.with("collection_exercise_id", collectionExerciseId)
        .with("active_enrolments", activeEnrolments)
        .debug("Retrieving action plan for selectors");

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("collectionExerciseId", collectionExerciseId.toString());
    queryParams.add("activeEnrolment", Boolean.toString(activeEnrolments));
    String actionPlansPath = appConfig.getActionSvc().getActionPlansPath();
    UriComponents uriComponents = restUtility.createUriComponents(actionPlansPath, queryParams);

    ResponseEntity<List<ActionPlanDTO>> responseEntity;
    HttpEntity<?> httpEntity = restUtility.createHttpEntityWithAuthHeader();
    responseEntity =
        restTemplate.exchange(
            uriComponents.toString(),
            HttpMethod.GET,
            httpEntity,
            new ParameterizedTypeReference<List<ActionPlanDTO>>() {});

    log.with("collection_exercise_id", collectionExerciseId)
        .with("active_enrolments", activeEnrolments)
        .debug("Successfully retrieved action plan for selectors");
    return responseEntity.getBody();
  }
}
