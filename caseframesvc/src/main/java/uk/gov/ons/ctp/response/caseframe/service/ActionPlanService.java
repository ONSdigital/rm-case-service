package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.ActionPlan;

/**
 * Created by Martin.Humphrey on 17/2/2016.
 */
public interface ActionPlanService extends CTPService {

  List<ActionPlan> findActionPlans();

  ActionPlan findActionPlan(Integer actionPlanId);


}
