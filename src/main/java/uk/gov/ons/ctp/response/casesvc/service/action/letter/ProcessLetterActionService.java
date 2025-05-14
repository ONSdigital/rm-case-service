package uk.gov.ons.ctp.response.casesvc.service.action.letter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.*;
import uk.gov.ons.ctp.response.casesvc.service.action.ActionTemplateService;
import uk.gov.ons.ctp.response.casesvc.service.action.common.ActionService;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.party.representation.Association;
import uk.gov.ons.ctp.response.lib.party.representation.Attributes;
import uk.gov.ons.ctp.response.lib.party.representation.Enrolment;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

@Service
public class ProcessLetterActionService {
  private static final Logger log = LoggerFactory.getLogger(ProcessLetterActionService.class);
  public static final String ACTIVE = "ACTIVE";
  public static final String CREATED = "CREATED";
  public static final String ENABLED = "ENABLED";
  public static final String PENDING = "PENDING";
  @Autowired private ActionTemplateService actionTemplateService;
  @Autowired private CaseGroupRepository caseGroupRepository;
  @Autowired private ActionService actionService;
  @Autowired private NotifyLetterService printFileService;

  @Async
  public Future<Boolean> processLetterService(
      CollectionExerciseDTO collectionExerciseDTO, String eventTag, Instant instant) {
    CaseActionTemplate actionTemplate =
        actionTemplateService.mapEventTagToTemplate(eventTag, false);
    UUID collectionExerciseId = collectionExerciseDTO.getId();
    if (null == actionTemplate) {
      log, kv("activeEnrolment", true)
          , kv("event", eventTag)
          , kv("collectionExerciseId", collectionExerciseId.toString())
          .info("No Template found, suggests no letters to be processed.");
      return new AsyncResult<>(true);
    }

    log.debug("Getting Letter cases against collectionExerciseId and event active enrolment");

    List<CaseAction> letterCases =
        caseGroupRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, false);
    log, kv("letter cases", letterCases.size())
        , kv(collectionExerciseId.toString())
        .info("Processing letter cases");

    SurveyDTO survey = actionService.getSurvey(collectionExerciseDTO.getSurveyId());

    List<CaseAction> actionableLetterCases =
        letterCases
            .parallelStream()
            .filter(c -> actionService.isActionable(c, actionTemplate, eventTag))
            .collect(Collectors.toList());

    if (actionableLetterCases.size() == 0) {
      log, kv("no. of cases", letterCases.size())
          , kv("actionType", actionTemplate.getType())
          , kv("collection exercise", collectionExerciseDTO.getId())
          .info(
              "No actionable cases found against the action type for collection exercise. "
                  + "Hence nothing to do");
      return new AsyncResult<>(true);
    }

    log, kv("no. of actionable cases", actionableLetterCases.size())
        , kv("actionType", actionTemplate.getType())
        .info("Populating letter data for letter cases.");

    List<LetterEntry> letterEntries =
        actionableLetterCases
            .parallelStream()
            .map(c -> getPrintFileEntry(c, actionTemplate, survey))
            .collect(Collectors.toList());

    if (actionableLetterCases.size() != letterEntries.size()) {
      log, kv("actionType", actionTemplate.getType())
          .info(
              "Unable to collect all letter entries. Hence aborting the process and will try again");
      return new AsyncResult<>(false);
    }

    log, kv("no. of actionable cases", actionableLetterCases.size())
        , kv("actionType", actionTemplate.getType())
        .info("Finished populating letter data for letter cases.");

    String fileNamePrefix =
        FilenamePrefix.getPrefix(actionTemplate.getPrefix())
            + "_"
            + survey.getSurveyRef()
            + "_"
            + getExerciseRefWithoutSurveyRef(collectionExerciseDTO.getExerciseRef());
    final String now =
        DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")
            , kvZone(ZoneId.systemDefault())
            .format(instant);
    String filename = String.format("%s_%s.csv", fileNamePrefix, now);
    log.info("filename: " + filename + ", uploading file");
    log, kv("actionType", actionTemplate.getType()).info("Processing Print File");

    boolean isSuccess = printFileService.processPrintFile(filename, letterEntries);

