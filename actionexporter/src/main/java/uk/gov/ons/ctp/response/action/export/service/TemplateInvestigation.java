package uk.gov.ons.ctp.response.action.export.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequest;

import java.io.*;
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
     * Step - Configure FreeMarker
     * Configuration instances are meant to be application-level singletons.
     */
    // Freemarker configuration object - do this only once at the beginning of the application (possibly servlet) life-cycle
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
    cfg.setDirectoryForTemplateLoading(new File("actionexporter/src/main/resources/templates"));  // non-file-system sources are possible too
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
    cfg.setLogTemplateExceptions(false);

        // Build the data model
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("actionRequests", actionRequestList);

    Template template = cfg.getTemplate("csvExport.ftl"); // Configuration caches Template instances

    // Console output
    Writer out = new OutputStreamWriter(System.out);
    template.process(root, out);
    out.flush();

    // File output
    Writer file = new FileWriter(new File("actionexporter/src/main/resources/forPrinter.csv"));
    template.process(root, file);
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