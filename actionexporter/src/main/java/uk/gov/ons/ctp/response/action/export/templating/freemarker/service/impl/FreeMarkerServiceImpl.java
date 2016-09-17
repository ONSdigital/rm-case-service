package uk.gov.ons.ctp.response.action.export.templating.freemarker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.domain.FreeMarkerTemplate;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.repository.FreeMarkerTemplateRepository;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.service.FreeMarkerService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.Date;
import java.util.List;

@Service
@Named
@Slf4j
public class FreeMarkerServiceImpl implements FreeMarkerService {

  public static final String EXCEPTION_STORE_TEMPLATE = "Issue storing FreeMarker template. It appears to be empty.";

  @Inject
  private FreeMarkerTemplateRepository repository;

  @Override
  public FreeMarkerTemplate retrieveTemplate(String templateName) {
    return repository.findOne(templateName);
  }

  @Override
  public List<FreeMarkerTemplate> retrieveAllTemplates() {
    return repository.findAll();
  }

  @Override
  public FreeMarkerTemplate storeTemplate(String templateName, InputStream fileContents) throws CTPException {
    String stringValue = getStringFromInputStream(fileContents);
    if (StringUtils.isEmpty(stringValue)) {
      log.error(EXCEPTION_STORE_TEMPLATE);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_STORE_TEMPLATE);
    }
    FreeMarkerTemplate template = new FreeMarkerTemplate();
    template.setContent(stringValue);

    template.setName(templateName);

    template.setDateModified(new Date());

    return repository.save(template);
  }

  private static String getStringFromInputStream(InputStream is) {
    BufferedReader br = null;
    String line;
    StringBuilder sb = new StringBuilder();
    try {
      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      log.error("IOException thrown while converting template stream to string - msg = {}", e.getMessage());
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          log.error("IOException thrown while closing buffered reader used to convert template stream - msg = {}",
                  e.getMessage());
        }
      }
    }

    return sb.toString();
  }

}
