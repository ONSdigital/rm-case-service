package uk.gov.ons.ctp.response.action.export.service;

import org.springframework.web.multipart.MultipartFile;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.FreeMarkerTemplate;

public interface FreeMarkerService {
  FreeMarkerTemplate storeTemplate(String templateName, MultipartFile file)  throws CTPException;
  FreeMarkerTemplate retrieveTemplate(String templateName);
}
