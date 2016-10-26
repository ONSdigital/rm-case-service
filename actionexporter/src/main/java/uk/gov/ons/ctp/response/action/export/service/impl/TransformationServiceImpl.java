package uk.gov.ons.ctp.response.action.export.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.SftpMessage;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.TemplateMappingService;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

/**
 * The implementation of TransformationService
 */
@Named
@Slf4j
public class TransformationServiceImpl implements TransformationService {

  public static final String ERROR_RETRIEVING_FREEMARKER_TEMPLATE = "Could not find FreeMarker template.";

  private static final String DATE_FORMAT_IN_FILE_NAMES = "ddMMyyyy_HH:mm";
  private static final String TEMPLATE_MAPPING = "templateMapping";

  @Inject
  private freemarker.template.Configuration configuration;

  @Inject
  private ActionRequestRepository actionRequestRepo;

  @Inject
  private TemplateMappingService templateMappingService;

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

  @Override
  public SftpMessage processActionRequests() {
    List<ActionRequestDocument> requests = actionRequestRepo.findByDateSentIsNullOrderByActionTypeDesc();
    return buildSftpMessage(requests);
  }

  @Override
  public SftpMessage processActionRequest(ActionRequestDocument actionRequestDocument) {
    List<ActionRequestDocument> requests = new ArrayList<>();
    requests.add(actionRequestDocument);
    return buildSftpMessage(requests);
  }

  /**
   * Produces SftpMessage with stream objects and list of ActionRequest Ids.
   * @param actionRequestDocumentList the list to be processed
   * @return SftpMessage with stream objects and list of ActionRequest Ids.
   */
  private SftpMessage buildSftpMessage(List<ActionRequestDocument> actionRequestDocumentList) {
    Map<String, List<String>> actionIds = new HashMap();
    Map<String, ByteArrayOutputStream> outputStreams = new HashMap();
    SftpMessage sftpMessage = new SftpMessage(actionIds, outputStreams);

    if (actionRequestDocumentList.isEmpty()) {
      log.warn("No Action Export requests to process.");
      return sftpMessage;
    }

    Map<String, Map<String, List<ActionRequestDocument>>> templateRequests = actionRequestDocumentList.stream()
            .collect(Collectors.groupingBy(ActionRequestDocument::getActionPlan,
                    Collectors.groupingBy(ActionRequestDocument::getActionType)));
    Map<String, String> mapping = templateMappingService.retrieveMapFromTemplateMappingDocument(TEMPLATE_MAPPING);
    String timeStamp = new SimpleDateFormat(DATE_FORMAT_IN_FILE_NAMES).format(Calendar.getInstance().getTime());
    templateRequests.forEach((actionPlan, actionPlans) -> {
      actionPlans.forEach((actionType, actionRequests) -> {
        if (mapping.containsKey(actionType)) {
          try {
            outputStreams.put(actionPlan + "_" + actionType + "_" + timeStamp + ".csv",
                    stream(actionRequests, mapping.get(actionType)));
            List<String> addActionIds = new ArrayList<String>();
            actionIds.put(actionPlan + "_" + actionType + "_" + timeStamp + ".csv", addActionIds);
            actionRequests.forEach((actionRequest) -> {
              addActionIds.add(actionRequest.getActionId().toString());
            });
          } catch (CTPException e) {
            log.error("Error generating actionType : {}.", actionType);
          }
        } else {
          log.warn("No mapping for actionType : {}.", actionType);
        }
      });
    });
    return sftpMessage;
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
