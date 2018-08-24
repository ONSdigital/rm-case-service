package uk.gov.ons.ctp.response.casesvc.service.impl;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;

/** The impl of the service which calls the action service via REST */
@Service
public class ActionSvcClientServiceImpl implements ActionSvcClientService {
  private static final Logger log = LoggerFactory.getLogger(ActionSvcClientServiceImpl.class);

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Qualifier("actionServiceRestUtility")
  @Autowired
  private RestUtility restUtility;

  @Override
  public void createAndPostAction(String actionType, UUID caseId, String createdBy) {
    ActionDTO actionDTO = new ActionDTO();
    actionDTO.setCaseId(caseId);
    actionDTO.setActionTypeName(actionType);
    actionDTO.setCreatedBy(createdBy);
    HttpEntity<ActionDTO> httpEntity = restUtility.createHttpEntity(actionDTO);

    UriComponents uriComponents =
        restUtility.createUriComponents(appConfig.getActionSvc().getActionsPath(), null);

    log.debug("about to post to the Action SVC with {}", actionDTO);
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
  @Override
  public List<ActionPlanDTO> getActionPlans(UUID collectionExerciseId, boolean activeEnrolments) {
    log.debug(
        "Retrieving action plan for selectors, " + "collectionExerciseId: {}, activeEnrolment: {}",
        collectionExerciseId,
        activeEnrolments);

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("collectionExerciseId", collectionExerciseId.toString());
    queryParams.add("activeEnrolment", Boolean.toString(activeEnrolments));
    UriComponents uriComponents =
        restUtility.createUriComponents(appConfig.getActionSvc().getActionPlansPath(), queryParams);

    ResponseEntity<List<ActionPlanDTO>> responseEntity;
    HttpEntity<?> httpEntity = restUtility.createHttpEntityWithAuthHeader();
    try {
      responseEntity =
          restTemplate.exchange(
              uriComponents.toString(),
              HttpMethod.GET,
              httpEntity,
              new ParameterizedTypeReference<List<ActionPlanDTO>>() {});
    } catch (HttpClientErrorException e) {
      throw e;
    }

    log.debug(
        "Successfully retrieved action plan for selectors, "
            + "collectionExerciseId: {}, activeEnrolment: {}",
        collectionExerciseId,
        activeEnrolments);
    return responseEntity.getBody();
  }
}