    log, kv("file processed?", isSuccess)
        , kv("actionType", actionTemplate.getType())
        .info("Recording case action event");
    if (isSuccess) {
      letterEntries
          .parallelStream()
          .forEach(
              letterEntry ->
                  actionService.createCaseActionEvent(
                      letterEntry.getActionCaseId(),
                      letterEntry.getActionTemplateType(),
                      letterEntry.getActionTemplateHandler(),
                      collectionExerciseDTO.getId(),
                      survey.getId(),
                      instant,
                      eventTag));
    }
    return new AsyncResult<>(isSuccess);
  }

  /**
   * Populates data for print file for case event
   *
   * @param caseAction
   * @param caseActionTemplate
   * @param survey
   * @return
   */
  private LetterEntry getPrintFileEntry(
      CaseAction caseAction, CaseActionTemplate caseActionTemplate, SurveyDTO survey) {
    log, kv("caseId", caseAction.getCaseId())
        , kv("actionTemplateType", caseActionTemplate.getType())
        .info("Getting print file entry");
    String iac = caseAction.getIac();
    String sampleUnitRef = caseAction.getSampleUnitRef();
    String status = caseAction.getStatus().toString();
    CaseActionParty actionCaseParty = actionService.setParties(caseAction, survey);
    PartyDTO businessParty = actionCaseParty.getParentParty();
    Contact contact = new Contact();
    String respondentStatus = "";
    List<PartyDTO> respondentParties = actionCaseParty.getChildParties();
    if (respondentParties.size() > 0) {
      Attributes attributes = respondentParties.get(0).getAttributes();
      contact.setEmailAddress(attributes.getEmailAddress());
      contact.setForename(attributes.getFirstName());
      contact.setSurname(attributes.getLastName());
      respondentStatus = parseRespondentStatuses(respondentParties);
      List<PartyDTO> createdRespondentParties = filterListByStatus(respondentParties, CREATED);
      if (createdRespondentParties != null && createdRespondentParties.size() > 0) {
        iac = "";
      }
    }
    log, kv("caseId", caseAction.getCaseId())
        , kv("actionTemplateType", caseActionTemplate.getType())
        .info("Finished getting print file entry");
    return new LetterEntry(
        caseAction.getCaseId(),
        caseActionTemplate.getType(),
        caseActionTemplate.getHandler(),
        sampleUnitRef,
        iac,
        status,
        getEnrolmentStatus(actionCaseParty.getParentParty()),
        respondentStatus,
        contact,
        businessParty.getAttributes().getRegion());
  }

  private String parseRespondentStatuses(final List<PartyDTO> childParties) {
    log.info("Getting respondent status");
    String respondentStatus = null;
    if (childParties != null) {
      List<PartyDTO> activeParties = filterListByStatus(childParties, ACTIVE);
      if (activeParties != null && activeParties.size() > 0) {
        respondentStatus = ACTIVE;
      } else {
        List<PartyDTO> createdParties = filterListByStatus(childParties, CREATED);
        if (createdParties != null && createdParties.size() > 0) {
          respondentStatus = CREATED;
        }
      }
    }
    return respondentStatus;
  }

  private List<PartyDTO> filterListByStatus(List<PartyDTO> parties, String status) {
    log.info("filter parties by status");
    return parties == null
        ? null
        : parties.stream().filter(p -> p.getStatus().equals(status)).collect(Collectors.toList());
  }

  private String getEnrolmentStatus(final PartyDTO parentParty) {
    log.info("getting enrolment statuss");
    final List<String> enrolmentStatuses = new ArrayList<>();
    final List<Association> associations = parentParty.getAssociations();
    if (associations != null) {
      for (Association association : associations) {
        for (Enrolment enrolment : association.getEnrolments()) {
          enrolmentStatuses.add(enrolment.getEnrolmentStatus());
        }
      }
    }
    String enrolmentStatus = null;
    if (enrolmentStatuses.contains(ENABLED)) {
      enrolmentStatus = ENABLED;
    } else if (enrolmentStatuses.contains(PENDING)) {
      enrolmentStatus = PENDING;
    }
    return enrolmentStatus;
  }

  private String getExerciseRefWithoutSurveyRef(String exerciseRef) {
    log.info("get exercise ref without survey ref");
    String exerciseRefWithoutSurveyRef = StringUtils.substringAfter(exerciseRef, "_");
    return StringUtils.defaultIfEmpty(exerciseRefWithoutSurveyRef, exerciseRef);
  }
}
