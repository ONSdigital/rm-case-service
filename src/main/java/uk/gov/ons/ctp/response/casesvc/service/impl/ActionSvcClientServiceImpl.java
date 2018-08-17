package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;

/** The impl of the service which calls the action service via REST */
@Slf4j
@Service
public class ActionSvcClientServiceImpl implements ActionSvcClientService {

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

  @Override
  public List<ActionPlanDTO> getActionPlans(UUID collectionExerciseId, boolean activeEnrolments) {

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

    queryParams.add("collectionExerciseId", collectionExerciseId.toString());
    queryParams.add("activeEnrolment", Boolean.toString(activeEnrolments).toLowerCase());

    UriComponents uriComponents =
        restUtility.createUriComponents(appConfig.getActionSvc().getActionPlansPath(), queryParams);

    return restTemplate
        .exchange(
            uriComponents.toUri(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ActionPlanDTO>>() {})
        .getBody();
  }
}
