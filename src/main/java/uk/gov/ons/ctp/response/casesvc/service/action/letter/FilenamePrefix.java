package uk.gov.ons.ctp.response.casesvc.service.action.letter;

import java.util.HashMap;
import java.util.Map;

/** This class replaces the template mappings database */
public class FilenamePrefix {
  private static final Map<String, String> mappings = new HashMap<>();

  static {
    mappings.put("BSNOT", "BSNOT");
    mappings.put("BSREM", "BSREM");
    mappings.put("BSNL", "BSNOT");
    mappings.put("BSRL", "BSREM");
  }

  public static String getPrefix(String actionType) {
    // look for a mapping or just return the action type
    return mappings.getOrDefault(actionType, actionType);
  }
}
