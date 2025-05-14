package uk.gov.ons.ctp.response.casesvc.service.action.email;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate.Handler;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseAction;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionParty;
import uk.gov.ons.ctp.response.casesvc.representation.action.NotifyModel;
import uk.gov.ons.ctp.response.casesvc.representation.action.NotifyModel.Notify.Classifiers;
import uk.gov.ons.ctp.response.casesvc.representation.action.NotifyModel.Notify.Personalisation;
import uk.gov.ons.ctp.response.casesvc.service.action.ActionTemplateService;
import uk.gov.ons.ctp.response.casesvc.service.action.common.ActionService;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.party.representation.Attributes;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

@Service
public class ProcessEmailActionService {
  private static final Logger log = LoggerFactory.getLogger(ProcessEmailActionService.class);
  public static final String DATE_FORMAT_IN_REMINDER_EMAIL = "dd/MM/yyyy";
  @Autowired private AppConfig appConfig;
  @Autowired private ActionTemplateService actionTemplateService;
  @Autowired private CaseGroupRepository caseGroupRepository;
  @Autowired private NotifyEmailService emailService;
  @Autowired private ActionService actionService;

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
      log, kv("activeEnrolment", true)
          , kv("event", eventTag)
          , kv("collectionExerciseId", collectionExerciseId.toString())
          .info("No Email Action Template defined for this event.");
      return new AsyncResult<>(true);
    }
    // Initial status of this async call will be considered as success unless the subsequent process
    // changes
    AtomicBoolean asyncEmailCallStatus = new AtomicBoolean(true);
    log.debug("Getting Email cases against collectionExerciseId and event active enrolment");
    List<CaseAction> emailCases =
        caseGroupRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, true);
    log, kv("email cases", emailCases.size())
        , kv(collectionExerciseId.toString())
        .info("Processing email cases");
    SurveyDTO survey = actionService.getSurvey(collectionExerciseDTO.getSurveyId());
    emailCases
        .parallelStream()
        .filter(caseAction -> actionService.isActionable(caseAction, actionTemplate, eventTag))
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
    Handler templateHandler = caseActionTemplate.getHandler();
    log, kv("caseId", actionCaseId)
        , kv("actionTemplate", templateType)
        , kv("actionHandler", templateHandler)
        .info("Processing Email Event.");
    boolean isSuccess = true;
    try {
      log, kv("caseId", actionCaseId).info("Getting ActionCaseParty");
      CaseActionParty actionCaseParty = actionService.setParties(caseAction, survey);
      if (isBusinessNotification(caseAction)) {
        log, kv("caseId", caseAction).info("Processing Email for isBusinessNotification true");
        actionCaseParty
            .getChildParties()
            .parallelStream()
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
        log, kv("caseId", caseAction).info("Processing Email for isBusinessNotification false");
        processEmail(
            actionCaseParty.getParentParty(),
            actionCaseParty.getChildParties().get(0),
            survey,
            caseActionTemplate,
            caseAction,
            collectionExercise);
      }
    } catch (Exception e) {
      log, kv("caseId", actionCaseId)
          , kv("actionTemplate", templateType)
          , kv("actionHandler", templateHandler)
          , kv("exception", e)
          .warn("Processing Email Event FAILED.");
      isSuccess = false;
      asyncEmailCallStatus.set(false);
    }
    if (isSuccess) {
      actionService.createCaseActionEvent(
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
    log, kv("template", caseActionTemplate.getType())
        , kv("case", caseAction.getCaseId())
        , kv("handler", caseActionTemplate.getHandler())
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
    log, kv("template", caseActionTemplate.getType())
        , kv("case", caseAction.getCaseId())
        , kv("handler", caseActionTemplate.getHandler())
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
    String formType =
        isMultipleTemplateSurvey(businessParty, survey)
            ? businessParty.getAttributes().getFormType()
            : "";
    String surveyRef = isSurveyIdSupportedTemplate(survey) ? survey.getSurveyRef() : "";
    return Classifiers.builder()
        .actionType(caseActionTemplate.getType())
        .legalBasis(survey.getLegalBasis())
        .region(businessParty.getAttributes().getRegion())
        .surveyRef(surveyRef)
        .formType(formType)
        .build();
  }

  /**
   * Checks if the classifier required is for survey id supported template
   *
   * @param survey
   * @return boolean
   */
  private boolean isSurveyIdSupportedTemplate(SurveyDTO survey) {
    String surveyIdSupportedTemplate = appConfig.getSurveySvc().getSurveyIdSupportedTemplate();
    String surveyRef = survey.getSurveyRef();
    if (isSupportedStringPresent(surveyRef, getSupportedList(surveyIdSupportedTemplate))) {
      return true;
    }
    return false;
  }

  /**
   * Checks if the classifier required is for supported multiple templates
   *
   * @param businessParty
   * @param survey
   * @return
   */
  private boolean isMultipleTemplateSurvey(PartyDTO businessParty, SurveyDTO survey) {
    String supportedMultipleSurveys =
        appConfig.getSurveySvc().getMultipleFormTypeSupportedSurveyIds();
    String supportedMultipleFormsTypes = appConfig.getSurveySvc().getMultipleFormTypeSupported();
    String surveyRef = survey.getSurveyRef();
    String formType = businessParty.getAttributes().getFormType();
    if (isSupportedStringPresent(surveyRef, getSupportedList(supportedMultipleSurveys))
        && isSupportedStringPresent(formType, getSupportedList(supportedMultipleFormsTypes))) {
      return true;
    }
    return false;
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

  /**
   * This function provides an array to the comma separated environment variables.
   *
   * @param commaSeparatedString
   * @return : Arrays of String
   */
  private String[] getSupportedList(String commaSeparatedString) {
    return commaSeparatedString.split(",");
  }

  /**
   * Takes the arrays of string and checks if the value is present.
   *
   * @param value
   * @param valueArray
   * @return boolean: True if present
   */
  private boolean isSupportedStringPresent(String value, String[] valueArray) {
    if (null == value || null == valueArray || value.isEmpty() || valueArray.length == 0) {
      return false;
    }
    return Arrays.stream(valueArray).anyMatch(value::equals);
  }
}
