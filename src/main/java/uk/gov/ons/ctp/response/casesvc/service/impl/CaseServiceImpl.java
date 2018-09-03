package uk.gov.ons.ctp.response.casesvc.service.impl;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
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
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseIACService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CollectionExerciseSvcClientService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;
import uk.gov.ons.ctp.response.collection.exercise.representation.CaseTypeDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;

/**
 * A CaseService implementation which encapsulates all business logic operating on the Case entity
 * model.
 */
@Service
public class CaseServiceImpl implements CaseService {
  private static final Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);

  public static final String IAC_OVERUSE_MSG = "More than one case found to be using IAC %s";
  public static final String MISSING_NEW_CASE_MSG = "New Case definition missing for case %s";
  public static final String WRONG_OLD_SAMPLE_UNIT_TYPE_MSG =
      "Old Case has sampleUnitType %s. It is expected to have sampleUnitType %s.";

  private static final String CASE_CREATED_EVENT_DESCRIPTION = "Case created when %s";
  private static final String MISSING_EXISTING_CASE_MSG = "No existing Case found for caseFK %d";

  private static final int TRANSACTION_TIMEOUT = 30;

  private CaseRepository caseRepo;
  private CaseEventRepository caseEventRepo;
  private CaseGroupRepository caseGroupRepo;
  private CaseIacAuditRepository caseIacAuditRepo;
  private CategoryRepository categoryRepo;

  private ActionSvcClientService actionSvcClientService;
  private CaseGroupService caseGroupService;
  private CollectionExerciseSvcClientService collectionExerciseSvcClientService;
  private InternetAccessCodeSvcClientService internetAccessCodeSvcClientService;
  private CaseIACService caseIacAuditService;

  private CaseNotificationPublisher notificationPublisher;
  private StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  /** Constructor for CaseServiceImpl */
  @Autowired
  public CaseServiceImpl(
      final CaseRepository caseRepo,
      final CaseEventRepository caseEventRepo,
      final CaseGroupRepository caseGroupRepo,
      final CaseIacAuditRepository caseIacAuditRepo,
      final CategoryRepository categoryRepo,
      final ActionSvcClientService actionSvcClientService,
      final CaseGroupService caseGroupService,
      final CollectionExerciseSvcClientService collectionExerciseSvcClientService,
      final InternetAccessCodeSvcClientService internetAccessCodeSvcClientService,
      final CaseIACService caseIacAuditService,
      final CaseNotificationPublisher notificationPublisher,
      final StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager) {
    this.caseRepo = caseRepo;
    this.caseEventRepo = caseEventRepo;
    this.caseGroupRepo = caseGroupRepo;
    this.caseIacAuditRepo = caseIacAuditRepo;
    this.categoryRepo = categoryRepo;
    this.actionSvcClientService = actionSvcClientService;
    this.caseGroupService = caseGroupService;
    this.collectionExerciseSvcClientService = collectionExerciseSvcClientService;
    this.internetAccessCodeSvcClientService = internetAccessCodeSvcClientService;
    this.caseIacAuditService = caseIacAuditService;
    this.notificationPublisher = notificationPublisher;
    this.caseSvcStateTransitionManager = caseSvcStateTransitionManager;
  }

  @Override
  public Case findCaseById(final UUID id) {
    log.debug("Entering findCaseById");

    Case caze = caseRepo.findById(id);
    String iac = caseIacAuditService.findCaseIacByCasePK(caze.getCasePK());

    caze.setIac(iac);

    return caze;
  }

  @Override
  public Case findCaseByIac(final String iac) throws CTPException {
    log.debug("Entering findCaseByIac");

    CaseIacAudit caseIacAudit = caseIacAuditService.findCaseByIac(iac);

    Case caze = caseRepo.findByCasePK(caseIacAudit.getCaseFK());

    return caze;
  }

  @Override
  public List<Case> findCasesByCaseGroupFK(final Integer caseGroupFK) {
    log.debug("Entering findCasesByCaseGroupFK");
    return caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(caseGroupFK);
  }

  @Override
  public List<Case> findCasesByPartyId(final UUID partyId) {
    log.debug("Entering findCasesByPartyId");

    List<Case> cazes = caseRepo.findByPartyId(partyId);

    cazes.stream().forEach(c -> c.setIac(caseIacAuditService.findCaseIacByCasePK(c.getCasePK())));

    return cazes;
  }

  @Override
  public List<CaseEvent> findCaseEventsByCaseFK(final Integer caseFK) {
    log.debug("Entering findCaseEventsByCaseFK");
    return caseEventRepo.findByCaseFKOrderByCreatedDateTimeDesc(caseFK);
  }

  @Override
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

  @Override
  public CaseEvent createCaseEvent(final CaseEvent caseEvent, final Case newCase)
      throws CTPException {
    return createCaseEvent(caseEvent, newCase, DateTimeUtil.nowUTC());
  }

  @Override
  public CaseEvent createCaseEvent(
      final CaseEvent caseEvent, final Case newCase, final Case targetCase) throws CTPException {
    return createCaseEvent(caseEvent, newCase, DateTimeUtil.nowUTC(), targetCase);
  }

  @Override
  public CaseEvent createCaseEvent(
      final CaseEvent caseEvent, final Case newCase, final Timestamp timestamp)
      throws CTPException {
    log.with("case_event", caseEvent).debug("Creating case event");

    Case targetCase = caseRepo.findOne(caseEvent.getCaseFK());
    log.with("target_case", targetCase).debug("Found target case");
    if (targetCase == null) {
      return null;
    }
    return createCaseEvent(caseEvent, newCase, timestamp, targetCase);
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  @Override
  public CaseEvent createCaseEvent(
      final CaseEvent caseEvent,
      final Case newCase,
      final Timestamp timestamp,
      final Case targetCase)
      throws CTPException {
    log.with("case_event", caseEvent).debug("Creating case event");

    Category category = categoryRepo.findOne(caseEvent.getCategory());
    validateCaseEventRequest(category, targetCase, newCase);

    // save the case event to db
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    CaseEvent createdCaseEvent = caseEventRepo.save(caseEvent);
    log.with("created_case_event", createdCaseEvent).debug("Saved case event");

    // do we need to record a response?
    recordCaseResponse(category, targetCase, timestamp);

    transitionCaseGroupStatus(targetCase, caseEvent);

    switch (caseEvent.getCategory()) {
      case RESPONDENT_ENROLED:
        List<CaseGroup> caseGroups =
            caseGroupService.findCaseGroupsForExecutedCollectionExercises(targetCase);
        processCaseCreationAndTransitionsDuringEnrolment(category, caseGroups, caseEvent, newCase);
        break;
      case SUCCESSFUL_RESPONSE_UPLOAD:
      case COMPLETED_BY_PHONE:
        createNewCase(category, caseEvent, targetCase, newCase);
        updateAllAssociatedBiCases(targetCase, category);
        break;
      case GENERATE_ENROLMENT_CODE:
      case NO_ACTIVE_ENROLMENTS:
        generateAndStoreNewIAC(targetCase);
        effectTargetCaseStateTransition(category, targetCase);
        break;
      default:
        createNewCase(category, caseEvent, targetCase, newCase);
        effectTargetCaseStateTransition(category, targetCase);
        break;
    }
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
  @Override
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
    if (iac == null || !internetAccessCodeSvcClientService.isIacActive(iac)) {
      log.with("case_id", targetCase.getId()).debug("Replacing existing case IAC");
      String newIac = internetAccessCodeSvcClientService.generateIACs(1).get(0);
      targetCase.setIac(newIac);
      saveCaseIacAudit(targetCase);
    } else {
      log.with("case_id", targetCase.getId()).debug("Existing IAC is still active");
    }
  }

  /**
   * If BI case is transitioned to Inactionable transition all other BI Cases associated with the B
   * case, they should all be inactionable after a successful upload & not receive any reminder
   * communications.
   *
   * @param targetCase
   * @param category
   * @throws CTPException
   */
  private void updateAllAssociatedBiCases(final Case targetCase, final Category category)
      throws CTPException {
    UUID caseGroupId = targetCase.getCaseGroupId();
    List<Case> associatedBiCases = caseRepo.findByCaseGroupId(caseGroupId);

    for (Case caze : associatedBiCases) {
      effectTargetCaseStateTransition(category, caze);
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

  /**
   * This has been triggered by a 'RESPONDENT_ENROLED' event. If we find any associated Case Groups
   * with only B cases we need to make case 'INACTIONABLE' and create BI case.
   *
   * @param category a category - currently only called for RESPONDENT_ENROLED
   * @param caseGroups a list of case groups
   * @param caseEvent a case event
   * @param newCase a case to provide a party id
   * @throws CTPException thrown if database error etc
   */
  private void processCaseCreationAndTransitionsDuringEnrolment(
      final Category category,
      final List<CaseGroup> caseGroups,
      final CaseEvent caseEvent,
      final Case newCase)
      throws CTPException {

    for (CaseGroup caseGroup : caseGroups) {

      // fetch all B and BI cases associated to the case group being processed
      List<Case> cases =
          caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(caseGroup.getCaseGroupPK());

      // Create a new BI case for the respondent enrolling (if one doesn't already exit)
      // This is primarily to guard against multiple RESPONDENT_ENROLED case events for the same
      // user
      // caused by a double click scenario in the verification email journey
      List<Case> biCases =
          cases
              .stream()
              .filter(
                  c ->
                      c.getSampleUnitType().toString().equals("BI")
                          && c.getPartyId().equals(newCase.getPartyId()))
              .collect(Collectors.toList());
      if (biCases.size() != 0) {
        log.with("party_id", newCase.getPartyId().toString())
            .with("case_group_id", caseGroup.getId())
            .with("case_id", biCases.get(0).getId())
            .warn("Existing BI case found during enrolment");
      } else {
        Case c = new Case();
        c.setPartyId(newCase.getPartyId());
        createNewCase(category, caseEvent, cases.get(0), c);
        log.with("party_id", newCase.getPartyId().toString())
            .with("case_group_id", caseGroup.getId())
            .debug("BI case created during enrolment");
      }

      // Transition each of the B cases for the casegroup being enrolled for
      List<Case> caseTypeBs =
          cases
              .stream()
              .filter(c -> c.getSampleUnitType().toString().equals("B"))
              .collect(Collectors.toList());
      for (Case caseTypeB : caseTypeBs) {
        effectTargetCaseStateTransition(category, caseTypeB);
      }
    }
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  @Override
  public void createInitialCase(SampleUnitParent sampleUnitParent) {
    CaseGroup newCaseGroup = createNewCaseGroup(sampleUnitParent);
    log.with("case_group_id", newCaseGroup.getId()).debug("Created new casegroup");

    Category category = new Category();
    category.setShortDescription("Initial creation of case");

    Case parentCase = createNewCase(sampleUnitParent, newCaseGroup);
    if (sampleUnitParent.getSampleUnitChildren() != null) {
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
   * @param newCase the details provided in the event request for the new case
   * @throws CTPException if the CaseEventRequest is invalid
   */
  private void validateCaseEventRequest(Category category, Case oldCase, Case newCase)
      throws CTPException {
    String oldCaseSampleUnitType = oldCase.getSampleUnitType().name();
    String expectedOldCaseSampleUnitTypes = category.getOldCaseSampleUnitTypes();
    if (!compareOldCaseSampleUnitType(oldCaseSampleUnitType, expectedOldCaseSampleUnitTypes)) {
      String errorMsg =
          String.format(
              WRONG_OLD_SAMPLE_UNIT_TYPE_MSG,
              oldCaseSampleUnitType,
              expectedOldCaseSampleUnitTypes);
      log.with("old_case_sample_unit_type", oldCaseSampleUnitType)
          .with("expected_old_case_sample_unit_types", expectedOldCaseSampleUnitTypes)
          .error(errorMsg);
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED, errorMsg);
    }

    if (category.getNewCaseSampleUnitType() != null && newCase == null) {
      String errorMsg = String.format(MISSING_NEW_CASE_MSG, oldCase.getId());
      log.with("old_case_id", oldCase.getId()).error(errorMsg);
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED, errorMsg);
    }

    CaseDTO.CaseEvent transitionEvent = category.getEventType();
    if (transitionEvent != null) {
      try {
        caseSvcStateTransitionManager.transition(oldCase.getState(), transitionEvent);
      } catch (CTPException e) {
        throw new CTPException(CTPException.Fault.VALIDATION_FAILED, e.getMessage());
      }
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
   * Check to see if a new case creation is indicated by the event category and if so create it
   *
   * @param category the category details of the event
   * @param caseEvent the basic event
   * @param targetCase the 'source' case the event is being created for
   * @param newCase the details for the new case (if indeed one is required) else null
   */
  private void createNewCase(
      Category category, CaseEvent caseEvent, Case targetCase, Case newCase) {
    if (category.getNewCaseSampleUnitType() != null) {
      // add sampleUnitType and actionplanId to newCase
      buildNewCase(category, newCase, targetCase);

      Boolean calculationRequired = category.getRecalcCollectionInstrument();
      // TODO if calculationRequired true = we need to call the Collection
      // Exercise (will only happen for CENSUS)
      if (calculationRequired == null || !calculationRequired) {
        newCase.setCollectionInstrumentId(targetCase.getCollectionInstrumentId());
      }

      createNewCaseFromEvent(caseEvent, targetCase, newCase, category);
    }
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
   * Add required values to the new case to be created
   *
   * @param category the category details of the event
   * @param targetCase the 'source' case the event is being created for
   * @param newCase the new case to be created
   */
  private void buildNewCase(Category category, Case newCase, Case targetCase) {
    newCase.setSampleUnitType(SampleUnitType.valueOf(category.getNewCaseSampleUnitType()));

    // set case group id to the same as
    newCase.setCaseGroupId(targetCase.getCaseGroupId());

    CaseGroup caseGroup = caseGroupRepo.findOne(targetCase.getCaseGroupFK());
    CollectionExerciseDTO collectionExercise =
        collectionExerciseSvcClientService.getCollectionExercise(
            caseGroup.getCollectionExerciseId());

    List<CaseTypeDTO> caseTypes = collectionExercise.getCaseTypes();
    for (CaseTypeDTO caseType : caseTypes) {
      if (caseType.getSampleUnitTypeFK().equals(newCase.getSampleUnitType().name())) {
        newCase.setActionPlanId(caseType.getActionPlanId());
      }
    }
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
    switch (category.getCategoryName()) {
      case OFFLINE_RESPONSE_PROCESSED:
        channel = InboundChannel.OFFLINE;
        break;
      case ONLINE_QUESTIONNAIRE_RESPONSE:
        channel = InboundChannel.ONLINE;
        break;
      case PAPER_QUESTIONNAIRE_RESPONSE:
        channel = InboundChannel.PAPER;
        break;
      default:
        break;
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
          || transitionEvent == CaseDTO.CaseEvent.ACCOUNT_CREATED) {
        if (targetCase.getIac() != null) {
          internetAccessCodeSvcClientService.disableIAC(targetCase.getIac());
        }
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
   * Go ahead and create a new case using the new case details, associate it with the target case
   * and create the CASE_CREATED event on the new case
   *
   * @param caseEvent the basic event
   * @param targetCase the 'source' case the event is being created for
   * @param newCase the details for the new case (if indeed one is required) else null
   * @param caseEventCategory the caseEventCategory
   * @return the new case
   */
  private Case createNewCaseFromEvent(
      CaseEvent caseEvent, Case targetCase, Case newCase, Category caseEventCategory) {
    Case persistedCase = saveNewCase(caseEvent, targetCase, newCase);
    // NOTE the action service does not need to be notified of the creation of
    // the new case - yet
    // That will be done when the CaseDistributor wakes up and assigns an IAC to
    // the newly created case
    // ie it might be created here, but it is not yet ready for prime time
    // without its IAC!
    createCaseCreatedEvent(persistedCase, caseEventCategory);
    return persistedCase;
  }

  /**
   * Create a new case row for a replacement/new case
   *
   * @param caseEvent the event that lead to the creation of the new case
   * @param targetCase the case the caseEvent was applied to
   * @param newCase the case we have been asked to create off the back of the event
   * @return the persisted case
   */
  private Case saveNewCase(CaseEvent caseEvent, Case targetCase, Case newCase) {
    newCase.setId(UUID.randomUUID());
    newCase.setState(CaseState.REPLACEMENT_INIT);
    newCase.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCase.setCaseGroupFK(targetCase.getCaseGroupFK());
    newCase.setCreatedBy(caseEvent.getCreatedBy());
    newCase.setSampleUnitId(targetCase.getSampleUnitId());
    if (newCase.getSampleUnitType() == SampleUnitType.B) {
      newCase.setSourceCaseId(null);
    } else {
      newCase.setSourceCaseId(targetCase.getCasePK());
    }

    return caseRepo.saveAndFlush(newCase);
  }

  /**
   * Create an event for a newly created case
   *
   * @param caze the case for which we want to record the event
   * @param caseEventCategory the category of the event that led to the creation of the case
   * @return the created event
   */
  private CaseEvent createCaseCreatedEvent(Case caze, Category caseEventCategory) {
    CaseEvent newCaseCaseEvent = new CaseEvent();
    newCaseCaseEvent.setCaseFK(caze.getCasePK());
    newCaseCaseEvent.setCategory(CategoryDTO.CategoryName.CASE_CREATED);
    newCaseCaseEvent.setCreatedBy(Constants.SYSTEM);
    newCaseCaseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCaseCaseEvent.setDescription(
        String.format(CASE_CREATED_EVENT_DESCRIPTION, caseEventCategory.getShortDescription()));

    caseEventRepo.saveAndFlush(newCaseCaseEvent);
    return newCaseCaseEvent;
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

    caseGroupRepo.saveAndFlush(newCaseGroup);
    log.with("case_group_id", newCaseGroup.getId().toString()).debug("New CaseGroup created");
    return newCaseGroup;
  }

  @Override
  public Case findCaseBySampleUnitId(UUID sampleUnitId) {
    return caseRepo.findBySampleUnitId(sampleUnitId);
  }
}
