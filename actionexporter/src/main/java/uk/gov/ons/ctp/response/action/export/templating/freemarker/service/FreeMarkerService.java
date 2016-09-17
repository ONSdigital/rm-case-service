package uk.gov.ons.ctp.response.action.export.templating.freemarker.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.domain.FreeMarkerTemplate;

import java.io.InputStream;
import java.util.List;

public interface FreeMarkerService {
  FreeMarkerTemplate storeTemplate(String templateName, InputStream fileContents)  throws CTPException;
  FreeMarkerTemplate retrieveTemplate(String templateName);
  List<FreeMarkerTemplate> retrieveAllTemplates();
}
