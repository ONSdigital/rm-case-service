package uk.gov.ons.ctp.response.casesvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.casesvc.client.ActionSvcClient;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.client.InternetAccessCodeSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.Response;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseIacAuditRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnit;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;

/**
 * A CaseService implementation which encapsulates all business logic operating on the Case entity
 * model.
 */
@Service
public class CaseService {
  private static final Logger log = LoggerFactory.getLogger(CaseService.class);

  public static final String WRONG_OLD_SAMPLE_UNIT_TYPE_MSG =
      "Old Case has sampleUnitType %s. It is expected to have sampleUnitType %s.";

  private static final String CASE_CREATED_EVENT_DESCRIPTION = "Case created when %s";

  private static final int TRANSACTION_TIMEOUT = 30;

  private CaseRepository caseRepo;
  private CaseEventRepository caseEventRepo;
  private CaseGroupRepository caseGroupRepo;
  private CaseIacAuditRepository caseIacAuditRepo;
  private CategoryRepository categoryRepo;

  private ActionSvcClient actionSvcClient;
  private CollectionExerciseSvcClient collectionExerciseSvcClient;
  private CaseGroupService caseGroupService;
  private InternetAccessCodeSvcClient internetAccessCodeSvcClient;
  private CaseIACService caseIacAuditService;
  private CaseNotificationPublisher notificationPublisher;
  private StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  /** Constructor for CaseService */
  public CaseService(
      final CaseRepository caseRepo,
      final CaseEventRepository caseEventRepo,
      final CaseGroupRepository caseGroupRepo,
      final CaseIacAuditRepository caseIacAuditRepo,
      final CategoryRepository categoryRepo,
      final ActionSvcClient actionSvcClient,
      final CollectionExerciseSvcClient collectionExerciseSvcClient,
      final CaseGroupService caseGroupService,
      final InternetAccessCodeSvcClient internetAccessCodeSvcClient,
      final CaseIACService caseIacAuditService,
      final CaseNotificationPublisher notificationPublisher,
      final StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager) {
    this.caseRepo = caseRepo;
    this.caseEventRepo = caseEventRepo;
    this.caseGroupRepo = caseGroupRepo;
    this.caseIacAuditRepo = caseIacAuditRepo;
    this.categoryRepo = categoryRepo;
    this.actionSvcClient = actionSvcClient;
    this.collectionExerciseSvcClient = collectionExerciseSvcClient;
    this.caseGroupService = caseGroupService;
    this.internetAccessCodeSvcClient = internetAccessCodeSvcClient;
    this.caseIacAuditService = caseIacAuditService;
    this.notificationPublisher = notificationPublisher;
    this.caseSvcStateTransitionManager = caseSvcStateTransitionManager;
  }

  /**
   * Find Case entity by UUID.
   *
   * @param id Unique Case UUID
   * @return Case object or null
   */
  public Case findCaseById(final UUID id) {
    log.debug("Entering findCaseById");
    Case caze = caseRepo.findById(id);

    if (caze != null) {
      String iac = caseIacAuditService.findCaseIacByCasePK(caze.getCasePK());
      caze.setIac(iac);
    } else {
      log.with("case_id", id.toString()).warn("Could not find case");
    }

    return caze;
  }

  /**
   * Find Case entity by IAC.
   *
   * @param iac The IAC.
   * @return Case object or null.
   * @throws CTPException if more than one case found for a given IAC
   */
  public Case findCaseByIac(final String iac) throws CTPException {
    log.debug("Entering findCaseByIac");

    CaseIacAudit caseIacAudit = caseIacAuditService.findCaseByIac(iac);
    Case caze = caseRepo.findByCasePK(caseIacAudit.getCaseFK());

    return caze;
  }

  /**
   * Find the cases in a casegroup.
   *
   * @param caseGroupFK the group.
   * @return the cases in the group.
   */
  public List<Case> findCasesByCaseGroupFK(final Integer caseGroupFK) {
    log.debug("Entering findCasesByCaseGroupFK");
    return caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(caseGroupFK);
  }

