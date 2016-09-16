package uk.gov.ons.ctp.response.action.export.service;

import uk.gov.ons.ctp.response.action.export.domain.FreeMarkerTemplate;

public interface FreeMarkerService {
  FreeMarkerTemplate storeTemplate(FreeMarkerTemplate template);
  FreeMarkerTemplate retrieveTemplate(String templateName);
}
