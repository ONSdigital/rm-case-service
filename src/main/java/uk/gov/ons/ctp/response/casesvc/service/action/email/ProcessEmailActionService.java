package uk.gov.ons.ctp.response.casesvc.service.action.email;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseAction;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.ActionTemplateDTO;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionParty;
import uk.gov.ons.ctp.response.casesvc.representation.action.NotifyModel;
import uk.gov.ons.ctp.response.casesvc.representation.action.NotifyModel.Notify.Classifiers;
import uk.gov.ons.ctp.response.casesvc.representation.action.NotifyModel.Notify.Personalisation;
import uk.gov.ons.ctp.response.casesvc.service.action.ActionTemplateService;
import uk.gov.ons.ctp.response.casesvc.service.action.common.ActionCommonService;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.party.representation.Attributes;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

@Service
public class ProcessEmailActionService {
  private static final Logger log = LoggerFactory.getLogger(ProcessEmailActionService.class);
  public static final String DATE_FORMAT_IN_REMINDER_EMAIL = "dd/MM/yyyy";
  @Autowired private ActionTemplateService actionTemplateService;
  @Autowired private CaseActionRepository caseActionRepository;
  @Autowired private NotifyEmailService emailService;
  @Autowired private ActionCommonService actionCommonService;

  /**
   * This async process takes collection exercise and event tag and processes the email action.
   *
   * @param collectionExerciseDTO
   * @param eventTag
   * @return Future:Boolean
   */
  @Async
  public Future<Boolean> processEmailService(
      CollectionExerciseDTO collectionExerciseDTO, String eventTag, Instant instant) {

    CaseActionTemplate actionTemplate =
        actionTemplateService.mapEventTagToTemplate(eventTag, Boolean.TRUE);
    UUID collectionExerciseId = collectionExerciseDTO.getId();
    if (null == actionTemplate) {
      log.with("activeEnrolment", Boolean.TRUE)
          .with("event", eventTag)
          .with("collectionExerciseId", collectionExerciseId.toString())
          .info("No Email Action Template defined for this event.");
      return new AsyncResult<>(Boolean.TRUE);
    }
    // Initial status of this async call will be considered as success unless the subsequent process
    // changes
    AtomicBoolean asyncEmailCallStatus = new AtomicBoolean(Boolean.TRUE);
    log.debug("Getting Email cases against collectionExerciseId and event active enrolment");
    List<CaseAction> emailCases =
        caseActionRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, Boolean.TRUE);
    log.with("email cases", emailCases.size())
        .with(collectionExerciseId.toString())
        .info("Processing email cases");
    SurveyDTO survey = actionCommonService.getSurvey(collectionExerciseDTO.getSurveyId());
    emailCases.parallelStream()
        .filter(
            caseAction -> actionCommonService.isActionable(caseAction, actionTemplate, eventTag))
        .forEach(
            caseAction ->
                processEmailCase(
                    caseAction,
                    collectionExerciseDTO,
                    survey,
                    actionTemplate,
                    instant,
                    eventTag,
                    asyncEmailCallStatus));
    return new AsyncResult<>(asyncEmailCallStatus.get());
  }

  /**
   * Processes Email Cases. Get CaseParty against the email case If BusinessNotification populate
   * email data and process it for each 'ACTIVE' respondentParty else to the Party
   *
   * @param caseAction
   * @param collectionExercise
   * @param survey
   * @param caseActionTemplate
   */
  private void processEmailCase(
      CaseAction caseAction,
      CollectionExerciseDTO collectionExercise,
      SurveyDTO survey,
      CaseActionTemplate caseActionTemplate,
      Instant instant,
      String eventTag,
      AtomicBoolean asyncEmailCallStatus) {
    UUID actionCaseId = caseAction.getCaseId();
    String templateType = caseActionTemplate.getType();
    ActionTemplateDTO.Handler templateHandler = caseActionTemplate.getHandler();
    log.with("caseId", actionCaseId)
        .with("actionTemplate", templateType)
        .with("actionHandler", templateHandler)
        .info("Processing Email Event.");
    boolean isSuccess = Boolean.TRUE;
    try {
      log.with("caseId", actionCaseId).info("Getting ActionCaseParty");
      CaseActionParty actionCaseParty = actionCommonService.setParties(caseAction, survey);
      if (isBusinessNotification(caseAction)) {
        log.with("caseId", caseAction).info("Processing Email for isBusinessNotification true");
        actionCaseParty.getChildParties().parallelStream()
            .forEach(
                respondentParty ->
                    processEmail(
                        actionCaseParty.getParentParty(),
                        respondentParty,
                        survey,
                        caseActionTemplate,
                        caseAction,
                        collectionExercise));
      } else {
        log.with("caseId", caseAction).info("Processing Email for isBusinessNotification false");
        processEmail(
            actionCaseParty.getParentParty(),
            actionCaseParty.getChildParties().get(0),
            survey,
            caseActionTemplate,
            caseAction,
            collectionExercise);
      }
    } catch (Exception e) {
      log.with("caseId", actionCaseId)
          .with("actionTemplate", templateType)
          .with("actionHandler", templateHandler)
          .warn("Processing Email Event FAILED.");
      isSuccess = Boolean.FALSE;
      asyncEmailCallStatus.set(Boolean.FALSE);
    }
    if (isSuccess) {
      actionCommonService.createCaseActionEvent(
          actionCaseId,
          templateType,
          templateHandler,
          collectionExercise.getId(),
          survey.getId(),
          instant,
          eventTag);
    }
  }

  /**
   * Processes Email. Populates email data NotifyModel
   *
   * @param businessParty
   * @param respondentParty
   * @param survey
   * @param caseActionTemplate
   * @param caseAction
   * @param collectionExercise
   */
  private void processEmail(
      PartyDTO businessParty,
      PartyDTO respondentParty,
      SurveyDTO survey,
      CaseActionTemplate caseActionTemplate,
      CaseAction caseAction,
      CollectionExerciseDTO collectionExercise) {
    log.with("template", caseActionTemplate.getType())
        .with("case", caseAction.getCaseId())
        .with("handler", caseActionTemplate.getHandler())
        .info("Collecting email data.");
    String sampleUnitRef = caseAction.getSampleUnitRef();
    Classifiers classifiers = getClassifiers(businessParty, survey, caseActionTemplate);
    Personalisation personalisation =
        getPersonalisation(
            businessParty, respondentParty, survey, sampleUnitRef, collectionExercise);
    NotifyModel payload =
        new NotifyModel(
            NotifyModel.Notify.builder()
                .personalisation(personalisation)
                .classifiers(classifiers)
                .emailAddress(respondentParty.getAttributes().getEmailAddress())
                .reference(survey.getSurveyRef() + "-" + sampleUnitRef)
                .build());
    log.with("template", caseActionTemplate.getType())
        .with("case", caseAction.getCaseId())
        .with("handler", caseActionTemplate.getHandler())
        .info("sending email data to pubsub.");
    emailService.processEmail(payload);
  }

  /**
   * * gets email personalisation data
   *
   * @param businessParty
   * @param respondentParty
   * @param survey
   * @param sampleUnitRef
   * @param collectionExercise
   * @return
   */
  private Personalisation getPersonalisation(
      PartyDTO businessParty,
      PartyDTO respondentParty,
      SurveyDTO survey,
      String sampleUnitRef,
      CollectionExerciseDTO collectionExercise) {
    log.info("collecting personalisation for email");
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_IN_REMINDER_EMAIL);
    return Personalisation.builder()
        .firstname(respondentParty.getAttributes().getFirstName())
        .lastname(respondentParty.getAttributes().getLastName())
        .reportingUnitReference(sampleUnitRef)
        .returnByDate(dateFormat.format(collectionExercise.getScheduledReturnDateTime()))
        .tradingSyle(generateTradingStyle(businessParty.getAttributes()))
        .ruName(businessParty.getName())
        .surveyId(survey.getSurveyRef())
        .surveyName(survey.getLongName())
        .respondentPeriod(collectionExercise.getUserDescription())
        .build();
  }

  /**
   * gets classifiers data for the email
   *
   * @param businessParty
   * @param survey
   * @param caseActionTemplate
   * @return
   */
  private Classifiers getClassifiers(
      PartyDTO businessParty, SurveyDTO survey, CaseActionTemplate caseActionTemplate) {
    log.info("collecting classifiers for email");
    return Classifiers.builder()
        .actionType(caseActionTemplate.getType())
        .legalBasis(survey.getLegalBasis())
        .region(businessParty.getAttributes().getRegion())
        .surveyRef(survey.getSurveyRef())
        .build();
  }

  private String generateTradingStyle(final Attributes businessUnitAttributes) {
    log.info("Generate trading style");
    final List<String> tradeStyles =
        Arrays.asList(
            businessUnitAttributes.getTradstyle1(),
            businessUnitAttributes.getTradstyle2(),
            businessUnitAttributes.getTradstyle3());
    return tradeStyles.stream().filter(Objects::nonNull).collect(Collectors.joining(" "));
  }

  /**
   * Check if the sample type associated to case is of type B
   *
   * @param caseAction
   * @return
   */
  private boolean isBusinessNotification(CaseAction caseAction) {
    return caseAction.getSampleUnitType().equals(SampleUnitDTO.SampleUnitType.B.name());
  }
}
