package uk.gov.ons.ctp.response.action.export.service.impl;

import static uk.gov.ons.ctp.common.util.InputStreamUtils.getStringFromInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMapping;
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


/**
 * The implementation of the TemplateMappingService
 */
@Named
@Slf4j
public class TemplateMappingServiceImpl implements TemplateMappingService {

  public static final String EXCEPTION_STORE_TEMPLATE_MAPPING = "Issue storing TemplateMappingDocument. It appears to be empty.";

  public static final String EXCEPTION_RETRIEVING_TEMPLATE_MAPPING = "TemplateMappingDocument not found.";

  public static final String TEMPLATE_MAPPING = "templateMapping";

  @Inject
  private TemplateMappingRepository repository;

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
  public TemplateMappingDocument retrieveTemplateMappingDocument(String templateMappingName) throws CTPException {
    TemplateMappingDocument templateMapping = repository.findOne(templateMappingName);
    if (templateMapping == null) {
      log.error("No template mapping document named {} found.", templateMappingName);
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s %s", EXCEPTION_RETRIEVING_TEMPLATE_MAPPING, templateMappingName));
    }
    return templateMapping;
  }

  @Override
  public List<TemplateMappingDocument> retrieveAllTemplateMappingDocuments() {
    return repository.findAll();
  }

  @Override
  public Map<String, List<TemplateMapping>> retrieveTemplateMappingByFilename(String templateMappingName)
      throws CTPException {
    return retrieveTemplateMapping(templateMappingName).stream()
        .collect(Collectors.groupingBy(TemplateMapping::getFile));
  }

  @Override
  public Map<String, TemplateMapping> retrieveTemplateMappingByActionType(String templateMappingName)
      throws CTPException {
    Map<String, TemplateMapping> mappings = new HashMap<String, TemplateMapping>();
    retrieveTemplateMapping(templateMappingName).forEach((templateMapping) -> {
      mappings.put(templateMapping.getActionType(), templateMapping);
    });
    return mappings;
  }

  @Override
  public List<TemplateMapping> retrieveTemplateMapping(String templateMappingName) throws CTPException {
    List<TemplateMapping> mapping = new ArrayList<TemplateMapping>();
    try {
      TemplateMappingDocument templateMapping = retrieveTemplateMappingDocument(templateMappingName);
      if (templateMapping != null) {
        ObjectMapper mapper = new ObjectMapper();
        mapping = mapper.readValue(templateMapping.getContent(), new TypeReference<List<TemplateMapping>>() {
        });
      }
    } catch (JsonParseException e) {
      log.error("JsonParseException thrown while parsing mapping...", e.getMessage());
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, e.getMessage());
    } catch (JsonMappingException e) {
      log.error("JsonMappingException thrown while parsing mapping...", e.getMessage());
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, e.getMessage());
    } catch (IOException e) {
      log.error("IOException thrown while parsing mapping...", e.getMessage());
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, e.getMessage());
    }
    return mapping;
  }

}
