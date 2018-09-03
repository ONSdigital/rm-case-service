package uk.gov.ons.ctp.response.casesvc.service.impl;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;

/** The impl of the service which calls the action service via REST */
@Service
public class ActionSvcClientServiceImpl implements ActionSvcClientService {
  private static final Logger log = LoggerFactory.getLogger(ActionSvcClientServiceImpl.class);

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Qualifier("collectionExerciseRestUtility")
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

    log.with("action", actionDTO).debug("about to post to the Action SVC");
    restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, httpEntity, ActionDTO.class);
  }
}
