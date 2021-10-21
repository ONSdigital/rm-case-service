package uk.gov.ons.ctp.response.casesvc.service.action.common;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.client.PartySvcClientService;
import uk.gov.ons.ctp.response.casesvc.client.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseAction;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionAuditEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionAuditEventRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.ActionTemplateDTO;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionParty;
import uk.gov.ons.ctp.response.lib.party.representation.Association;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

@Service
public class ActionCommonService {
  private static final Logger log = LoggerFactory.getLogger(ActionCommonService.class);
  @Autowired private SurveySvcClientService surveySvcClientService;
  @Autowired private PartySvcClientService partySvcClientService;
  @Autowired private CaseActionAuditEventRepository actionEventAuditRepository;

  /**
   * Gets survey dto against survey id
   *
   * @param surveyId
   * @return
   */
  public SurveyDTO getSurvey(String surveyId) {
    log.with("surveyId", surveyId).debug("Getting survey");
    return surveySvcClientService.requestDetailsForSurvey(surveyId);
  }

  public CaseActionParty setParties(CaseAction caseAction, SurveyDTO survey) {
    log.with("caseId", caseAction.getCaseId())
        .with("surveyId", survey.getId())
        .info("Getting Event Party data");
    List<String> desiredEnrolmentStatuses = new ArrayList<>();
    desiredEnrolmentStatuses.add("ENABLED");
    desiredEnrolmentStatuses.add("PENDING");
    log.info("Getting parent party data");
    PartyDTO businessParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SampleUnitDTO.SampleUnitType.B.name(),
            caseAction.getPartyId(),
            survey.getId(),
            desiredEnrolmentStatuses);
    log.info("Getting child party data");
    List<PartyDTO> respondentParties = getRespondentParties(businessParty);
    log.with("caseId", caseAction.getCaseId())
        .with("surveyId", survey.getId())
        .info("Finish getting Event Party data");
    return new CaseActionParty(businessParty, respondentParties);
  }

  /**
   * gets respondent parties for business party
   *
   * @param businessParty
   * @return
   */
  private List<PartyDTO> getRespondentParties(PartyDTO businessParty) {
    log.info("getting respondent party");
    final List<String> respondentPartyIds =
        businessParty.getAssociations().stream()
            .map(Association::getPartyId)
            .collect(Collectors.toList());
    return respondentPartyIds.stream()
        .map(
            id ->
                partySvcClientService.getParty(
                    SampleUnitDTO.SampleUnitType.BI.toString(), UUID.fromString(id)))
        .collect(Collectors.toList());
  }

  public boolean isActionable(
      CaseAction caseAction, CaseActionTemplate actionTemplate, String eventTag) {
    CaseActionAuditEvent actionEvent =
        actionEventAuditRepository.findByCaseIdAndTypeAndHandlerAndTagAndStatus(
            caseAction.getCaseId(),
            actionTemplate.getType(),
            actionTemplate.getHandler(),
            eventTag,
            CaseActionAuditEvent.ActionEventStatus.PROCESSED);
    if (actionEvent != null) {
      log.with("caseId", caseAction.getCaseId())
          .with("event", eventTag)
          .with("type", actionTemplate.getType())
          .with("handler", actionTemplate.getHandler())
          .info("Event Already processed.");
    }
    return actionEvent == null;
  }

  public void createCaseActionEvent(
      UUID actionCaseId,
      String templateType,
      ActionTemplateDTO.Handler templateHandler,
      UUID collectionExerciseId,
      String surveyId,
      Instant instant,
      String eventTag) {
    log.with("caseId", actionCaseId)
        .with("actionTemplateType", templateType)
        .with("actionTemplateHandler", templateHandler)
        .info("Creating a new record.");
    CaseActionAuditEvent actionEventAudit =
        CaseActionAuditEvent.builder()
            .caseId(actionCaseId)
            .type(templateType)
            .handler(templateHandler)
            .status(CaseActionAuditEvent.ActionEventStatus.PROCESSED)
            .collectionExerciseId(collectionExerciseId)
            .processedTimestamp(Timestamp.from(instant))
            .tag(eventTag)
            .build();
    actionEventAuditRepository.save(actionEventAudit);
  }
}
