package uk.gov.ons.ctp.response.action.export.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.repository.TemplateRepository;
import uk.gov.ons.ctp.response.action.export.service.TemplateService;

import static uk.gov.ons.ctp.common.util.InputStreamUtil.getStringFromInputStream;

/**
 * The implementation of the TemplateService
 * TODO Specific to FreeMarker at the moment with freemarker.template.Configuration, clearTemplateCache, etc.
 */
@Named
@Slf4j
public class TemplateServiceImpl implements TemplateService {

  public static final String ERROR_RETRIEVING_FREEMARKER_TEMPLATE = "Could not find FreeMarker template.";
  public static final String EXCEPTION_STORE_TEMPLATE = "Issue storing template. It appears to be empty.";

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
  public TemplateDocument storeTemplateDocument(String templateName, InputStream fileContents) throws CTPException {
    String stringValue = getStringFromInputStream(fileContents);
    if (StringUtils.isEmpty(stringValue)) {
      log.error(EXCEPTION_STORE_TEMPLATE);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_STORE_TEMPLATE);
    }

    TemplateDocument template = new TemplateDocument();
    template.setContent(stringValue);
    template.setName(templateName);
    template.setDateModified(new Date());
    template = repository.save(template);

    configuration.clearTemplateCache();

    return template;
  }

  @Override
  public File file(List<ActionRequestDocument> actionRequestDocumentList, String templateName, String path)
          throws CTPException {
    File resultFile = new File(path);
    Writer fileWriter = null;
    try {
      Template template = giveTemplate(templateName);
      fileWriter = new FileWriter(resultFile);
      template.process(buildDataModel(actionRequestDocumentList), fileWriter);
    } catch (IOException e) {
      log.error("IOException thrown while templating for file...", e.getMessage());
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, e.getMessage());
    } catch (TemplateException f) {
      log.error("TemplateException thrown while templating for file...", f.getMessage());
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, f.getMessage());
    } finally {
      if (fileWriter != null) {
        try {
          fileWriter.close();
        } catch (IOException e) {
          log.error("IOException thrown while closing the file writer...", e.getMessage());
        }
      }
    }

    return resultFile;
  }

  @Override
  public ByteArrayOutputStream stream(List<ActionRequestDocument> actionRequestDocumentList, String templateName)
          throws CTPException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Writer outputStreamWriter = null;
    try {
      Template template = giveTemplate(templateName);
      outputStreamWriter = new OutputStreamWriter(outputStream);
      template.process(buildDataModel(actionRequestDocumentList), outputStreamWriter);
      outputStreamWriter.close();
    } catch (IOException e) {
      log.error("IOException thrown while templating for stream...", e.getMessage());
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, e.getMessage());
    } catch (TemplateException f) {
      log.error("TemplateException thrown while templating for stream...", f.getMessage());
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, f.getMessage());
    } finally {
      if (outputStreamWriter != null) {
        try {
          outputStreamWriter.close();
        } catch (IOException e) {
          log.error("IOException thrown while closing the output stream writer...", e.getMessage());
        }
      }
    }

    return outputStream;
  }

  /**
   * This returns the FreeMarker template required for the transformation.
   *
   * @param templateName the FreeMarker template to use
   * @return the FreeMarker template
   * @throws IOException if issue creating the FreeMarker template
   * @throws CTPException if problem getting Freemarker template with name given
   */
  private Template giveTemplate(String templateName) throws CTPException, IOException {
    log.debug("Entering giveMeTemplate with templateName {}", templateName);
    Template template = configuration.getTemplate(templateName);
    log.debug("template = {}", template);
    if (template == null) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, ERROR_RETRIEVING_FREEMARKER_TEMPLATE);
    }
    return template;
  }

  /**
   * This builds the data model required by FreeMarker
   *
   * @param actionRequestDocumentList the list of action requests
   * @return the data model map
   */
  private Map<String, Object> buildDataModel(List<ActionRequestDocument> actionRequestDocumentList) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("actionRequests", actionRequestDocumentList);
    return result;
  }

}
