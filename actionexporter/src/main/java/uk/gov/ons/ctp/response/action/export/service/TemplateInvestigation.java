package uk.gov.ons.ctp.response.action.export.service;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to investigate best option for templating - CTPA-700
 */
public class TemplateInvestigation {
  public static void main(String[] args) throws IOException, TemplateException {
    /**
     * Step - Get the action requests from the MongoDB
     */
    List<ActionRequest> actionRequestList = buildMeListOfActionRequests();
    System.out.println(String.format("We have %d action requests...", actionRequestList.size()));

    /**
     * Step - Produce a csv file
     */
    //Freemarker configuration object
    Configuration cfg = new Configuration();
    Template template = cfg.getTemplate("actionexporter/src/main/resources/templates/csvExport.ftl");

    // Build the data-model
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("actionRequests", actionRequestList);

    // File output
    Writer file = new FileWriter(new File("actionexporter/src/main/resources/forPrinter.csv"));
    template.process(data, file);
    file.flush();
    file.close();
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
    result.setActionType("testActionType");
    result.setIac("testIac");
    return result;
  }
}