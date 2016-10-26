package uk.gov.ons.ctp.response.action.export.service.impl;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.SftpMessage;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.TemplateMappingService;
import uk.gov.ons.ctp.response.action.export.service.TemplateService;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

/**
 * The implementation of TransformationService
 */
@Named
@Slf4j
public class TransformationServiceImpl implements TransformationService {

  private static final String DATE_FORMAT_IN_FILE_NAMES = "ddMMyyyy_HH:mm";
  private static final String TEMPLATE_MAPPING = "templateMapping";

  @Inject
  private ActionRequestRepository actionRequestRepo;

  @Inject
  private TemplateService templateService;

  @Inject
  private TemplateMappingService templateMappingService;

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
                    templateService.stream(actionRequests, mapping.get(actionType)));
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
}