  /**
   * Find the cases for a partyId.
   *
   * @param partyId the partyId
   * @return the cases for the partyId
   */
  public List<Case> findCasesByPartyId(final UUID partyId) {
    log.debug("Entering findCasesByPartyId");
    List<Case> cazes = caseRepo.findByPartyId(partyId);
    cazes.stream().forEach(c -> c.setIac(caseIacAuditService.findCaseIacByCasePK(c.getCasePK())));

    return cazes;
  }

  /**
   * Find CaseEvent entities associated with a Case.
   *
   * @param caseFK Case ForeignKey.
   * @return List of CaseEvent entities or empty List.
   */
  public List<CaseEvent> findCaseEventsByCaseFK(final Integer caseFK) {
    log.debug("Entering findCaseEventsByCaseFK");
    return caseEventRepo.findByCaseFKOrderByCreatedDateTimeDesc(caseFK);
  }

  /**
   * Not sure this is the best place for this method, but .. several parts of case svc need to build
   * a CaseNotification for a Case and need the services of the ActionPlanMappingService to get the
   * actionPlanId This method just creates a CaseNotification Not sure this is the best place for
   * this method, but .. several parts of case svc need to build a CaseNotification for a Case and
   * need the services of the ActionPlanMappingService to get the actionPlanId This method just
   * creates a CaseNotification
   *
   * @param caze The Case
   * @param transitionEvent the event to inform the recipient of
   * @return the newly created notification object
   */
  public CaseNotification prepareCaseNotification(Case caze, CaseDTO.CaseEvent transitionEvent) {
    CaseGroup caseGroup = caseGroupRepo.findOne(caze.getCaseGroupFK());
    return new CaseNotification(
        Objects.toString(caze.getSampleUnitId(), null),
        caze.getId().toString(),
        caze.getActionPlanId().toString(),
        caseGroup.getCollectionExerciseId().toString(),
        Objects.toString(caze.getPartyId(), null),
        caze.getSampleUnitType().toString(),
        NotificationType.valueOf(transitionEvent.name()));
  }

  /**
   * Create a CaseEvent from the details provided in the passed CaseEvent. Some events will also as
   * a side effect create a new case - if so the details must be provided in the newCase argument,
   * otherwise it may remain null. If the newCase is passed it must also contain the contact details
   * for the new case.
   *
   * @param caseEvent CaseEvent to be created.
   * @return the created CaseEvent.
   * @throws CTPException when case state transition error
   */
  public CaseEvent createCaseEvent(final CaseEvent caseEvent) throws CTPException {
    return createCaseEvent(caseEvent, DateTimeUtil.nowUTC());
  }

  /**
   * Create a CaseEvent for the specific scenario of an incoming CaseReceipt (sent by the SDX
   * Gateway and containing the responseDateTime of the online/paper response).
   *
   * @param caseEvent CaseEvent to be created
   * @param targetCase case to post caseEvent against
   * @return the created CaseEvent.
   * @throws CTPException when case state transition error
   */
  public CaseEvent createCaseEvent(final CaseEvent caseEvent, final Case targetCase)
      throws CTPException {
    return createCaseEvent(caseEvent, DateTimeUtil.nowUTC(), targetCase);
  }

  /**
   * Create a CaseEvent for the specific scenario of an incoming CaseReceipt (sent by the SDX
   * Gateway and containing the responseDateTime of the online/paper response).
   *
   * @param caseEvent CaseEvent to be created
   * @param timestamp timestamp equals to the incoming CaseReceipt's responseDateTime.
   * @return the created CaseEvent.
   * @throws CTPException when case state transition error
   */
  public CaseEvent createCaseEvent(final CaseEvent caseEvent, final Timestamp timestamp)
      throws CTPException {
    log.with("case_event", caseEvent).debug("Creating case event");

    Case targetCase = caseRepo.findOne(caseEvent.getCaseFK());
    log.with("target_case", targetCase).debug("Found target case");
    if (targetCase == null) {
      return null;
    }
    return createCaseEvent(caseEvent, timestamp, targetCase);
  }

