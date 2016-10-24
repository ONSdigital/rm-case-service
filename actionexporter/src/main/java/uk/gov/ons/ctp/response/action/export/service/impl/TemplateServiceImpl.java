package uk.gov.ons.ctp.response.action.export.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ContentDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateEngine;
import uk.gov.ons.ctp.response.action.export.repository.TemplateRepository;
import uk.gov.ons.ctp.response.action.export.service.TemplateService;

/**
 * The implementation of the TemplateService
 */
@Named
@Slf4j
public class TemplateServiceImpl implements TemplateService {

  public static final String EXCEPTION_STORE_TEMPLATE = "Issue storing TemplateDocument. It appears to be empty.";

  @Inject
  private TemplateRepository repository;

  @Inject
  private freemarker.template.Configuration configuration;

  @Override
  public TemplateDocument retrieveTemplateDocument(String templateName) {
    return repository.findOne(templateName);
  }

  @Override
  public List<TemplateDocument> retrieveAllTemplateDocuments() {
    return repository.findAll();
  }

  @Override
  public TemplateDocument storeTemplateDocument(String templateName, TemplateEngine templateEngine, InputStream
          fileContents) throws CTPException {
    String stringValue = getStringFromInputStream(fileContents);
    if (StringUtils.isEmpty(stringValue)) {
      log.error(EXCEPTION_STORE_TEMPLATE);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_STORE_TEMPLATE);
    }
    TemplateDocument template = new TemplateDocument();
    template.setContent(stringValue);
    template.setName(templateName);
    template.setTemplateEngine(templateEngine);
    template.setDateModified(new Date());

    // Clear cache in case updated FreeMarker content template stored
    clearTemplateCache();

    return repository.save(template);
  }

  @Override
  public void clearTemplateCache() {
    configuration.clearTemplateCache();
    log.debug("Free Marker template cache has been cleared.");
  }

  /**
   * Form content String of ContentDocument.
   * @param is InputStream of Document content
   * @return content String
   */
  private static String getStringFromInputStream(InputStream is) {
    BufferedReader br = null;
    String line;
    StringBuilder sb = new StringBuilder();
    try {
      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      }
    } catch (Exception e) {
      log.error("Exception thrown while converting template stream to string - msg = {}", e.getMessage());
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
