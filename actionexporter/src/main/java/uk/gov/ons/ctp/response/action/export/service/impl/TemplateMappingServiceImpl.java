package uk.gov.ons.ctp.response.action.export.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateEngine;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;
import uk.gov.ons.ctp.response.action.export.repository.TemplateMappingRepository;
import uk.gov.ons.ctp.response.action.export.service.TemplateMappingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@Slf4j
public class TemplateMappingServiceImpl implements TemplateMappingService {

  public static final String EXCEPTION_STORE_TEMPLATE_MAPPING =
          "Issue storing TemplateMappingDocument. It appears to be empty.";

  @Inject
  private TemplateMappingRepository repository;

  @Override
  public TemplateMappingDocument retrieveTemplateMappingDocument(String templateMappingName) {
    return repository.findOne(templateMappingName);
  }

  @Override
  public List<TemplateMappingDocument> retrieveAllTemplateMappingDocuments() {
    return repository.findAll();
  }

  @Override
  public TemplateMappingDocument storeTemplateMappingDocument(String templateMappingName, TemplateEngine templateEngine,
                                                              InputStream fileContents) throws CTPException {
    String stringValue = getStringFromInputStream(fileContents);
    if (StringUtils.isEmpty(stringValue)) {
      log.error(EXCEPTION_STORE_TEMPLATE_MAPPING);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_STORE_TEMPLATE_MAPPING);
    }
    TemplateMappingDocument templateMappingDocument = new TemplateMappingDocument();
    templateMappingDocument.setContent(stringValue);
    templateMappingDocument.setName(templateMappingName);
    templateMappingDocument.setTemplateEngine(templateEngine);
    templateMappingDocument.setDateModified(new Date());

    return repository.save(templateMappingDocument);
  }

  @Override
  public Map<String, String> retrieveMaoFromTemplateMappingDocument(String mappingName) {
    Map<String, String> mapping = new HashMap<String, String>();
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapping = mapper.readValue(repository.findOne(mappingName).getContent(),
              new TypeReference<Map<String, String>>() {
              });
    } catch (JsonParseException e) {
      log.error("JsonParseException thrown while parsing mapping...", e.getMessage());
    } catch (JsonMappingException e) {
      log.error("JsonMappingException thrown while parsing mapping...", e.getMessage());
    } catch (IOException e) {
      log.error("IOException thrown while parsing mapping...", e.getMessage());
    }
    return mapping;
  }

  // TODO to make it in Common
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
