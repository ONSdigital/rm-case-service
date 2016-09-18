package uk.gov.ons.ctp.response.action.export.templating.freemarker.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.domain.FreeMarkerTemplate;

import java.io.InputStream;
import java.util.List;

public interface FreeMarkerService {
  /**
   * To store a FreeMarker template
   * @param templateName the template name
   * @param fileContents the template content
   * @return the FreeMarker template stored
   * @throws CTPException if the FreeMarker template content is empty
   */
  FreeMarkerTemplate storeTemplate(String templateName, InputStream fileContents) throws CTPException;

  /**
   * To retrieve a given FreeMarker template
   * @param templateName the template name to be retrieved
   * @return the given FreeMarker template
   */
  FreeMarkerTemplate retrieveTemplate(String templateName);

  /**
   * To retrieve all FreeMarker templates
   * @return a list of FreeMarker templates
   */
  List<FreeMarkerTemplate> retrieveAllTemplates();

  /**
   * To clear the FreeMarker configuration's template cache
   */
  void clearTemplateCache();
}
