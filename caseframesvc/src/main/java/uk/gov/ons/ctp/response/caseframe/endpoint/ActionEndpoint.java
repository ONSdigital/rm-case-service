package uk.gov.ons.ctp.response.caseframe.endpoint;

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
import uk.gov.ons.ctp.response.caseframe.domain.model.Action;
import uk.gov.ons.ctp.response.caseframe.representation.ActionDTO;
import uk.gov.ons.ctp.response.caseframe.service.ActionService;

/**
 * The REST endpoint controller for Actions
 */
@Path("/actions")
@Produces({ "application/json" })
@Slf4j
public class ActionEndpoint implements CTPEndpoint {

  @Inject
  private ActionService actionService;

  @Inject
  private MapperFacade mapperFacade;

  @GET
  @Path("/{actionid}")
  public ActionDTO findActionByActionId(@PathParam("actionid") Integer actionId) throws CTPException {
    log.debug("Entering findActionByActionId with {}", actionId);
    Action action = actionService.findActionByActionId(actionId);
    if (action == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Action not found for id %s", actionId);
    }
    ActionDTO result = mapperFacade.map(action, ActionDTO.class);
    return result;
  }

  @GET
  @Path("/case/{caseid}")
  public List<ActionDTO> findActionsByCaseId(@PathParam("caseid") Integer caseId) {
    log.debug("Entering findActionsByCaseId...");
    List<Action> actions = actionService.findActionsByCaseId(caseId);
    List<ActionDTO> actionDTOs = mapperFacade.mapAsList(actions, ActionDTO.class);
    return CollectionUtils.isEmpty(actionDTOs) ? null : actionDTOs; 
  }
}
