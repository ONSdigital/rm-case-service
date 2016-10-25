package uk.gov.ons.ctp.response.action.export.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;
import uk.gov.ons.ctp.response.action.export.repository.TemplateMappingRepository;
import uk.gov.ons.ctp.response.action.export.service.TemplateMappingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.ons.ctp.common.util.InputStreamUtil.getStringFromInputStream;

/**
 * The implementation of the TemplateMappingService
 */
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
  public TemplateMappingDocument storeTemplateMappingDocument(String templateMappingName, InputStream fileContents)
          throws CTPException {
    String stringValue = getStringFromInputStream(fileContents);
    if (StringUtils.isEmpty(stringValue)) {
      log.error(EXCEPTION_STORE_TEMPLATE_MAPPING);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_STORE_TEMPLATE_MAPPING);
    }
    TemplateMappingDocument templateMappingDocument = new TemplateMappingDocument();
    templateMappingDocument.setContent(stringValue);
    templateMappingDocument.setName(templateMappingName);
    templateMappingDocument.setDateModified(new Date());

    return repository.save(templateMappingDocument);
  }

  @Override
  public Map<String, String> retrieveMapFromTemplateMappingDocument(String templateMappingName) {
    Map<String, String> mapping = new HashMap<>();
    try {
      TemplateMappingDocument templateMapping = repository.findOne(templateMappingName);
      if (templateMapping != null) {
        ObjectMapper mapper = new ObjectMapper();
        mapping = mapper.readValue(templateMapping.getContent(), new TypeReference<Map<String, String>>() { });
      }
    } catch (JsonParseException e) {
      log.error("JsonParseException thrown while parsing mapping...", e.getMessage());
    } catch (JsonMappingException e) {
      log.error("JsonMappingException thrown while parsing mapping...", e.getMessage());
    } catch (IOException e) {
      log.error("IOException thrown while parsing mapping...", e.getMessage());
    }
    return mapping;
  }

}
