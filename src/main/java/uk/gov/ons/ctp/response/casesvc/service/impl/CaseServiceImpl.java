package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
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
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CollectionExerciseSvcClientService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;

/**
 * A CaseService implementation which encapsulates all business logic operating on the Case entity
 * model.
 */
@Service
@Slf4j
public class CaseServiceImpl implements CaseService {

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
    this.notificationPublisher = notificationPublisher;
    this.caseSvcStateTransitionManager = caseSvcStateTransitionManager;
  }

  @Override
  public Case findCaseById(final UUID id) {
    log.debug("Entering findCaseById");
    return caseRepo.findById(id);
  }

  @Override
  public Case findCaseByIac(final String iac) throws CTPException {
    log.debug("Entering findCaseByIac");

    List<Case> cases = caseRepo.findByIac(iac);

    Case caze = null;
    if (!CollectionUtils.isEmpty(cases)) {
      if (cases.size() != 1) {
        throw new CTPException(
            CTPException.Fault.SYSTEM_ERROR, String.format(IAC_OVERUSE_MSG, iac));
      }
      caze = cases.get(0);
    }

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
    return caseRepo.findByPartyId(partyId);
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
    log.info(
        "Creating case event, casePK={}, category={}, subCategory={}, createdBy={}",
        caseEvent.getCaseFK(),
        caseEvent.getCategory(),
        caseEvent.getSubCategory(),
        caseEvent.getCreatedBy());

    Case targetCase = caseRepo.findOne(caseEvent.getCaseFK());
    log.debug("targetCase is {}", targetCase);
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
    log.info(
        "Creating case event, casePK={}, category={}, subCategory={}, createdBy={}",
        caseEvent.getCaseFK(),
        caseEvent.getCategory(),
        caseEvent.getSubCategory(),
        caseEvent.getCreatedBy());

    Category category = categoryRepo.findOne(caseEvent.getCategory());
    validateCaseEventRequest(category, targetCase, newCase);

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
        replaceIAC(targetCase);
        break;
      case NO_ACTIVE_ENROLMENTS:
        replaceIAC(targetCase);
        processActionPlanChange(targetCase, false);
        break;
      default:
        // Do nothing
        break;
    }

    effectTargetCaseStateTransition(category, targetCase);
    log.info(
        "Successfully created case event, casePK={}, category={}, subCategory={}, createdBy={}",
        caseEvent.getCaseFK(),
        caseEvent.getCategory(),
        caseEvent.getSubCategory(),
        caseEvent.getCreatedBy());
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
    log.debug("Saving case iac audit, caseId: {}", updatedCase.getId());
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
  private void replaceIAC(final Case targetCase) {
    String iac = targetCase.getIac();
    if (iac == null || !internetAccessCodeSvcClientService.isIacActive(iac)) {
      log.debug("Replacing existing case IAC, caseId: {}", targetCase.getId());
      String newIac = internetAccessCodeSvcClientService.generateIACs(1).get(0);
      targetCase.setIac(newIac);
      caseRepo.saveAndFlush(targetCase);
      saveCaseIacAudit(targetCase);
    } else {
      log.debug("Existing IAC is still active, caseId: {}", targetCase.getId());
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

      // fetch all B and BI cases associated to the case group being processed
      List<Case> cases =
          caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(caseGroup.getCaseGroupPK());

      List<ActionPlanDTO> actionPlans =
          actionSvcClientService.getActionPlans(caseGroup.getCollectionExerciseId(), enrolments);

      if (actionPlans == null || actionPlans.size() != 1) {
        log.error(
            "One action plan expected for collectionExerciseId={} with activeEnrolmentStatus={}",
            caseGroup.getCollectionExerciseId(),
            enrolments);
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

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  @Override
  public void createInitialCase(SampleUnitParent sampleUnitParent) {
    CaseGroup newCaseGroup = createNewCaseGroup(sampleUnitParent);
    log.info("Created new casegroup, casegroupId: {}", newCaseGroup.getId());

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
        log.info(
            "New Case created, caseId: {}, sampleUnitType: {}",
            childCase.getId().toString(),
            childCase.getSampleUnitType().toString());
      }
    }
    caseRepo.saveAndFlush(parentCase);
    createCaseCreatedEvent(parentCase, category);
    log.info(
        "New Case created, caseId: {}, sampleUnitType: {}",
        parentCase.getId().toString(),
        parentCase.getSampleUnitType().toString());
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

    caseGroupRepo.saveAndFlush(newCaseGroup);
    log.debug("New CaseGroup created: {}", newCaseGroup.getId().toString());
    return newCaseGroup;
  }

  @Override
  public Case findCaseBySampleUnitId(UUID sampleUnitId) {
    return caseRepo.findBySampleUnitId(sampleUnitId);
  }
}
