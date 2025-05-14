package uk.gov.ons.ctp.response.casesvc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.client.InternetAccessCodeSvcClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseIacAuditRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnit;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.representation.*;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO.SampleUnitType;

import static net.logstash.logback.argument.StructuredArguments.kv;

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

  private final AppConfig appConfig;

  private CaseRepository caseRepo;
  private CaseEventRepository caseEventRepo;
  private CaseGroupRepository caseGroupRepo;
  private CaseIacAuditRepository caseIacAuditRepo;
  private CategoryRepository categoryRepo;
  private CollectionExerciseSvcClient collectionExerciseSvcClient;
  private CaseGroupService caseGroupService;
  private InternetAccessCodeSvcClient internetAccessCodeSvcClient;
  private CaseIACService caseIacAuditService;
  private StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  /** Constructor for CaseService */
  public CaseService(
      final AppConfig appConfig,
      final CaseRepository caseRepo,
      final CaseEventRepository caseEventRepo,
      final CaseGroupRepository caseGroupRepo,
      final CaseIacAuditRepository caseIacAuditRepo,
      final CategoryRepository categoryRepo,
      final CollectionExerciseSvcClient collectionExerciseSvcClient,
      final CaseGroupService caseGroupService,
      final InternetAccessCodeSvcClient internetAccessCodeSvcClient,
      final CaseIACService caseIacAuditService,
      final StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager) {
    this.appConfig = appConfig;
    this.caseRepo = caseRepo;
    this.caseEventRepo = caseEventRepo;
    this.caseGroupRepo = caseGroupRepo;
    this.caseIacAuditRepo = caseIacAuditRepo;
    this.categoryRepo = categoryRepo;
    this.collectionExerciseSvcClient = collectionExerciseSvcClient;
    this.caseGroupService = caseGroupService;
    this.internetAccessCodeSvcClient = internetAccessCodeSvcClient;
    this.caseIacAuditService = caseIacAuditService;
    this.caseSvcStateTransitionManager = caseSvcStateTransitionManager;
  }

  /**
   * Find Case entity by UUID.
   *
   * @param id Unique Case UUID
   * @return Case object or null
   */
  public Case findCaseById(final UUID id) {
    Case caze = caseRepo.findById(id).orElse(null);

    if (caze != null) {
      String iac = caseIacAuditService.findCaseIacByCasePK(caze.getCasePK());
      caze.setIac(iac);
    } else {
      log.warn("Could not find case",
          kv("case_id", id.toString()));
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
  public Case findCaseByIac(final String iac)
      throws uk.gov.ons.ctp.response.lib.common.error.CTPException {
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
    return caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(caseGroupFK);
  }

  /**
   * Find the cases for a partyId.
   *
   * @param partyId the partyId
   * @return the cases for the partyId
   */
  public List<Case> findCasesByPartyId(final UUID partyId, boolean iac) {
    List<Case> cases = caseRepo.findByPartyId(partyId);

    if (iac) {
      cases.stream().forEach(c -> c.setIac(caseIacAuditService.findCaseIacByCasePK(c.getCasePK())));
    }

    return cases;
  }

  /**
   * Find the paginated cases for a partyId ordered by create date descending.
   *
   * @param partyId the partyId
   * @param maxCasesPerSurvey the maximum number of cases per survey to return
   * @return the cases for the partyId
   */
  public List<Case> findCasesByPartyIdLimitedPerSurvey(
      final UUID partyId, boolean iac, int maxCasesPerSurvey) {

    List<Case> cases =
        limitCasesPerSurvey(
            caseRepo.findByPartyIdOrderByCreatedDateTimeDesc(partyId), maxCasesPerSurvey);

    if (iac) {
      cases.stream().forEach(c -> c.setIac(caseIacAuditService.findCaseIacByCasePK(c.getCasePK())));
    }

    return cases;
  }

  /**
   * Return only the first n instances of case per survey The query to do this in the JPA is too
   * complex and uses SQL constructs that JPQL does not support like Row Number and Over. Hence in
   * code
   *
   * @param unrefinedCaseList, the list of cases to be potentially limited
   * @param maxCasesPerSurvey the maximum number of cases per survey to return
   * @return the cases for the partyId
   */
  private List<Case> limitCasesPerSurvey(List<Case> unrefinedCaseList, int maxCasesPerSurvey) {

    List<Case> limitedResults = new ArrayList();
    HashMap<UUID, Integer> surveyCounts = new HashMap<>();
    HashMap<UUID, CaseGroup> caseGroups = new HashMap<>();
    CaseGroup currentGroup = new CaseGroup();
    UUID caseGroupId;
    UUID currentSurveyId;
    int currentCount;

    for (Case current : unrefinedCaseList) {

      // Get CaseGroup, only get new ones from DB
      caseGroupId = current.getCaseGroupId();
      if (!caseGroups.containsKey(caseGroupId)) {
        caseGroups.put(caseGroupId, caseGroupRepo.findById(current.getCaseGroupId()));
      }

      currentGroup = caseGroups.get(caseGroupId);

      currentSurveyId = currentGroup.getSurveyId();

      if (!surveyCounts.containsKey(currentSurveyId)) {
        surveyCounts.put(currentSurveyId, 0);
      }

      currentCount = surveyCounts.get(currentSurveyId);

      if (currentCount < maxCasesPerSurvey) {
        surveyCounts.replace(currentSurveyId, ++currentCount);
        limitedResults.add(current);
      }
    }
    return limitedResults;
  }

  /**
   * Find CaseEvent entities associated with a Case.
   *
   * @param caseFK Case ForeignKey.
   * @return List of CaseEvent entities or empty List.
   */
  public List<CaseEvent> findCaseEventsByCaseFK(final Integer caseFK) {
    return caseEventRepo.findByCaseFKOrderByCreatedDateTimeDesc(caseFK);
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
  public CaseEvent createCaseEvent(final CaseEvent caseEvent)
      throws uk.gov.ons.ctp.response.lib.common.error.CTPException {
    return createCaseEvent(caseEvent, DateTimeUtil.nowUTC());
  }

  /**
   * Create a CaseEvent for the specific scenario of an incoming CaseReceipt (sent by the SDX
   * Gateway and containing the responseDateTime of the online response).
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
   * Gateway and containing the responseDateTime of the online response).
   *
   * @param caseEvent CaseEvent to be created
   * @param timestamp timestamp equals to the incoming CaseReceipt's responseDateTime.
   * @return the created CaseEvent.
   * @throws CTPException when case state transition error
   */
  public CaseEvent createCaseEvent(final CaseEvent caseEvent, final Timestamp timestamp)
      throws CTPException {
    log.info("Creating case event",
        kv("case_event", caseEvent));

    Case targetCase = caseRepo.findById(caseEvent.getCaseFK()).orElse(null);
    log.debug("Found target case",
        kv("target_case", targetCase));
    if (targetCase == null) {
      return null;
    }
    return createCaseEvent(caseEvent, timestamp, targetCase);
  }

  /**
   * Create a CaseEvent for the specific scenario of an incoming CaseReceipt (sent by the SDX
   * Gateway and containing the responseDateTime of the online response).
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
    log.debug("Creating case event",
        kv("case_event", caseEvent));

    Category category = categoryRepo.findById(caseEvent.getCategory()).orElse(null);
    validateCaseEventRequest(category, targetCase);

    // save the case event to db
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    CaseEvent createdCaseEvent = caseEventRepo.save(caseEvent);
    log.debug("createdCaseEvent is {}", createdCaseEvent);
    transitionCaseGroupStatus(targetCase, caseEvent);

    switch (caseEvent.getCategory()) {
      case RESPONDENT_ENROLED:
        processActiveEnrolmentChange(targetCase, true);
        break;
      case GENERATE_ENROLMENT_CODE:
        generateAndStoreNewIAC(targetCase);
        break;
      case NO_ACTIVE_ENROLMENTS:
        processActiveEnrolmentChange(targetCase, false);
        break;
      default:
        // Do nothing
        break;
    }

    effectTargetCaseStateTransition(category, targetCase);
    log.debug("Successfully created case event",
        kv("case_event", caseEvent));
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
  protected void saveCaseIacAudit(final Case updatedCase) {
    log.debug("Saving case iac audit",
        kv("case_id", updatedCase.getId()));
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
      log.debug("Replacing existing case IAC",
            kv("case_id", targetCase.getId()));
              
      String newIac = internetAccessCodeSvcClient.generateIACs(1).get(0);
      targetCase.setIac(newIac);
      caseRepo.saveAndFlush(targetCase);
      saveCaseIacAudit(targetCase);
    } else {
      log.debug("Existing IAC is still active",
              kv("case_id", targetCase.getId()));

    }
  }

  private void transitionCaseGroupStatus(final Case targetCase, final CaseEvent caseEvent) {
    CaseGroup caseGroup = caseGroupRepo.findById(targetCase.getCaseGroupFK()).orElse(null);
    try {
      caseGroupService.transitionCaseGroupStatus(
          caseGroup, caseEvent.getCategory(), targetCase.getPartyId());
    } catch (uk.gov.ons.ctp.response.lib.common.error.CTPException e) {
      // The transition manager throws an exception if the event doesn't cause a transition, however
      // there are lots of
      // events which do not cause CaseGroupStatus transitions, (this is valid behaviour).
      log.debug(e.getMessage());
    }
  }

  private void processActiveEnrolmentChange(final Case targetCase, final boolean enrolments)
      throws uk.gov.ons.ctp.response.lib.common.error.CTPException {

    List<CaseGroup> caseGroups =
        caseGroupService.findCaseGroupsForExecutedCollectionExercises(targetCase);

    for (CaseGroup caseGroup : caseGroups) {
      String sampleUnitRef = caseGroup.getSampleUnitRef();
      CaseGroupStatus status = caseGroup.getStatus();
      String iac = caseIacAuditService.findCaseIacByCasePK(caseGroup.getCaseGroupPK());
      if (caseGroup.getStatus() == CaseGroupStatus.NOTSTARTED
          || caseGroup.getStatus() == CaseGroupStatus.INPROGRESS) {

        // fetch all B and BI cases associated to the case group being processed
        List<Case> cases =
            caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(caseGroup.getCaseGroupPK());
        for (Case caze : cases) {
          if (caze.getSampleUnitType() == SampleUnitType.B) {
            caze.setActiveEnrolment(enrolments);
            caseRepo.saveAndFlush(caze);
          }
        }
      }
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void createInitialCase(SampleUnitParent sampleUnitParent) throws CTPException {
    try {
      CaseGroup newCaseGroup = createNewCaseGroup(sampleUnitParent);
      log.debug("Created new casegroup",
              kv("caseGroupId", newCaseGroup.getId()),
              kv("collectionExericseId", sampleUnitParent.getCollectionExerciseId()),
              kv("sampleUnitRef", sampleUnitParent.getSampleUnitRef()));
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
          log.debug("New child case created",
              kv("caseId", childCase.getId().toString()),
              kv("sampleUnitType", childCase.getSampleUnitType().toString()),
              kv("sampleUnitRef", sampleUnitChild.getSampleUnitRef()),
              kv("collectionExericseId", sampleUnitParent.getCollectionExerciseId()));
          updateCaseWithIACs(childCase, sampleUnitParent.getSampleUnitRef());
          processCase(childCase);
        }
      }
      caseRepo.saveAndFlush(parentCase);
      createCaseCreatedEvent(parentCase, category);
      log.info("New Case created",
          kv("caseId", parentCase.getId().toString()),
          kv("sampleUnitTupe", parentCase.getSampleUnitType().toString()),
          kv("sampleUnitRef", sampleUnitParent.getSampleUnitRef()),
          kv("collectionExericseId", sampleUnitParent.getCollectionExerciseId()));
      updateCaseWithIACs(parentCase, sampleUnitParent.getSampleUnitRef());
      processCase(parentCase);
    } catch (DataIntegrityViolationException exception) {
      log.warn("Case already exists. Ignoring case creation",
          kv("collectionExericseId", sampleUnitParent.getCollectionExerciseId()),
          kv("sampleUnitRef", sampleUnitParent.getSampleUnitRef()),
          kv("error", exception));
      throw new CTPException(CTPException.Fault.DUPLICATE_RECORD, exception.getMessage());
    }
  }

  @Async
  protected void processCase(final Case caze) throws CTPException {
    log.debug("Processing case", kv("case_id", caze.getId()));
    CaseDTO.CaseEvent event = null;
    CaseState initialState = caze.getState();
    switch (caze.getState()) {
      case SAMPLED_INIT:
        event = CaseDTO.CaseEvent.ACTIVATED;
        break;
      case REPLACEMENT_INIT:
        event = CaseDTO.CaseEvent.REPLACED;
        break;
      default:
        log.error("Unexpected state found", kv("initial_state", initialState));
    }

    Case updatedCase = transitionCase(caze, event);
    caseRepo.saveAndFlush(updatedCase);
  }

  private Case transitionCase(final Case caze, final CaseDTO.CaseEvent event) throws CTPException {
    CaseState nextState = caseSvcStateTransitionManager.transition(caze.getState(), event);
    caze.setState(nextState);
    return caze;
  }

  /**
   * This method updates the created case with IAC code
   *
   * @param createdCase Case Object
   */
  private void updateCaseWithIACs(Case createdCase, String sampleUnitRef) {
    try {
      log.info("About to call IAC service to generate an IAC code.",
          kv("caseId", createdCase.getId().toString()),
          kv("sampleUnitRef", sampleUnitRef));
      updateCaseWithIACs(createdCase);
      log.info("IAC received and saved",
          kv("caseId", createdCase.getId().toString()),
          kv("sampleUnitRef", sampleUnitRef));
    } catch (Exception e) {
      log.error("Failed to obtain IAC codes",
          kv("exception", e),
          kv("caseId", createdCase.getId().toString()),
          kv("sampleUnitRef", sampleUnitRef));
    }
  }

  public void updateCaseWithIACs(Case createdCase) {
    log.info("Calling IAC service to generate an IAC code.",
        kv("caseId", createdCase.getId().toString()));
    String iac = internetAccessCodeSvcClient.generateIACs(1).get(0);
    createdCase.setIac(iac);
    Case updatedCase = caseRepo.saveAndFlush(createdCase);
    updatedCase.setIac(iac);
    saveCaseIacAudit(updatedCase);
    log.info("Case updated with IAC code.",
        kv("caseId", createdCase.getId().toString()));
  }

  /**
   * Upfront fail fast validation - if this event is going to require a new case to be created,
   * let's check the request is valid before we do something we cannot rollback ie IAC disable, or
   * Action creation. This logs the error for info purposes and intentionally doesn't throw an
   * exception.
   *
   * @param category the category details
   * @param oldCase the case the event is being created against
   */
  private void validateCaseEventRequest(Category category, Case oldCase) {
    String oldCaseSampleUnitType = oldCase.getSampleUnitType().name();
    String expectedOldCaseSampleUnitTypes = category.getOldCaseSampleUnitTypes();
    if (!compareOldCaseSampleUnitType(oldCaseSampleUnitType, expectedOldCaseSampleUnitTypes)) {
      String errorMsg =
          String.format(
              WRONG_OLD_SAMPLE_UNIT_TYPE_MSG,
              oldCaseSampleUnitType,
              expectedOldCaseSampleUnitTypes);
      log.error(errorMsg);
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
    newCase.setActiveEnrolment(caseData.isActiveEnrolment());

    // HardCoded values
    newCase.setState(CaseState.SAMPLED_INIT);
    newCase.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCase.setCreatedBy(Constants.SYSTEM);

    return newCase;
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
    log.info("Creating case event.",
        kv("caseId", caze.getId().toString()));
    CaseEvent newCaseCaseEvent = new CaseEvent();
    newCaseCaseEvent.setCaseFK(caze.getCasePK());
    newCaseCaseEvent.setCategory(CategoryDTO.CategoryName.CASE_CREATED);
    newCaseCaseEvent.setCreatedBy(Constants.SYSTEM);
    newCaseCaseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCaseCaseEvent.setDescription(
        String.format(CASE_CREATED_EVENT_DESCRIPTION, caseEventCategory.getShortDescription()));

    caseEventRepo.saveAndFlush(newCaseCaseEvent);
    log.info("Case event saved.",
        kv("caseId", caze.getId().toString()));
  }

  /**
   * Create the CaseGroup for the Case.
   *
   * @param caseGroupData SampleUnitParent from which to create CaseGroup.
   * @return newcaseGroup created caseGroup.
   */
  private CaseGroup createNewCaseGroup(SampleUnitParent caseGroupData)
      throws DataIntegrityViolationException {
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
    log.debug("New CaseGroup created",
        kv("case_group_id", newCaseGroup.getId().toString()));
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
