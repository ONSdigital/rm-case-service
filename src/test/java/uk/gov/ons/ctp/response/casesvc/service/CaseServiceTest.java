package uk.gov.ons.ctp.response.casesvc.service;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.EQ_LAUNCH;
import static uk.gov.ons.ctp.response.casesvc.service.CaseService.WRONG_OLD_SAMPLE_UNIT_TYPE_MSG;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.client.ActionSvcClient;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.client.InternetAccessCodeSvcClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.InternetAccessCodeSvc;
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
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnit;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitChildren;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;

/**
 * Test the CaseServiceImpl primarily the createCaseEvent functionality. Note that these tests
 * require the mocked category data to represent the real Category table data in order to be
 * effective.
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseServiceTest {

  private static final String IAC_SVC_PUT_PATH = "iacs/123";
  private static final String IAC_SVC_POST_PATH = "iacs/123";

  private static final int CAT_ACTION_CANCELLATION_COMPLETED = 1;
  private static final int CAT_ACTION_CANCELLATION_CREATED = 2;
  private static final int CAT_ACTION_COMPLETED = 3;
  private static final int CAT_ACTION_CREATED = 4;
  private static final int CAT_ACTION_UPDATED = 5;
  private static final int CAT_ADDRESS_DETAILS_INCORRECT = 6;
  private static final int CAT_CASE_CREATED = 7;
  private static final int CAT_GENERAL_COMPLAINT = 12;
  private static final int CAT_ONLINE_QUESTIONNAIRE_RESPONSE = 23;
  private static final int CAT_PAPER_QUESTIONNAIRE_RESPONSE = 24;
  private static final int CAT_RESPONDENT_ENROLED = 27;
  private static final int CAT_ACCESS_CODE_AUTHENTICATION_ATTEMPT = 44;
  private static final int CAT_COLLECTION_INSTRUMENT_DOWNLOADED = 45;
  private static final int CAT_UNSUCCESSFUL_RESPONSE_UPLOAD = 46;
  private static final int CAT_SUCCESSFUL_RESPONSE_UPLOAD = 47;
  private static final int CAT_OFFLINE_RESPONSE_PROCESSED = 48;
  private static final int CAT_NO_ACTIVE_ENROLMENTS = 49;
  private static final int CAT_GENERATE_ENROLMENT_CODE = 50;

  /**
   * Note that the Integer values below are linked to the order in which cases appear in the array
   * defined at CaseServiceTest.Case.json = casePK
   */
  private static final Integer NON_EXISTING_PARENT_CASE_FK = 0;

  private static final Integer ACTIONABLE_HOUSEHOLD_CASE_FK = 0;
  private static final Integer INACTIONABLE_HOUSEHOLD_CASE_FK = 1;
  private static final Integer ENROLMENT_CASE_INDIVIDUAL_FK = 8;
  private static final Integer ACTIONABLE_BUSINESS_UNIT_CASE_FK = 9;
  private static final Integer INITIAL_BUSINESS_UNIT_CASE_FK = 10;

  private static final Integer CASEGROUP_PK = 1;

  private static final String CASEEVENT_CREATEDBY = "unit test";
  private static final String CASEEVENT_DESCRIPTION = "a desc";
  private static final String CASEEVENT_SUBCATEGORY = "sub category";
  private static final String IAC_FOR_TEST = "ABCD-EFGH-IJKL";

  @Mock private CaseRepository caseRepo;
  @Mock private CaseEventRepository caseEventRepo;
  @Mock private CaseGroupRepository caseGroupRepo;
  @Mock private CaseIacAuditRepository caseIacAuditRepo;
  @Mock private CategoryRepository categoryRepo;

  @Mock private ActionSvcClient actionSvcClient;
  @Mock private CaseGroupService caseGroupService;
  @Mock private CaseGroupAuditService caseGroupAuditService;
  @Mock private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Mock private InternetAccessCodeSvcClient internetAccessCodeSvcClient;
  @Mock private CaseIACService caseIacAuditService;

  @Mock private AppConfig appConfig;
  @Mock private CaseNotificationPublisher notificationPublisher;
  @Mock private StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;
  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  @InjectMocks private CaseService caseService;

  @Captor ArgumentCaptor<Set<CategoryName>> argumentCaptor;

  private List<Case> cases;
  private List<Category> categories;
  private List<CaseGroup> caseGroups;

  /**
   * All of these tests require the mocked repos to respond with predictable data loaded from test
   * fixture json files.
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    cases = FixtureHelper.loadClassFixtures(Case[].class);
    categories = FixtureHelper.loadClassFixtures(Category[].class);
    caseGroups = FixtureHelper.loadClassFixtures(CaseGroup[].class);
    mockStateTransitions();
    mockupCaseGroupRepo();
    mockAppConfigUse();
    mockupCaseEventRepo();
    mockupCollectionExerciseServiceClient();
  }

  /**
   * To test findCaseByIac when no case is found for given IAC
   *
   * @throws CTPException if findCaseByIac does
   */
  @Test
  public void testFindCaseByIacNoCaseFound() throws CTPException {
    when(caseIacAuditService.findCaseByIac(anyString())).thenReturn(new CaseIacAudit());

    assertNull(caseService.findCaseByIac(IAC_FOR_TEST));
  }

  /**
   * To test findCaseByIac when one case is found for given IAC
   *
   * @throws CTPException if findCaseByIac does
   */
  @Test
  public void testFindCaseByIacOneCaseFound() throws CTPException {
    when(caseIacAuditService.findCaseByIac(anyString())).thenReturn(new CaseIacAudit());
    when(caseRepo.findByCasePK(anyInt())).thenReturn(cases.get(0));

    assertEquals(cases.get(0), caseService.findCaseByIac(IAC_FOR_TEST));
  }

  /**
   * Should not be allowed to create an event against a case that does not exist!
   *
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testCreateCaseEventWithTargetCase() throws CTPException {
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ADDRESS_DETAILS_INCORRECT))
        .thenReturn(categories.get(CAT_ADDRESS_DETAILS_INCORRECT));
    Timestamp currentTime = DateTimeUtil.nowUTC();
    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("partyId", UUID.randomUUID().toString());
    CaseEvent caseEvent =
        new CaseEvent(
            1,
            NON_EXISTING_PARENT_CASE_FK,
            CASEEVENT_DESCRIPTION,
            CASEEVENT_CREATEDBY,
            currentTime,
            CategoryDTO.CategoryName.ADDRESS_DETAILS_INCORRECT,
            CASEEVENT_SUBCATEGORY,
            metadata);
    when(caseEventRepo.save(any(CaseEvent.class))).thenReturn(caseEvent);

    CaseEvent result = caseService.createCaseEvent(caseEvent, null, cases.get(0));

    verify(caseEventRepo, times(1)).save(caseEvent);
    assertEquals(caseEvent, result);
  }

  /**
   * Should not be allowed to create an event against a case that does not exist!
   *
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testCreateCaseEventAgainstNonExistentCase() throws CTPException {
    when(caseRepo.findOne(NON_EXISTING_PARENT_CASE_FK)).thenReturn(null);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ADDRESS_DETAILS_INCORRECT))
        .thenReturn(categories.get(CAT_ADDRESS_DETAILS_INCORRECT));

    Timestamp currentTime = DateTimeUtil.nowUTC();
    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("partyId", UUID.randomUUID().toString());
    CaseEvent caseEvent =
        new CaseEvent(
            1,
            NON_EXISTING_PARENT_CASE_FK,
            CASEEVENT_DESCRIPTION,
            CASEEVENT_CREATEDBY,
            currentTime,
            CategoryDTO.CategoryName.ADDRESS_DETAILS_INCORRECT,
            CASEEVENT_SUBCATEGORY,
            metadata);
    CaseEvent result = caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(NON_EXISTING_PARENT_CASE_FK);
    assertNull(result);
  }

  /**
   * Tries to apply a general event against a case already inactionable. Should allow it.
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testCreateNonActionableEventAgainstInactionableCase() throws Exception {
    when(caseRepo.findOne(INACTIONABLE_HOUSEHOLD_CASE_FK))
        .thenReturn(cases.get(INACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.GENERAL_COMPLAINT))
        .thenReturn(categories.get(CAT_GENERAL_COMPLAINT));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.GENERAL_COMPLAINT, INACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(INACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.GENERAL_COMPLAINT);
    // there was no change to case - no state transition
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, times(0)).disableAllIACsForCase(any(Case.class));
    // event was saved
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, times(0))
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * Tries to apply a response event against an actionable case Should allow it and record response.
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testCreatePaperResponseEventAgainstActionableCase() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE))
        .thenReturn(categories.get(CAT_PAPER_QUESTIONNAIRE_RESPONSE));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE, ACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(1, caseSaved.getResponses().size());
    assertEquals(CaseState.INACTIONABLE, caseSaved.getState());

    // IAC should not be disabled for paper responses
    verify(caseIacAuditService, times(0)).disableAllIACsForCase(any(Case.class));

    // action service should be told of case state change
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));

    // no new action to be created
    verify(actionSvcClient, times(0))
        .postAction(any(String.class), any(UUID.class), any(String.class));

    // event was saved
    verify(caseEventRepo, times(1)).save(caseEvent);
  }

  /**
   * Tries to apply an online response event against an actionable case Should allow it and record
   * response.
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testCreateOnlineResponseEventAgainstActionableCase() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE))
        .thenReturn(categories.get(CAT_ONLINE_QUESTIONNAIRE_RESPONSE));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE, ACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(1, caseSaved.getResponses().size());
    assertEquals(CaseState.INACTIONABLE, caseSaved.getState());

    // action service should be told of case state change
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));

    // no new action to be created
    verify(actionSvcClient, times(0))
        .postAction(any(String.class), any(UUID.class), any(String.class));

    // event was saved
    verify(caseEventRepo, times(1)).save(caseEvent);
  }

  /**
   * Tries to apply a response event against an already inactionable case Should allow it and record
   * response but the state should remain inactionable.
   *
   * @throws Exception exception thrown
   */
  @Test
  public void testCreateResponseEventAgainstInActionableCase() throws Exception {
    when(caseRepo.findOne(INACTIONABLE_HOUSEHOLD_CASE_FK))
        .thenReturn(cases.get(INACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE))
        .thenReturn(categories.get(CAT_PAPER_QUESTIONNAIRE_RESPONSE));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE, INACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(INACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(2, caseSaved.getResponses().size());
    assertEquals(CaseState.INACTIONABLE, caseSaved.getState());

    // IAC should not be disabled again!
    verify(caseIacAuditService, times(0)).disableAllIACsForCase(any(Case.class));

    // action service should NOT be told of case state change
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));

    // no new action to be created
    verify(actionSvcClient, times(0))
        .postAction(any(String.class), any(UUID.class), any(String.class));

    // event was saved
    verify(caseEventRepo, times(1)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category CASE_CREATED on an initial BRES case (the one created for a
   * business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventCaseCreated() throws Exception {
    when(caseRepo.findOne(INITIAL_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(INITIAL_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.CASE_CREATED))
        .thenReturn(categories.get(CAT_CASE_CREATED));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.CASE_CREATED, INITIAL_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(INITIAL_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.CASE_CREATED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category ACTION_CREATED on an ACTIONABLE BRES case (the one created
   * for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCreated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_CREATED))
        .thenReturn(categories.get(CAT_ACTION_CREATED));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.ACTION_CREATED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_CREATED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category ACTION_UPDATED on an ACTIONABLE BRES case (the one created
   * for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionUpdated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_UPDATED))
        .thenReturn(categories.get(CAT_ACTION_UPDATED));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.ACTION_UPDATED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_UPDATED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category ACTION_COMPLETED on an ACTIONABLE BRES case (the one
   * created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCompleted() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_COMPLETED))
        .thenReturn(categories.get(CAT_ACTION_COMPLETED));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.ACTION_COMPLETED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_COMPLETED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  @Test
  public void testCaseGroupStatusIsTransitioned() throws Exception {
    Case targetCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    CaseGroup caseGroup = caseGroups.get(1);

    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(caseGroupRepo.findOne(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK).getCaseGroupFK()))
        .thenReturn(caseGroup);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD))
        .thenReturn(categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD));

    CaseEvent caseEvent1 =
        fabricateEvent(
            CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent1);

    verify(caseGroupService, times(1))
        .transitionCaseGroupStatus(
            caseGroup,
            CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD,
            targetCase.getPartyId());
  }

  @Test
  public void testCaseGroupStatusNotUpdated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(caseGroupRepo.findOne(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK).getCaseGroupFK()))
        .thenReturn(caseGroups.get(1));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.GENERAL_COMPLAINT))
        .thenReturn(categories.get(CAT_GENERAL_COMPLAINT));

    CaseEvent caseEvent1 =
        fabricateEvent(
            CategoryDTO.CategoryName.GENERAL_COMPLAINT, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent1);

    verify(caseGroupRepo, times(0)).saveAndFlush(any(CaseGroup.class));
  }

  /**
   * We create a CaseEvent with category ACTION_CANCELLATION_COMPLETED on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCancellationCompleted() throws Exception {
    Case targetCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(targetCase);
    Category category = categories.get(CAT_ACTION_CANCELLATION_COMPLETED);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED))
        .thenReturn(category);

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(caseGroupService, times(1))
        .transitionCaseGroupStatus(
            caseGroups.get(CASEGROUP_PK - 1),
            CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED,
            targetCase.getPartyId());
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category ACTION_CANCELLATION_COMPLETED on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCancellationCreated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED))
        .thenReturn(categories.get(CAT_ACTION_CANCELLATION_CREATED));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category ACCESS_CODE_AUTHENTICATION_ATTEMPT on an ACTIONABLE BRES
   * case (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventAccessCodeAuthenticationAttempt() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT))
        .thenReturn(categories.get(CAT_ACCESS_CODE_AUTHENTICATION_ATTEMPT));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category RESPONDENT_ENROLED on an ACTIONABLE BRES case (the one
   * created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventRespondentEnrolled() throws Exception {
    // Given
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    Category respondentEnrolledCategory = categories.get(CAT_RESPONDENT_ENROLED);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED))
        .thenReturn(respondentEnrolledCategory);
    when(caseGroupRepo.findOne(CASEGROUP_PK)).thenReturn(caseGroups.get(CASEGROUP_PK));
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroups.get(CASEGROUP_PK));
    when(caseGroupService.findCaseGroupsForExecutedCollectionExercises(any()))
        .thenReturn(caseGroupList);
    List<Case> caseList = Collections.singletonList(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(any())).thenReturn(caseList);
    List<CollectionExerciseDTO> listCollex = Collections.singletonList(makeCollectionExercise());
    when(collectionExerciseSvcClient.getCollectionExercises(null)).thenReturn(listCollex);
    when(caseRepo.saveAndFlush(any(Case.class)))
        .thenReturn(cases.get(ENROLMENT_CASE_INDIVIDUAL_FK));
    ActionPlanDTO actionPlan = new ActionPlanDTO();
    actionPlan.setId(UUID.randomUUID());
    when(actionSvcClient.getActionPlans(any(UUID.class), anyBoolean()))
        .thenReturn(Collections.singletonList(actionPlan));

    // When
    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.RESPONDENT_ENROLED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    caseService.createCaseEvent(caseEvent, cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));

    // Then
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).saveAndFlush(argument.capture());

    verify(caseSvcStateTransitionManager, times(1))
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    Case caze = argument.getValue();
    assertEquals(CaseState.ACTIONABLE, caze.getState());
    assertEquals(actionPlan.getId(), caze.getActionPlanId());

    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));
    // no new action to be created
    verify(actionSvcClient, times(0))
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * Make a test collection exercise
   *
   * @return a new test collection exercise
   */
  private CollectionExerciseDTO makeCollectionExercise() {
    CollectionExerciseDTO collex = new CollectionExerciseDTO();
    collex.setId(UUID.randomUUID());
    collex.setState(CollectionExerciseDTO.CollectionExerciseState.READY_FOR_LIVE);
    return collex;
  }

  /**
   * Make a test case group
   *
   * @return a new test case group
   */
  private CaseGroup makeCaseGroup() {
    CaseGroup cg = new CaseGroup();
    cg.setId(UUID.randomUUID());
    cg.setStatus(CaseGroupStatus.NOTSTARTED);
    cg.setSampleUnitType("B");
    return cg;
  }

  /**
   * Make a test case
   *
   * @return a new test case
   */
  private Case makeCase() {
    Case c = new Case();
    c.setId(UUID.randomUUID());
    c.setSampleUnitType(SampleUnitDTO.SampleUnitType.B);
    c.setState(CaseState.ACTIONABLE);
    c.setActionPlanId(UUID.randomUUID());
    c.setCaseGroupId(UUID.randomUUID());
    c.setCaseGroupFK(ENROLMENT_CASE_INDIVIDUAL_FK);
    return c;
  }

  /**
   * We create a CaseEvent with category RESPONDENT_ENROLED versus a Case of wrong sampleUnitType
   * (ie NOT a B)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventRespondentEnrolledVersusWrongCaseType() throws Exception {
    Case existingCase = cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED))
        .thenReturn(categories.get(CAT_RESPONDENT_ENROLED));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.RESPONDENT_ENROLED, ACTIONABLE_HOUSEHOLD_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent);
      fail();
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "H", "B"), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, times(0))
        .postAction(any(String.class), any(UUID.class), any(String.class));
    verify(caseIacAuditService, times(0)).disableAllIACsForCase(any(Case.class));
    verify(caseEventRepo, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category COLLECTION_INSTRUMENT_DOWNLOADED on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventCollectionInstrumentDownloaded() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED))
        .thenReturn(categories.get(CAT_COLLECTION_INSTRUMENT_DOWNLOADED));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category UNSUCCESSFUL_RESPONSE_UPLOAD on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventUnsuccessfulResponseUploaded() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD))
        .thenReturn(categories.get(CAT_UNSUCCESSFUL_RESPONSE_UPLOAD));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClient, never())
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category OFFLINE_RESPONSE_PROCESSED on an ACTIONABLE BRES case (the
   * one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventOfflineResponseProcessed() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED))
        .thenReturn(categories.get(CAT_OFFLINE_RESPONSE_PROCESSED));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, times(1)).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, times(1)).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, times(1))
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));
    //    verify(actionSvcClient, times(1))
    //        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * A SUCCESSFUL_RESPONSE_UPLOAD event transitions an actionable BI case to INACTIONABLE, and all
   * associated BI cases in the case group. The action service is notified of the transition of all
   * BI Cases to stop them receiving communications.
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventSuccessfulResponseUploaded() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));

    Category successfulResponseUploadedCategory = categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD))
        .thenReturn(successfulResponseUploadedCategory);

    when(caseRepo.findByCaseGroupId(null))
        .thenReturn(Arrays.asList(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);
    verify(caseEventRepo, times(1)).save(caseEvent);
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).saveAndFlush(argument.capture());
    verify(caseSvcStateTransitionManager, times(1))
        .transition(
            any(CaseState.class),
            any(
                CaseDTO.CaseEvent
                    .class)); // action service should be told of the old case state change for both
    // cases

    // Now verifying that case has been moved to INACTIONABLE
    Case oldCase = argument.getAllValues().get(0);
    assertEquals(CaseState.INACTIONABLE, oldCase.getState());

    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));
    // no new action to be created
    verify(actionSvcClient, times(0))
        .postAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * A SUCCESSFUL_RESPONSE_UPLOAD event transitions an actionable BI case to INACTIONABLE, and all
   * associated BI cases in the case group. The action service is notified of the transition of all
   * BI Cases to stop them receiving communications.
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventSuccessfulNoActiveEnrolmentCaseEvent() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    Category noActiveEnrolmentCategory = categories.get(CAT_NO_ACTIVE_ENROLMENTS);
    when(categoryRepo.findOne(CategoryName.NO_ACTIVE_ENROLMENTS))
        .thenReturn(noActiveEnrolmentCategory);
    when(caseRepo.findByCaseGroupId(null))
        .thenReturn(
            Arrays.asList(
                cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK),
                cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    CaseGroup caseGroup = makeCaseGroup();
    when(caseGroupRepo.findById(null)).thenReturn(caseGroup);
    when(internetAccessCodeSvcClient.generateIACs(1))
        .thenReturn(Collections.singletonList(IAC_FOR_TEST));
    when(caseGroupService.findCaseGroupsForExecutedCollectionExercises(any()))
        .thenReturn(Collections.singletonList(caseGroup));
    List<Case> caseList = Collections.singletonList(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(any())).thenReturn(caseList);
    ActionPlanDTO actionPlan = new ActionPlanDTO();
    actionPlan.setId(UUID.randomUUID());
    when(actionSvcClient.getActionPlans(any(UUID.class), anyBoolean()))
        .thenReturn(Collections.singletonList(actionPlan));

    CaseEvent caseEvent =
        fabricateEvent(CategoryName.NO_ACTIVE_ENROLMENTS, ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryName.NO_ACTIVE_ENROLMENTS);
    verify(caseEventRepo, times(1)).save(caseEvent);
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(2)).saveAndFlush(argument.capture());
    verify(caseRepo, times(2)).saveAndFlush(argument.capture());
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));
  }

  /**
   * caseService.createCaseEvent will be called with invalid state transitions but suppress the
   * exception. This is expected behaviour. This code smells but keeping as is.
   */
  @Test
  public void testGivenCaseGroupChangeIsInvalidWhenTransitionFailGracefully() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    Category successfulResponseUploadedCategory = categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD))
        .thenReturn(successfulResponseUploadedCategory);

    doThrow(new CTPException(CTPException.Fault.BAD_REQUEST))
        .when(caseGroupService)
        .transitionCaseGroupStatus(any(), any(), any());
    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseGroupService).transitionCaseGroupStatus(any(), any(), any());
  }

  @Test
  public void testGenerateEnrolmentCodeCaseEvent() throws CTPException {
    Case actionableBCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(actionableBCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.GENERATE_ENROLMENT_CODE))
        .thenReturn(categories.get(CAT_GENERATE_ENROLMENT_CODE));
    when(internetAccessCodeSvcClient.isIacActive(actionableBCase.getIac())).thenReturn(false);
    when(internetAccessCodeSvcClient.generateIACs(1))
        .thenReturn(Collections.singletonList(IAC_FOR_TEST));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.GENERATE_ENROLMENT_CODE, ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    Case updatedBCase = mapperFacade.map(actionableBCase, Case.class);
    updatedBCase.setIac(IAC_FOR_TEST);
    verify(caseIacAuditRepo, times(1)).saveAndFlush(any());
  }

  @Test
  public void testCreateInitialCaseWithSampleUnitChildren() throws Exception {
    SampleUnitParent sampleUnitParent = new SampleUnitParent();
    SampleUnit sampleUnit = new SampleUnit();
    SampleUnitChildren sampleUnitChildren =
        new SampleUnitChildren(new ArrayList<>(Collections.singletonList(sampleUnit)));
    sampleUnit.setActionPlanId(UUID.randomUUID().toString());
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setPartyId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitRef("str1234");
    sampleUnit.setSampleUnitType("BI");
    sampleUnit.setId(UUID.randomUUID().toString());

    sampleUnitParent.setActionPlanId(UUID.randomUUID().toString());
    sampleUnitParent.setCollectionExerciseId(UUID.randomUUID().toString());
    sampleUnitParent.setSampleUnitChildren(sampleUnitChildren);
    sampleUnitParent.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnitParent.setPartyId(UUID.randomUUID().toString());
    sampleUnitParent.setSampleUnitRef("str1234");
    sampleUnitParent.setSampleUnitType("B");
    sampleUnitParent.setId(UUID.randomUUID().toString());

    List<CollectionExerciseDTO> collectionExercises =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    when(collectionExerciseSvcClient.getCollectionExercise(any()))
        .thenReturn(collectionExercises.get(0));

    caseService.createInitialCase(sampleUnitParent);

    ArgumentCaptor<CaseGroup> caseGroup = ArgumentCaptor.forClass(CaseGroup.class);
    verify(caseGroupRepo, times(1)).saveAndFlush(caseGroup.capture());

    List<CaseGroup> capturedCaseGroup = caseGroup.getAllValues();

    verify(caseRepo, times(2)).saveAndFlush(any());
  }

  @Test(expected = IllegalStateException.class)
  public void testRespondentEnrolledCaseEventWithNoActionPlansThrowsException()
      throws CTPException {
    // Given
    given(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .willReturn(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    Category respondentEnrolledCategory = categories.get(CAT_RESPONDENT_ENROLED);
    given(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED))
        .willReturn(respondentEnrolledCategory);
    given(caseGroupRepo.findOne(CASEGROUP_PK)).willReturn(caseGroups.get(CASEGROUP_PK));
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroups.get(CASEGROUP_PK));
    given(caseGroupService.findCaseGroupsForExecutedCollectionExercises(any()))
        .willReturn(caseGroupList);
    List<Case> caseList = Collections.singletonList(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    given(caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(any())).willReturn(caseList);
    List<CollectionExerciseDTO> listCollex = Collections.singletonList(makeCollectionExercise());
    given(collectionExerciseSvcClient.getCollectionExercises(null)).willReturn(listCollex);
    given(caseRepo.saveAndFlush(any(Case.class)))
        .willReturn(cases.get(ENROLMENT_CASE_INDIVIDUAL_FK));
    ActionPlanDTO actionPlan = new ActionPlanDTO();
    actionPlan.setId(UUID.randomUUID());
    given(actionSvcClient.getActionPlans(any(UUID.class), anyBoolean())).willReturn(null);

    // When
    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.RESPONDENT_ENROLED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    caseService.createCaseEvent(caseEvent, cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));

    // Then IllegalStateException is thrown

  }

  @Test
  public void testFindByCaseFKAndCategoryCanMapStringsToEnumValues() throws CTPException {
    // Given

    // When
    caseService.findCaseEventsByCaseFKAndCategory(123, Collections.singletonList("EQ_LAUNCH"));

    // Then
    verify(caseEventRepo)
        .findByCaseFKAndCategoryInOrderByCreatedDateTimeDesc(eq(123), argumentCaptor.capture());
    assertTrue(argumentCaptor.getValue().contains(EQ_LAUNCH));
    assertThat(argumentCaptor.getValue().size()).isEqualTo(1);
  }

  @Test(expected = CTPException.class)
  public void testFindByCaseFKAndCategoryBlowsUpWhenStringDoesNotMapToEnumValue()
      throws CTPException {
    // Given

    // When
    caseService.findCaseEventsByCaseFKAndCategory(123, Collections.singletonList("BANANA"));

    // Then
  }

  /**
   * To mock the behaviour of caseGroupRepo
   *
   * @throws Exception if loadClassFixtures does
   */
  private void mockupCaseGroupRepo() throws Exception {
    List<CaseGroup> caseGroups = FixtureHelper.loadClassFixtures(CaseGroup[].class);
    when(caseGroupRepo.findOne(CASEGROUP_PK)).thenReturn(caseGroups.get(CASEGROUP_PK - 1));
  }

  /**
   * mock loading data
   *
   * @param categoryName which category name to load
   * @param casePK the associated existing/old Case
   * @return a mock case event
   */
  private CaseEvent fabricateEvent(CategoryDTO.CategoryName categoryName, int casePK) {
    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseFK(casePK);
    caseEvent.setCategory(categoryName);
    caseEvent.setCreatedBy(CASEEVENT_CREATEDBY);
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    caseEvent.setDescription(CASEEVENT_DESCRIPTION);
    caseEvent.setSubCategory(CASEEVENT_SUBCATEGORY);
    return caseEvent;
  }

  /** mock loading data */
  private void mockupCaseEventRepo() {
    when(caseEventRepo.save(any(CaseEvent.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  /**
   * mock the collection exercise service
   *
   * @throws Exception if fixtures loading fails
   */
  private void mockupCollectionExerciseServiceClient() throws Exception {
    List<CollectionExerciseDTO> collectionExerciseDTOs =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    when(collectionExerciseSvcClient.getCollectionExercise(any()))
        .thenAnswer(invocation -> collectionExerciseDTOs.get(0));
  }

  /**
   * mock state transitions
   *
   * @throws CTPException if case state transition errors
   */
  private void mockStateTransitions() throws CTPException {
    when(caseSvcStateTransitionManager.transition(CaseState.ACTIONABLE, CaseDTO.CaseEvent.DISABLED))
        .thenReturn(CaseState.INACTIONABLE);
    when(caseSvcStateTransitionManager.transition(
            CaseState.ACTIONABLE, CaseDTO.CaseEvent.DEACTIVATED))
        .thenReturn(CaseState.INACTIONABLE);
    when(caseSvcStateTransitionManager.transition(
            CaseState.ACTIONABLE, CaseDTO.CaseEvent.ACTIONPLAN_CHANGED))
        .thenReturn(CaseState.ACTIONABLE);
    when(caseSvcStateTransitionManager.transition(
            CaseState.INACTIONABLE, CaseDTO.CaseEvent.DISABLED))
        .thenReturn(CaseState.INACTIONABLE);
    when(caseSvcStateTransitionManager.transition(
            CaseState.INACTIONABLE, CaseDTO.CaseEvent.DEACTIVATED))
        .thenReturn(CaseState.INACTIONABLE);
  }

  /** mock loading data */
  private void mockAppConfigUse() {
    InternetAccessCodeSvc iacSvc = new InternetAccessCodeSvc();
    iacSvc.setIacPutPath(IAC_SVC_PUT_PATH);
    iacSvc.setIacPostPath(IAC_SVC_POST_PATH);
    when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvc);
  }
}
