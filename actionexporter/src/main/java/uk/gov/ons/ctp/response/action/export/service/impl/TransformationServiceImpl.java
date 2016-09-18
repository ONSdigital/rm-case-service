package uk.gov.ons.ctp.response.action.export.service.impl;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The implementation of TransformationService
 */
@Named
@Slf4j
public class TransformationServiceImpl implements TransformationService {

  public static final String ERROR_RETRIEVING_FREEMARKER_TEMPLATE = "Could not find FreeMarker template.";

  @Inject
  private freemarker.template.Configuration configuration;

  @Override
  public File fileMe(List<ActionRequestDocument> actionRequestDocumentList, String templateName, String path)
          throws CTPException {
    File resultFile = new File(path);
    Writer fileWriter = null;
    try {
      Template template = giveMeTemplate(templateName);
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
  public ByteArrayOutputStream streamMe(List<ActionRequestDocument> actionRequestDocumentList, String templateName)
          throws CTPException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Writer outputStreamWriter = null;
    try {
      Template template = giveMeTemplate(templateName);
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
   * @param templateName the FreeMarker template to use
   * @return the FreeMarker template
   * @throws IOException if issue creating the FreeMarker template
   */
  private Template giveMeTemplate(String templateName) throws CTPException, IOException {
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
   * @param actionRequestDocumentList the list of action requests
   * @return the data model map
   */
  private Map<String, Object> buildDataModel(List<ActionRequestDocument> actionRequestDocumentList) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("actionRequests", actionRequestDocumentList);
    return result;
  }
}
