package uk.gov.ons.ctp.response.action.export.service;


import uk.gov.ons.ctp.response.action.export.domain.ActionRequest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to investigate best option for templating - CTPA-700
 */
public class TemplateInvestigation {
  public static void main(String[] args) {
    /**
     * Step - Get the action requests from the MongoDB
     */
    List<ActionRequest> actionRequestList = buildMeListOfActionRequests();
    System.out.println(String.format("We have %d action requests...", actionRequestList.size()));

    /**
     * Step - Produce a csv file
     */

  }

  /**
   * TODO This will be replaced by a actionRequestRepo.findAll or similar
   */
  private static List<ActionRequest> buildMeListOfActionRequests() {
    List<ActionRequest> result = new ArrayList<>();
    for (int i = 1; i < 51; i++) {
      result.add(buildAMeActionRequest(i));
    }
    return result;
  }

  private static ActionRequest buildAMeActionRequest(int i) {
    ActionRequest result =  new ActionRequest();
    result.setActionId(new BigInteger(new Integer(i).toString()));
    result.setResponseRequired(true);
    result.setActionType("testActionType");
    result.setIac("testIac");
    return result;
  }
}