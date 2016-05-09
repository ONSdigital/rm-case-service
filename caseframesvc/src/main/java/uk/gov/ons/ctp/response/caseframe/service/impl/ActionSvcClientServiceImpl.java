package uk.gov.ons.ctp.response.caseframe.service.impl;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.caseframe.config.AppConfig;
import uk.gov.ons.ctp.response.caseframe.service.ActionSvcClientService;

@Slf4j
@Named
public class ActionSvcClientServiceImpl implements ActionSvcClientService {

  @Inject
  private AppConfig appConfig;

  @Inject
  private RestClient caseFrameClient;

  @Override
  public void createAndPostAction(String actionType, int caseId, String createdBy) {
      ActionDTO actionDTO = new ActionDTO();
      actionDTO.setCaseId(caseId);
      actionDTO.setActionTypeName(actionType);
      actionDTO.setCreatedBy(createdBy);
      log.debug("about to post to the Action SVC with {}", actionDTO);
      caseFrameClient.postResource(appConfig.getActionSvc().getActionsPath(), actionDTO, ActionDTO.class);
  }

  @Override
  public void cancelActions(int caseId) {
    log.debug("about to put cancel actions to the Action SVC with {}", caseId);
    caseFrameClient.putResource(appConfig.getActionSvc().getCancelActionsPath(), null, ActionDTO[].class, caseId);

  }

}
