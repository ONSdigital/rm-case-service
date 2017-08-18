package uk.gov.ons.ctp.response.casesvc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;

import java.util.UUID;

/**
 * The impl of the service which calls the action service via REST
 *
 */
@Slf4j
@Service
public class ActionSvcClientServiceImpl implements ActionSvcClientService {

  @Autowired
  private AppConfig appConfig;

  @Autowired
  private RestTemplate restTemplate;

  @Qualifier("collectionExerciseRestUtility")
  @Autowired
  private RestUtility restUtility;

  @Override
  public void createAndPostAction(String actionType, UUID caseId, String createdBy) {
    UriComponents uriComponents = restUtility.createUriComponents(appConfig.getActionSvc().getActionsPath(),
        null);

    ActionDTO actionDTO = new ActionDTO();
    actionDTO.setCaseId(caseId);
    actionDTO.setActionTypeName(actionType);
    actionDTO.setCreatedBy(createdBy);
    HttpEntity<ActionDTO> httpEntity = restUtility.createHttpEntity(actionDTO);

    log.debug("about to post to the Action SVC with {}", actionDTO);
    restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, httpEntity, ActionDTO.class);
  }
}
