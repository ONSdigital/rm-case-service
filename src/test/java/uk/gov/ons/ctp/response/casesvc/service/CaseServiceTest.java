package uk.gov.ons.ctp.response.casesvc.service;

import static junit.framework.TestCase.assertNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.EQ_LAUNCH;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
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
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnit;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitChildren;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.representation.*;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.lib.action.ActionPlanDTO;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;

/**
 * Test the CaseServiceImpl primarily the createCaseEvent functionality. Note that these tests
 * require the mocked category data to represent the real Category table data in order to be
 * effective.
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseServiceTest {

  private static final String IAC_SVC_PUT_PATH = "iacs/123";
  private static final String IAC_SVC_POST_PATH = "iacs/123";

  private static final int CAT_ACTION_CANCELLATION_COMPLETED = 0;
  private static final int CAT_ACTION_CANCELLATION_CREATED = 1;
  private static final int CAT_ACTION_COMPLETED = 2;
  private static final int CAT_ACTION_CREATED = 3;
  private static final int CAT_ACTION_UPDATED = 4;
  private static final int CAT_PHYSICALLY_OR_MENTALLY_UNABLE = 5;
  private static final int CAT_CASE_CREATED = 6;
  private static final int CAT_LACK_OF_COMPUTER_INTERNET_ACCESS = 8;
  private static final int CAT_ONLINE_QUESTIONNAIRE_RESPONSE = 11;
  private static final int CAT_RESPONDENT_ENROLED = 13;
  private static final int CAT_ACCESS_CODE_AUTHENTICATION_ATTEMPT = 14;
  private static final int CAT_COLLECTION_INSTRUMENT_DOWNLOADED = 15;
  private static final int CAT_UNSUCCESSFUL_RESPONSE_UPLOAD = 16;
  private static final int CAT_SUCCESSFUL_RESPONSE_UPLOAD = 17;
  private static final int CAT_OFFLINE_RESPONSE_PROCESSED = 18;
  private static final int CAT_NO_ACTIVE_ENROLMENTS = 19;
  private static final int CAT_GENERATE_ENROLMENT_CODE = 20;

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
  @Mock private AppConfig appConfig;
  @Mock private CaseGroupService caseGroupService;
  @Mock private CaseGroupAuditService caseGroupAuditService;
  @Mock private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Mock private InternetAccessCodeSvcClient internetAccessCodeSvcClient;
  @Mock private CaseIACService caseIacAuditService;
  @Mock private StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;
  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  @Spy @InjectMocks private Case mockCase;

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
    when(categoryRepo.findById(CategoryDTO.CategoryName.CASE_CREATED))
        .thenReturn(Optional.of(categories.get(CAT_PHYSICALLY_OR_MENTALLY_UNABLE)));
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
            CategoryDTO.CategoryName.CASE_CREATED,
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
    when(caseRepo.findById(NON_EXISTING_PARENT_CASE_FK)).thenReturn(Optional.ofNullable(null));

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
            CategoryDTO.CategoryName.COMPLETED_BY_PHONE,
            CASEEVENT_SUBCATEGORY,
            metadata);
    CaseEvent result = caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findById(NON_EXISTING_PARENT_CASE_FK);
    assertNull(result);
  }

  /**
   * Tries to apply a general event against a case already inactionable. Should allow it.
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testCreateNonActionableEventAgainstInactionableCase() throws Exception {
    when(caseRepo.findById(INACTIONABLE_HOUSEHOLD_CASE_FK))
        .thenReturn(Optional.of(cases.get(INACTIONABLE_HOUSEHOLD_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.NO_LONGER_REQUIRED))
        .thenReturn(Optional.of(categories.get(CAT_LACK_OF_COMPUTER_INTERNET_ACCESS)));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.NO_LONGER_REQUIRED, INACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findById(INACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.NO_LONGER_REQUIRED);
    // there was no change to case - no state transition
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, times(0)).disableAllIACsForCase(any(Case.class));
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
    when(caseRepo.findById(INITIAL_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(INITIAL_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.CASE_CREATED))
        .thenReturn(Optional.of(categories.get(CAT_CASE_CREATED)));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.CASE_CREATED, INITIAL_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(INITIAL_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.CASE_CREATED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
  }

  /**
   * We create a CaseEvent with category ACTION_CREATED on an ACTIONABLE BRES case (the one created
   * for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCreated() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.ACTION_CREATED))
        .thenReturn(Optional.of(categories.get(CAT_ACTION_CREATED)));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.ACTION_CREATED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.ACTION_CREATED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
  }

  /**
   * We create a CaseEvent with category ACTION_UPDATED on an ACTIONABLE BRES case (the one created
   * for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionUpdated() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.ACTION_UPDATED))
        .thenReturn(Optional.of(categories.get(CAT_ACTION_UPDATED)));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.ACTION_UPDATED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.ACTION_UPDATED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
  }

  /**
   * We create a CaseEvent with category ACTION_COMPLETED on an ACTIONABLE BRES case (the one
   * created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCompleted() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.ACTION_COMPLETED))
        .thenReturn(Optional.of(categories.get(CAT_ACTION_COMPLETED)));

    CaseEvent caseEvent =
        fabricateEvent(CategoryDTO.CategoryName.ACTION_COMPLETED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.ACTION_COMPLETED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
  }

  @Test
  public void testCaseGroupStatusIsTransitioned() throws Exception {
    Case targetCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    CaseGroup caseGroup = caseGroups.get(1);

    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(caseGroupRepo.findById(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK).getCaseGroupFK()))
        .thenReturn(Optional.of(caseGroup));
    when(categoryRepo.findById(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD))
        .thenReturn(Optional.of(categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD)));

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
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(caseGroupRepo.findById(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK).getCaseGroupFK()))
        .thenReturn(Optional.of(caseGroups.get(1)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.SECURE_MESSAGE_SENT))
        .thenReturn(Optional.of(categories.get(CAT_LACK_OF_COMPUTER_INTERNET_ACCESS)));

    CaseEvent caseEvent1 =
        fabricateEvent(
            CategoryDTO.CategoryName.SECURE_MESSAGE_SENT, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

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
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(Optional.of(targetCase));
    Category category = categories.get(CAT_ACTION_CANCELLATION_COMPLETED);
    when(categoryRepo.findById(CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED))
        .thenReturn(Optional.of(category));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED);
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
  }

  /**
   * We create a CaseEvent with category ACTION_CANCELLATION_COMPLETED on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCancellationCreated() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED))
        .thenReturn(Optional.of(categories.get(CAT_ACTION_CANCELLATION_CREATED)));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
  }

  /**
   * We create a CaseEvent with category ACCESS_CODE_AUTHENTICATION_ATTEMPT on an ACTIONABLE BRES
   * case (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventAccessCodeAuthenticationAttempt() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT))
        .thenReturn(Optional.of(categories.get(CAT_ACCESS_CODE_AUTHENTICATION_ATTEMPT)));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
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
    Category respondentEnrolledCategory = categories.get(CAT_RESPONDENT_ENROLED);
    when(categoryRepo.findById(CategoryDTO.CategoryName.RESPONDENT_ENROLED))
        .thenReturn(Optional.of(respondentEnrolledCategory));
    when(caseGroupRepo.findById(CASEGROUP_PK))
        .thenReturn(Optional.of(caseGroups.get(CASEGROUP_PK)));
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroups.get(CASEGROUP_PK));
    when(caseGroupService.findCaseGroupsForExecutedCollectionExercises(any()))
        .thenReturn(caseGroupList);
    List<Case> caseList = Collections.singletonList(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(any())).thenReturn(caseList);
    List<CollectionExerciseDTO> listCollex = Collections.singletonList(makeCollectionExercise());
    when(caseRepo.saveAndFlush(any(Case.class)))
        .thenReturn(cases.get(ENROLMENT_CASE_INDIVIDUAL_FK));

    // When
    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.RESPONDENT_ENROLED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    caseService.createCaseEvent(caseEvent, cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));

    // Then
    verify(categoryRepo).findById(CategoryDTO.CategoryName.RESPONDENT_ENROLED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).saveAndFlush(argument.capture());

    verify(caseSvcStateTransitionManager, times(1))
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
    Case caze = argument.getValue();
    assertEquals(CaseState.ACTIONABLE, caze.getState());
    assertEquals(true, caze.isActiveEnrolment());
  }

  /**
   * Tests that with multiple survey ids then the limit per survey matches maxCasesPerSurvey and
   * that the later cases in the list are rejected This suffices because SpringBoot implements the
   * findByDateTime desc , so we only need validate the later ones are removed
   *
   * @throws Exception
   */
  @Test
  public void testFindCasesByPartyIdLimitedPerSurveyLimitsCases() throws Exception {

    int maxCasesPerSurvey = 2;

    // Arrange: Create a List<Case> of 6 entries corresponding to two groups
    UUID partyId = UUID.randomUUID();

    UUID groupUUIDOne = UUID.randomUUID();
    UUID groupUUIDTwo = UUID.randomUUID();

    Case case_one = makeCaseWithPartyId(groupUUIDOne, partyId);
    Case case_two = makeCaseWithPartyId(groupUUIDOne, partyId);
    Case case_three = makeCaseWithPartyId(groupUUIDOne, partyId);
    Case case_four = makeCaseWithPartyId(groupUUIDTwo, partyId);
    Case case_five = makeCaseWithPartyId(groupUUIDTwo, partyId);
    Case case_six = makeCaseWithPartyId(groupUUIDTwo, partyId);

    List<Case> caseList = new ArrayList<>();

    caseList.add(case_one);
    caseList.add(case_two);
    caseList.add(case_three);
    caseList.add(case_four);
    caseList.add(case_five);
    caseList.add(case_six);

    CaseGroup caseGroupOne = makeCaseGroupWithSurveyId(UUID.randomUUID());
    CaseGroup caseGroupTwo = makeCaseGroupWithSurveyId(UUID.randomUUID());

    when(caseGroupRepo.findById(groupUUIDOne)).thenReturn(caseGroupOne);
    when(caseGroupRepo.findById(groupUUIDTwo)).thenReturn(caseGroupTwo);
    when(caseRepo.findByPartyIdOrderByCreatedDateTimeDesc(partyId)).thenReturn(caseList);

    // Act: limit those to 2 cases per survey id

    List<Case> results =
        caseService.findCasesByPartyIdLimitedPerSurvey(partyId, false, maxCasesPerSurvey);

    // Assert: First the right count
    assertEquals(results.size(), 4);

    // Assert: Secondly that the Cases at the start of the list are present
    assertEquals(results.get(0).getId(), case_one.getId());
    assertEquals(results.get(1).getId(), case_two.getId());

    assertEquals(results.get(2).getId(), case_four.getId());
    assertEquals(results.get(3).getId(), case_five.getId());
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

  private CaseGroup makeCaseGroupWithSurveyId(UUID surveyId) {
    CaseGroup cg = makeCaseGroup();
    cg.setSurveyId(surveyId);
    return cg;
  }

  /**
   * Make a test case
   *
   * @return a new test case
   */
  private Case makeCase(UUID groupId) {
    Case c = new Case();
    c.setId(UUID.randomUUID());
    c.setSampleUnitType(SampleUnitDTO.SampleUnitType.B);
    c.setState(CaseState.ACTIONABLE);
    c.setActionPlanId(UUID.randomUUID());
    c.setCaseGroupId(groupId);
    c.setCaseGroupFK(ENROLMENT_CASE_INDIVIDUAL_FK);
    return c;
  }

  private Case makeCaseWithPartyId(UUID groupId, UUID partyId) {
    Case c = makeCase(groupId);
    c.setPartyId(partyId);
    return c;
  }

  /**
   * We create a CaseEvent with category COLLECTION_INSTRUMENT_DOWNLOADED on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventCollectionInstrumentDownloaded() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED))
        .thenReturn(Optional.of(categories.get(CAT_COLLECTION_INSTRUMENT_DOWNLOADED)));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
  }

  /**
   * We create a CaseEvent with category UNSUCCESSFUL_RESPONSE_UPLOAD on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventUnsuccessfulResponseUploaded() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD))
        .thenReturn(Optional.of(categories.get(CAT_UNSUCCESSFUL_RESPONSE_UPLOAD)));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, never()).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, never())
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
  }

  /**
   * We create a CaseEvent with category OFFLINE_RESPONSE_PROCESSED on an ACTIONABLE BRES case (the
   * one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventOfflineResponseProcessed() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    when(categoryRepo.findById(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED))
        .thenReturn(Optional.of(categories.get(CAT_OFFLINE_RESPONSE_PROCESSED)));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);
    verify(caseEventRepo, times(1)).save(caseEvent);
    verify(caseRepo, times(1)).saveAndFlush(any(Case.class));
    verify(caseIacAuditService, times(1)).disableAllIACsForCase(any(Case.class));
    verify(caseSvcStateTransitionManager, times(1))
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class));
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
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));

    Category successfulResponseUploadedCategory = categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD);
    when(categoryRepo.findById(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD))
        .thenReturn(Optional.of(successfulResponseUploadedCategory));

    CaseEvent caseEvent =
        fabricateEvent(
            CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);
    verify(caseEventRepo, times(1)).save(caseEvent);
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).saveAndFlush(argument.capture());
    verify(caseSvcStateTransitionManager, times(1))
        .transition(any(CaseState.class), any(CaseDTO.CaseEvent.class)); // action
    // service
    // should
    // be
    // told
    // of
    // the
    // old
    // case
    // state
    // change
    // for
    // both
    // cases

    // Now verifying that case has been moved to INACTIONABLE
    Case oldCase = argument.getAllValues().get(0);
    assertEquals(CaseState.INACTIONABLE, oldCase.getState());
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
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    Category noActiveEnrolmentCategory = categories.get(CAT_NO_ACTIVE_ENROLMENTS);
    when(categoryRepo.findById(CategoryName.NO_ACTIVE_ENROLMENTS))
        .thenReturn(Optional.of(noActiveEnrolmentCategory));
    CaseGroup caseGroup = makeCaseGroup();
    when(caseGroupService.findCaseGroupsForExecutedCollectionExercises(any()))
        .thenReturn(Collections.singletonList(caseGroup));
    List<Case> caseList = Collections.singletonList(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(any())).thenReturn(caseList);
    ActionPlanDTO actionPlan = new ActionPlanDTO();
    actionPlan.setId(UUID.randomUUID());

    CaseEvent caseEvent =
        fabricateEvent(CategoryName.NO_ACTIVE_ENROLMENTS, ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo, times(1)).findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findById(CategoryName.NO_ACTIVE_ENROLMENTS);
    verify(caseEventRepo, times(1)).save(caseEvent);
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).saveAndFlush(argument.capture());
  }

  /**
   * caseService.createCaseEvent will be called with invalid state transitions but suppress the
   * exception. This is expected behaviour. This code smells but keeping as is.
   */
  @Test
  public void testGivenCaseGroupChangeIsInvalidWhenTransitionFailGracefully() throws Exception {
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK)));
    Category successfulResponseUploadedCategory = categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD);
    when(categoryRepo.findById(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD))
        .thenReturn(Optional.of(successfulResponseUploadedCategory));

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
    when(caseRepo.findById(ACTIONABLE_BUSINESS_UNIT_CASE_FK))
        .thenReturn(Optional.of(actionableBCase));
    when(categoryRepo.findById(CategoryDTO.CategoryName.GENERATE_ENROLMENT_CODE))
        .thenReturn(Optional.of(categories.get(CAT_GENERATE_ENROLMENT_CODE)));
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
    String collectionExerciseId = UUID.randomUUID().toString();
    UUID partyId = UUID.randomUUID();
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
    sampleUnitParent.setCollectionExerciseId(collectionExerciseId);
    sampleUnitParent.setSampleUnitChildren(sampleUnitChildren);
    sampleUnitParent.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnitParent.setPartyId(partyId.toString());
    sampleUnitParent.setSampleUnitRef("str1234");
    sampleUnitParent.setSampleUnitType("B");
    sampleUnitParent.setId(UUID.randomUUID().toString());

    List<CollectionExerciseDTO> collectionExercises =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    when(collectionExerciseSvcClient.getCollectionExercise(any()))
        .thenReturn(collectionExercises.get(0));

    UUID groupUUIDOne = UUID.randomUUID();

    mockCase = makeCaseWithPartyId(groupUUIDOne, partyId);
    mockCase.setCasePK(1);

    when(caseRepo.saveAndFlush(any(Case.class))).thenReturn(mockCase);
    when(internetAccessCodeSvcClient.generateIACs(1))
        .thenReturn(Collections.singletonList(IAC_FOR_TEST));

    when(caseIacAuditRepo.saveAndFlush(any())).thenReturn(new CaseIacAudit());
    when(caseGroupService.isCaseGroupUnique(any(SampleUnitParent.class))).thenReturn(true);

    caseService.createInitialCase(sampleUnitParent);

    ArgumentCaptor<CaseGroup> caseGroup = ArgumentCaptor.forClass(CaseGroup.class);
    verify(caseGroupRepo, times(1)).saveAndFlush(caseGroup.capture());

    List<CaseGroup> capturedCaseGroup = caseGroup.getAllValues();

    assertEquals(
        collectionExerciseId, capturedCaseGroup.get(0).getCollectionExerciseId().toString());
    verify(caseRepo, times(6)).saveAndFlush(any());
  }

  @Test
  public void testCreateInitialCaseWithSampleUnitChildrenDoesNothingIfDuplicate() throws Exception {
    String collectionExerciseId = UUID.randomUUID().toString();
    UUID partyId = UUID.randomUUID();
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
    sampleUnitParent.setCollectionExerciseId(collectionExerciseId);
    sampleUnitParent.setSampleUnitChildren(sampleUnitChildren);
    sampleUnitParent.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnitParent.setPartyId(partyId.toString());
    sampleUnitParent.setSampleUnitRef("str1234");
    sampleUnitParent.setSampleUnitType("B");
    sampleUnitParent.setId(UUID.randomUUID().toString());

    // case group is a duplicate
    when(caseGroupService.isCaseGroupUnique(any(SampleUnitParent.class))).thenReturn(false);

    // so this should do nothing
    caseService.createInitialCase(sampleUnitParent);

    // check
    verify(caseGroupRepo, times(0)).saveAndFlush(any());
    verify(caseRepo, times(0)).saveAndFlush(any());
    verify(caseIacAuditRepo, times(0)).saveAndFlush(any());
  }

  @Test
  public void testCreateInitialCaseWithSampleUnitChildrenFailedOnIACUpdate() throws Exception {
    String collectionExerciseId = UUID.randomUUID().toString();
    UUID partyId = UUID.randomUUID();
    SampleUnitParent sampleUnitParent = new SampleUnitParent();
    SampleUnit sampleUnit = new SampleUnit();
    SampleUnitChildren sampleUnitChildren =
        new SampleUnitChildren(new ArrayList<>(Collections.singletonList(sampleUnit)));
    sampleUnit.setActionPlanId(UUID.randomUUID().toString());
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setPartyId(partyId.toString());
    sampleUnit.setSampleUnitRef("str1234");
    sampleUnit.setSampleUnitType("BI");
    sampleUnit.setId(UUID.randomUUID().toString());

    sampleUnitParent.setActionPlanId(UUID.randomUUID().toString());
    sampleUnitParent.setCollectionExerciseId(collectionExerciseId);
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

    UUID groupUUIDOne = UUID.randomUUID();

    mockCase = makeCaseWithPartyId(groupUUIDOne, partyId);
    mockCase.setCasePK(1);

    when(caseRepo.saveAndFlush(any(Case.class))).thenReturn(mockCase);
    when(caseGroupService.isCaseGroupUnique(any(SampleUnitParent.class))).thenReturn(true);

    when(internetAccessCodeSvcClient.generateIACs(any(Integer.class)))
        .thenThrow(new RuntimeException("IAC access failed"));

    caseService.createInitialCase(sampleUnitParent);

    ArgumentCaptor<CaseGroup> caseGroup = ArgumentCaptor.forClass(CaseGroup.class);
    verify(caseGroupRepo, times(1)).saveAndFlush(caseGroup.capture());

    List<CaseGroup> capturedCaseGroup = caseGroup.getAllValues();

    assertEquals(
        collectionExerciseId, capturedCaseGroup.get(0).getCollectionExerciseId().toString());
    verify(caseRepo, times(4)).saveAndFlush(any());
    verify(caseIacAuditRepo, times((0))).saveAndFlush(any());
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
    when(caseGroupRepo.findById(CASEGROUP_PK))
        .thenReturn(Optional.of(caseGroups.get(CASEGROUP_PK - 1)));
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
            CaseState.INACTIONABLE, CaseDTO.CaseEvent.DEACTIVATED))
        .thenReturn(CaseState.INACTIONABLE);
  }

  /** mock loading data */
  private void mockAppConfigUse() {
    InternetAccessCodeSvc iacSvc = new InternetAccessCodeSvc();
    iacSvc.setIacPutPath(IAC_SVC_PUT_PATH);
    iacSvc.setIacPostPath(IAC_SVC_POST_PATH);
  }
}