  /**
   * Create a CaseEvent for the specific scenario of an incoming CaseReceipt (sent by the SDX
   * Gateway and containing the responseDateTime of the online/paper response).
   *
   * @param caseEvent CaseEvent to be created
   * @param timestamp timestamp equals to the incoming CaseReceipt's responseDateTime.
   * @param targetCase case to post caseEvent against
   * @return the created CaseEvent.
   * @throws CTPException when case state transition error
   */
  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public CaseEvent createCaseEvent(
      final CaseEvent caseEvent, final Timestamp timestamp, final Case targetCase)
      throws CTPException {
    log.with("case_event", caseEvent).debug("Creating case event");

    Category category = categoryRepo.findOne(caseEvent.getCategory());
    validateCaseEventRequest(category, targetCase);

    // save the case event to db
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    CaseEvent createdCaseEvent = caseEventRepo.save(caseEvent);
    log.debug("createdCaseEvent is {}", createdCaseEvent);

    // do we need to record a response?
    recordCaseResponse(category, targetCase, timestamp);

    transitionCaseGroupStatus(targetCase, caseEvent);

    switch (caseEvent.getCategory()) {
      case RESPONDENT_ENROLED:
        processActionPlanChange(targetCase, true);
        break;
      case GENERATE_ENROLMENT_CODE:
        generateAndStoreNewIAC(targetCase);
        break;
      case NO_ACTIVE_ENROLMENTS:
        generateAndStoreNewIAC(targetCase);
        processActionPlanChange(targetCase, false);
        break;
      default:
        // Do nothing
        break;
    }

    effectTargetCaseStateTransition(category, targetCase);
    log.with("case_event", caseEvent).debug("Successfully created case event");
    return createdCaseEvent;
  }

  /**
   * Add new row to caseiacaudit table with casefk and iac of given case
   *
   * @param updatedCase Case to create audit table row for
   */
  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public void saveCaseIacAudit(final Case updatedCase) {
    log.with("case_id", updatedCase.getId()).debug("Saving case iac audit");
    CaseIacAudit caseIacAudit = new CaseIacAudit();
    caseIacAudit.setCaseFK(updatedCase.getCasePK());
    caseIacAudit.setIac(updatedCase.getIac());
    caseIacAudit.setCreatedDateTime(DateTimeUtil.nowUTC());
    caseIacAuditRepo.saveAndFlush(caseIacAudit);
  }

  /**
   * Overwrites existing iac for given case if iac is not active and adds new iac to caseiacaudit
   * table
   *
   * @param targetCase Case to update
   */
  private void generateAndStoreNewIAC(final Case targetCase) {
    String iac = targetCase.getIac();
    if (iac == null || !internetAccessCodeSvcClient.isIacActive(iac)) {
      log.with("case_id", targetCase.getId()).debug("Replacing existing case IAC");
      String newIac = internetAccessCodeSvcClient.generateIACs(1).get(0);
      targetCase.setIac(newIac);
      caseRepo.saveAndFlush(targetCase);
      saveCaseIacAudit(targetCase);
    } else {
      log.with("case_id", targetCase.getId()).debug("Existing IAC is still active");
    }
  }

  private void transitionCaseGroupStatus(final Case targetCase, final CaseEvent caseEvent) {
    CaseGroup caseGroup = caseGroupRepo.findOne(targetCase.getCaseGroupFK());
    try {
      caseGroupService.transitionCaseGroupStatus(
          caseGroup, caseEvent.getCategory(), targetCase.getPartyId());
    } catch (CTPException e) {
      // The transition manager throws an exception if the event doesn't cause a transition, however
      // there are lots of
      // events which do not cause CaseGroupStatus transitions, (this is valid behaviour).
      log.debug(e.getMessage());
    }
  }

