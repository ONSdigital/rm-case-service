package uk.gov.ons.ctp.response.casesvc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;

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
  @Qualifier("actionServiceClient")
  private RestClient actionServiceClient;

  @Override
  public void createAndPostAction(String actionType, int caseId, String createdBy) {
      ActionDTO actionDTO = new ActionDTO();
      actionDTO.setCaseId(caseId);
      actionDTO.setActionTypeName(actionType);
      actionDTO.setCreatedBy(createdBy);
      log.debug("about to post to the Action SVC with {}", actionDTO);
      actionServiceClient.postResource(appConfig.getActionSvc().getActionsPath(), actionDTO, ActionDTO.class);
  }

}