  private void processActionPlanChange(final Case targetCase, final boolean enrolments)
      throws CTPException {

    List<CaseGroup> caseGroups =
        caseGroupService.findCaseGroupsForExecutedCollectionExercises(targetCase);

    for (CaseGroup caseGroup : caseGroups) {

      if (caseGroup.getStatus() == CaseGroupStatus.NOTSTARTED
          || caseGroup.getStatus() == CaseGroupStatus.INPROGRESS) {

        // fetch all B and BI cases associated to the case group being processed
        List<Case> cases =
            caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(caseGroup.getCaseGroupPK());

        List<ActionPlanDTO> actionPlans =
            actionSvcClient.getActionPlans(caseGroup.getCollectionExerciseId(), enrolments);

        if (actionPlans == null || actionPlans.size() != 1) {
          log.with("collection_exercise_id", caseGroup.getCollectionExerciseId())
              .with("enrolments", enrolments)
              .error("One action plan expected");
          throw new IllegalStateException(
              "Expected one action plan for collection exercise with enrolmentStatus");
        }

        for (Case caze : cases) {
          if (caze.getSampleUnitType() == SampleUnitType.B) {
            caze.setActionPlanId(actionPlans.get(0).getId());
            caseRepo.saveAndFlush(caze);
            notificationPublisher.sendNotification(
                prepareCaseNotification(caze, CaseDTO.CaseEvent.ACTIONPLAN_CHANGED));
          }
        }
      }
    }
  }

  @Transactional
  public void createInitialCase(SampleUnitParent sampleUnitParent) {
    CaseGroup newCaseGroup = createNewCaseGroup(sampleUnitParent);
    log.with("case_group_id", newCaseGroup.getId()).debug("Created new casegroup");

    Category category = new Category();
    category.setShortDescription("Initial creation of case");

    Case parentCase = createNewCase(sampleUnitParent, newCaseGroup);
    if (sampleUnitParent.getSampleUnitChildren() != null
        && !sampleUnitParent.getSampleUnitChildren().getSampleUnitchildren().isEmpty()) {
      parentCase.setState(CaseState.INACTIONABLE);

      for (SampleUnit sampleUnitChild :
          sampleUnitParent.getSampleUnitChildren().getSampleUnitchildren()) {
        Case childCase = createNewCase(sampleUnitChild, newCaseGroup);
        caseRepo.saveAndFlush(childCase);
        createCaseCreatedEvent(childCase, category);
        log.with("case_id", childCase.getId().toString())
            .with("sample_unit_type", childCase.getSampleUnitType().toString())
            .debug("New Case created");
      }
    }
    caseRepo.saveAndFlush(parentCase);
    createCaseCreatedEvent(parentCase, category);
    log.with("case_id", parentCase.getId().toString())
        .with("sample_unit_type", parentCase.getSampleUnitType().toString())
        .debug("New Case created");
  }

  /**
   * Upfront fail fast validation - if this event is going to require a new case to be created,
   * let's check the request is valid before we do something we cannot rollback ie IAC disable, or
   * Action creation.
   *
   * @param category the category details
   * @param oldCase the case the event is being created against
   * @throws CTPException if the CaseEventRequest is invalid
   */
  private void validateCaseEventRequest(Category category, Case oldCase) throws CTPException {
    String oldCaseSampleUnitType = oldCase.getSampleUnitType().name();
    String expectedOldCaseSampleUnitTypes = category.getOldCaseSampleUnitTypes();
    if (!compareOldCaseSampleUnitType(oldCaseSampleUnitType, expectedOldCaseSampleUnitTypes)) {
      String errorMsg =
          String.format(
              WRONG_OLD_SAMPLE_UNIT_TYPE_MSG,
              oldCaseSampleUnitType,
              expectedOldCaseSampleUnitTypes);
      log.error(errorMsg);
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED, errorMsg);
    }
  }

  /**
   * To compare the old case sample unit type with the expected sample unit types
   *
   * @param oldCaseSampleUnitType the old case sample unit type
   * @param expectedOldCaseSampleUnitTypes a comma separated list of expected sample unit types
   * @return true if the expected types contain the old case sample unit type
   */
  private boolean compareOldCaseSampleUnitType(
      String oldCaseSampleUnitType, String expectedOldCaseSampleUnitTypes) {
    boolean result = false;
    if (expectedOldCaseSampleUnitTypes != null) {
      List<String> expectedTypes = Arrays.asList(expectedOldCaseSampleUnitTypes.split("\\s*,\\s*"));
      if (oldCaseSampleUnitType != null && expectedTypes.contains(oldCaseSampleUnitType)) {
        result = true;
      }
    }
    return result;
  }

  /**
   * Create the new Case.
   *
   * @param caseData SampleUnit from which to create Case.
   * @param caseGroup to which Case belongs.
   * @return newCase created Case.
   */
  private Case createNewCase(SampleUnit caseData, CaseGroup caseGroup) {
    Case newCase = new Case();
    newCase.setId(UUID.randomUUID());

    // values from case group
    newCase.setCaseGroupId(caseGroup.getId());
    newCase.setCaseGroupFK(caseGroup.getCaseGroupPK());
    newCase.setSampleUnitId(UUID.fromString(caseData.getId()));

    // set case values from sampleUnit
    newCase.setSampleUnitType(SampleUnitDTO.SampleUnitType.valueOf(caseData.getSampleUnitType()));

    if (caseData.getPartyId() != null) {
      newCase.setPartyId(UUID.fromString(caseData.getPartyId()));
    }

    newCase.setCollectionInstrumentId(UUID.fromString(caseData.getCollectionInstrumentId()));
    newCase.setActionPlanId(UUID.fromString(caseData.getActionPlanId()));

    // HardCoded values
    newCase.setState(CaseState.SAMPLED_INIT);
    newCase.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCase.setCreatedBy(Constants.SYSTEM);

    return newCase;
  }

  /**
   * Check to see if the event requires a response to be recorded for the case and if so ... record
   * it
   *
   * @param category the category details of the event
   * @param targetCase the 'source' case the event is being created for
   * @param timestamp timestamp the timestamp of the CaseResponse
   */
  private void recordCaseResponse(Category category, Case targetCase, Timestamp timestamp) {
    InboundChannel channel = null;
    if (category.getCategoryName() == CategoryName.OFFLINE_RESPONSE_PROCESSED) {
      channel = InboundChannel.OFFLINE;
    } else if (category.getCategoryName() == CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE) {
      channel = InboundChannel.ONLINE;
    } else if (category.getCategoryName() == CategoryName.PAPER_QUESTIONNAIRE_RESPONSE) {
      channel = InboundChannel.PAPER;
    }

    if (channel != null) {
      Response response =
          Response.builder()
              .inboundChannel(channel)
              .caseFK(targetCase.getCasePK())
              .dateTime(timestamp)
              .build();
      targetCase.getResponses().add(response);
      caseRepo.save(targetCase);
    }
  }

  /**
   * Effect a state transition for the target case if the category indicates one is required If a
   * transition was made and the state changes as a result, notify the action service of the state
   * change AND if the event was type DISABLED then also call the IAC service to disable/deactivate
   * the IAC code related to the target case.
   *
   * @param category the category details of the event
   * @param targetCase the 'source' case the event is being created for
   * @throws CTPException when case state transition error
   */
  private void effectTargetCaseStateTransition(Category category, Case targetCase)
      throws CTPException {
    CaseDTO.CaseEvent transitionEvent = category.getEventType();
    if (transitionEvent != null) {
      if ((transitionEvent == CaseDTO.CaseEvent.DISABLED
              && !category
                  .getCategoryName()
                  .equals(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD))
          || transitionEvent == CaseDTO.CaseEvent.ACTIONPLAN_CHANGED) {
        caseIacAuditService.disableAllIACsForCase(targetCase);
      }
      CaseState oldState = targetCase.getState();
      CaseState newState = caseSvcStateTransitionManager.transition(oldState, transitionEvent);

      if (!oldState.equals(newState)) {
        targetCase.setState(newState);
        caseRepo.saveAndFlush(targetCase);
        notificationPublisher.sendNotification(
            prepareCaseNotification(targetCase, transitionEvent));
      }
    }
  }

  /**
   * Create an event for a newly created case
   *
   * @param caze the case for which we want to record the event
   * @param caseEventCategory the category of the event that led to the creation of the case
   */
  private void createCaseCreatedEvent(Case caze, Category caseEventCategory) {
    CaseEvent newCaseCaseEvent = new CaseEvent();
    newCaseCaseEvent.setCaseFK(caze.getCasePK());
    newCaseCaseEvent.setCategory(CategoryDTO.CategoryName.CASE_CREATED);
    newCaseCaseEvent.setCreatedBy(Constants.SYSTEM);
    newCaseCaseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCaseCaseEvent.setDescription(
        String.format(CASE_CREATED_EVENT_DESCRIPTION, caseEventCategory.getShortDescription()));

    caseEventRepo.saveAndFlush(newCaseCaseEvent);
  }

  /**
   * Create the CaseGroup for the Case.
   *
   * @param caseGroupData SampleUnitParent from which to create CaseGroup.
   * @return newcaseGroup created caseGroup.
   */
  private CaseGroup createNewCaseGroup(SampleUnitParent caseGroupData) {
    CaseGroup newCaseGroup = new CaseGroup();

    newCaseGroup.setId(UUID.randomUUID());
    if (caseGroupData.getPartyId() != null) {
      newCaseGroup.setPartyId(UUID.fromString(caseGroupData.getPartyId()));
    }
    newCaseGroup.setCollectionExerciseId(UUID.fromString(caseGroupData.getCollectionExerciseId()));
    newCaseGroup.setSampleUnitRef(caseGroupData.getSampleUnitRef());
    newCaseGroup.setSampleUnitType(caseGroupData.getSampleUnitType());
    newCaseGroup.setStatus(CaseGroupStatus.NOTSTARTED);

    CollectionExerciseDTO collectionExercise =
        collectionExerciseSvcClient.getCollectionExercise(
            UUID.fromString(caseGroupData.getCollectionExerciseId()));

    newCaseGroup.setSurveyId(UUID.fromString(collectionExercise.getSurveyId()));

    caseGroupRepo.saveAndFlush(newCaseGroup);
    log.with("case_group_id", newCaseGroup.getId().toString()).debug("New CaseGroup created");
    return newCaseGroup;
  }

  /**
   * Get a case by the sample unit id it relates to
   *
   * @param sampleUnitId: sample unit id
   * @return the case
   */
  public Case findCaseBySampleUnitId(UUID sampleUnitId) {
    return caseRepo.findBySampleUnitId(sampleUnitId);
  }

  public List<CaseEvent> findCaseEventsByCaseFKAndCategory(Integer casePK, List<String> categories)
      throws CTPException {
    try {
      Set<CategoryName> categoryNames =
          categories.stream().map(CategoryDTO.CategoryName::fromValue).collect(Collectors.toSet());
      return caseEventRepo.findByCaseFKAndCategoryInOrderByCreatedDateTimeDesc(
          casePK, categoryNames);
    } catch (IllegalArgumentException exc) {
      throw new CTPException(CTPException.Fault.BAD_REQUEST, exc.getMessage());
    }
  }
}
