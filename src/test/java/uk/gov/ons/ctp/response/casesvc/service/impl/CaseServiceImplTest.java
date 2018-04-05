package uk.gov.ons.ctp.response.casesvc.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.InternetAccessCodeSvc;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.*;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import static uk.gov.ons.ctp.common.state.BasicStateTransitionManager.TRANSITION_ERROR_MSG;
import static uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent.ACCOUNT_CREATED;
import static uk.gov.ons.ctp.response.casesvc.service.impl.CaseServiceImpl.IAC_OVERUSE_MSG;
import static uk.gov.ons.ctp.response.casesvc.service.impl.CaseServiceImpl.MISSING_NEW_CASE_MSG;
import static uk.gov.ons.ctp.response.casesvc.service.impl.CaseServiceImpl.WRONG_OLD_SAMPLE_UNIT_TYPE_MSG;

/**
 * Test the CaseServiceImpl primarily the createCaseEvent functionality. Note
 * that these tests require the mocked category data to represent the real
 * Category table data in order to be effective.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseServiceImplTest {

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
  private static final int CAT_HOUSEHOLD_PAPER_REQUESTED = 16;
  private static final int CAT_HOUSEHOLD_REPLACEMENT_IAC_REQUESTED = 17;
  private static final int CAT_H_INDIVIDUAL_PAPER_REQUESTED = 19;
  private static final int CAT_H_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED = 20;
  private static final int CAT_H_INDIVIDUAL_RESPONSE_REQUESTED = 21;
  private static final int CAT_ONLINE_QUESTIONNAIRE_RESPONSE = 23;
  private static final int CAT_PAPER_QUESTIONNAIRE_RESPONSE = 24;
  private static final int CAT_REFUSAL = 26;
  private static final int CAT_RESPONDENT_ENROLED = 27;
  private static final int CAT_TRANSLATION_ARABIC = 29;
  private static final int CAT_RESPONDENT_ACCOUNT_CREATED = 44;
  private static final int CAT_ACCESS_CODE_AUTHENTICATION_ATTEMPT = 45;
  private static final int CAT_COLLECTION_INSTRUMENT_DOWNLOADED = 46;
  private static final int CAT_UNSUCCESSFUL_RESPONSE_UPLOAD = 47;
  private static final int CAT_SUCCESSFUL_RESPONSE_UPLOAD = 48;
  private static final int CAT_OFFLINE_RESPONSE_PROCESSED = 49;

  /**
   * Note that the Integer values below are linked to the order in which cases appear
   * in the array defined at CaseServiceImplTest.Case.json = casePK
   */
  private static final Integer NON_EXISTING_PARENT_CASE_FK = 0;
  private static final Integer ACTIONABLE_HOUSEHOLD_CASE_FK = 0;
  private static final Integer INACTIONABLE_HOUSEHOLD_CASE_FK = 1;
  private static final Integer ACTIONABLE_H_INDIVIDUAL_CASE_FK = 2;
  private static final Integer NEW_HOUSEHOLD_CASE_FK = 4;
  private static final Integer NEW_H_INDIVIDUAL_CASE_FK = 5;
  private static final Integer ENROLMENT_CASE_INDIVIDUAL_FK = 8;
  private static final Integer ACTIONABLE_BUSINESS_UNIT_CASE_FK = 9;
  private static final Integer INITIAL_BUSINESS_UNIT_CASE_FK = 10;
  private static final Integer ACTIONABLE_BI_CASE_FK = 11;
  private static final Integer INACTIONABLE_BUSINESS_UNIT_CASE_FK = 12;
  private static final Integer ANOTHER_ACTIONABLE_BI_CASE_FK = 13;

  private static final Integer CASEGROUP_PK = 1;

  private static final String CASEEVENT_CREATEDBY = "unit test";
  private static final String CASEEVENT_DESCRIPTION = "a desc";
  private static final String CASEEVENT_SUBCATEGORY = "sub category";
  private static final String IAC_FOR_TEST = "ABCD-EFGH-IJKL";
  private static final String UUID_FOR_ACTIONABLE_HOUSEHOLD_CASE_FK = "1bc5d41b-0549-40b3-ba76-42f6d4cf3fd1";
  private static final String UUID_FOR_ACTIONABLE_BUSINESS_UNIT_CASE_FK = "91fda7f2-3825-4bd4-baef-943a0ccf0856";

  @Mock
  private CaseRepository caseRepo;

  @Mock
  private CaseEventRepository caseEventRepository;

  @Mock
  private CategoryRepository categoryRepo;

  @Mock
  private CaseGroupRepository caseGroupRepo;

  @Mock
  private CaseGroupService caseGroupService;

  @Mock
  private AppConfig appConfig;

  @Mock
  private CaseNotificationPublisher notificationPublisher;

  @Mock
  private InternetAccessCodeSvcClientService internetAccessCodeSvcClientService;

  @Mock
  private CollectionExerciseSvcClientService collectionExerciseSvcClientService;

  @Mock
  private ActionSvcClientService actionSvcClientService;

  @Mock
  private StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  @Mock
  private CaseGroupAuditService caseGroupAuditService;

  @InjectMocks
  private CaseServiceImpl caseService;

  private List<Case> cases;
  private List<Category> categories;
  private List<CaseGroup> caseGroups;

  /**
   * All of these tests require the mocked repos to respond with predictable
   * data loaded from test fixture json files.
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
    assertNull(caseService.findCaseByIac(IAC_FOR_TEST));
  }

  /**
   * To test findCaseByIac when more than one case is found for given IAC
   */
  @Test
  public void testFindCaseByIacMoreThanOneCaseFound() {
    when(caseRepo.findByIac(IAC_FOR_TEST)).thenReturn(cases);

    try {
      caseService.findCaseByIac(IAC_FOR_TEST);
      fail();
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(String.format(IAC_OVERUSE_MSG, IAC_FOR_TEST), e.getMessage());
    }
  }

  /**
   * To test findCaseByIac when one case is found for given IAC
   *
   * @throws CTPException if findCaseByIac does
   */
  @Test
  public void testFindCaseByIacOneCaseFound() throws CTPException {
    List<Case> result = new ArrayList<>();
    result.add(cases.get(0));
    when(caseRepo.findByIac(IAC_FOR_TEST)).thenReturn(result);

    assertEquals(cases.get(0), caseService.findCaseByIac(IAC_FOR_TEST));
  }

  /**
   * Should not be allowed to create an event against a case that does not exist!
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testCreateCaseEventAgainstNonExistentCase() throws CTPException {
    when(caseRepo.findOne(NON_EXISTING_PARENT_CASE_FK)).thenReturn(null);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ADDRESS_DETAILS_INCORRECT))
            .thenReturn(categories.get(CAT_ADDRESS_DETAILS_INCORRECT));

    Timestamp currentTime = DateTimeUtil.nowUTC();
    CaseEvent caseEvent = new CaseEvent(1, NON_EXISTING_PARENT_CASE_FK, CASEEVENT_DESCRIPTION,
            CASEEVENT_CREATEDBY, currentTime, CategoryDTO.CategoryName.ADDRESS_DETAILS_INCORRECT,
            CASEEVENT_SUBCATEGORY);
    CaseEvent result = caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo).findOne(NON_EXISTING_PARENT_CASE_FK);
    assertNull(result);
  }

  /**
   * Tries to apply an actionable event against a case already inactionable. Should allow.
   * 
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testCreateActionableEventAgainstInactionableCase() throws Exception {
    when(caseRepo.findOne(INACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(INACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.TRANSLATION_ARABIC))
            .thenReturn(categories.get(CAT_TRANSLATION_ARABIC));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.TRANSLATION_ARABIC, INACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(2)).findOne(INACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.TRANSLATION_ARABIC);
    // there was no change to case - no state transition
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(1)).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * Tries to apply a general event against a case already inactionable. Should
   * allow it.
   * 
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testCreateNonActionableEventAgainstInactionableCase() throws Exception {
    when(caseRepo.findOne(INACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(INACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.GENERAL_COMPLAINT))
            .thenReturn(categories.get(CAT_GENERAL_COMPLAINT));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.GENERAL_COMPLAINT, INACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo).findOne(INACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.GENERAL_COMPLAINT);
    // there was no change to case - no state transition
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class), any(UUID.class),
        any(String.class));
  }

  /**
   * Tries to apply a response event against an actionable case Should allow it
   * and record response.
   * 
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testCreatePaperResponseEventAgainstActionableCase() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE))
            .thenReturn(categories.get(CAT_PAPER_QUESTIONNAIRE_RESPONSE));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE,
        ACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(1, caseSaved.getResponses().size());
    assertEquals(CaseState.INACTIONABLE, caseSaved.getState());

    // IAC should not be disabled for paper responses
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));

    // action service should be told of case state change
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));

    // no new action to be created
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class), any(UUID.class),
        any(String.class));

    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Tries to apply an online response event against an actionable case Should
   * allow it and record response.
   * 
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testCreateOnlineResponseEventAgainstActionableCase() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE))
            .thenReturn(categories.get(CAT_ONLINE_QUESTIONNAIRE_RESPONSE));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE,
        ACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(1, caseSaved.getResponses().size());
    assertEquals(CaseState.INACTIONABLE, caseSaved.getState());

    // IAC should be disabled for online responses
    verify(internetAccessCodeSvcClientService, times(1)).disableIAC(any(String.class));

    // action service should be told of case state change
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));

    // no new action to be created
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class), any(UUID.class),
        any(String.class));

    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Tries to apply a response event against an already inactionable case Should
   * allow it and record response but the state should remain inactionable.
   * 
   * @throws Exception exception thrown
   */
  @Test
  public void testCreateResponseEventAgainstInActionableCase() throws Exception {
    when(caseRepo.findOne(INACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(INACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE))
            .thenReturn(categories.get(CAT_PAPER_QUESTIONNAIRE_RESPONSE));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE,
        INACTIONABLE_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo).findOne(INACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(2, caseSaved.getResponses().size());
    assertEquals(CaseState.INACTIONABLE, caseSaved.getState());

    // IAC should not be disabled again!
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));

    // action service should NOT be told of case state change
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));

    // no new action to be created
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class), any(UUID.class),
        any(String.class));

    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Bluesky test for creating a replacement household case
   * 
   * @throws Exception exception thrown
   * */
  @Test
  public void testBlueSkyHouseholdIACRequested() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK));
    when(caseRepo.findOne(NEW_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(NEW_HOUSEHOLD_CASE_FK));
    when(caseRepo.saveAndFlush(any(Case.class))).thenReturn(cases.get(NEW_HOUSEHOLD_CASE_FK));  // the new case
    when(categoryRepo.findOne(CategoryDTO.CategoryName.HOUSEHOLD_REPLACEMENT_IAC_REQUESTED)).
            thenReturn(categories.get(CAT_HOUSEHOLD_REPLACEMENT_IAC_REQUESTED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.HOUSEHOLD_REPLACEMENT_IAC_REQUESTED,
            ACTIONABLE_HOUSEHOLD_CASE_FK);
    Case newCase = caseRepo.findOne(NEW_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent, newCase);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.HOUSEHOLD_REPLACEMENT_IAC_REQUESTED);
    verify(caseRepo, times(2)).saveAndFlush(any(Case.class));
    Case oldCase = caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(internetAccessCodeSvcClientService, times(1)).disableIAC(oldCase.getIac());
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));

    // no new action to be created
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));

    // action service should be told of case state change
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));

    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Bluesky test for creating a IndividualReplacementIACRequested
   * 
   * @throws Exception exception thrown
   */
  @Test
  public void testBlueSkyIndividualReplacementIACRequested() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK)).thenReturn(cases.get(ACTIONABLE_H_INDIVIDUAL_CASE_FK));
    when(caseRepo.findOne(NEW_H_INDIVIDUAL_CASE_FK)).thenReturn(cases.get(NEW_H_INDIVIDUAL_CASE_FK));
    when(caseRepo.saveAndFlush(any(Case.class))).thenReturn(
            cases.get(NEW_H_INDIVIDUAL_CASE_FK));  // the new case
    when(categoryRepo.findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED))
            .thenReturn(categories.get(CAT_H_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.H_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED,
            ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    Case newCase = caseRepo.findOne(NEW_H_INDIVIDUAL_CASE_FK);
    caseService.createCaseEvent(caseEvent, newCase);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED);
    verify(caseRepo, times(2)).saveAndFlush(any(Case.class));
    Case oldCase = caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    verify(internetAccessCodeSvcClientService, times(1)).disableIAC(oldCase.getIac());
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Bluesky test for creating a paper form request case
   *
   * @throws Exception exception thrown
   */
  @Test
  public void testBlueSkyHouseholdPaperRequested() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK));
    when(caseRepo.findOne(NEW_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(NEW_HOUSEHOLD_CASE_FK));
    when(caseRepo.saveAndFlush(any(Case.class))).thenReturn(cases.get(NEW_HOUSEHOLD_CASE_FK));  // the new case
    when(categoryRepo.findOne(CategoryDTO.CategoryName.HOUSEHOLD_PAPER_REQUESTED))
            .thenReturn(categories.get(CAT_HOUSEHOLD_PAPER_REQUESTED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.HOUSEHOLD_PAPER_REQUESTED,
        ACTIONABLE_HOUSEHOLD_CASE_FK);
    Case newCase = caseRepo.findOne(NEW_HOUSEHOLD_CASE_FK);
    caseService.createCaseEvent(caseEvent, newCase);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.HOUSEHOLD_PAPER_REQUESTED);
    verify(caseRepo, times(2)).saveAndFlush(any(Case.class));
    Case oldCase = caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(oldCase.getIac());
    // action service should be told of case state change
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));
    // no new action to be created
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Bluesky test for creating a replacement individual case
   * 
   * @throws Exception exception thrown
   */
  @Test
  public void testBlueSkyIndividualResponseRequested() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK));
    when(caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK)).thenReturn(cases.get(ACTIONABLE_H_INDIVIDUAL_CASE_FK));
    when(caseRepo.saveAndFlush(any(Case.class))).thenReturn(
            cases.get(ACTIONABLE_H_INDIVIDUAL_CASE_FK));  // the new case
    when(categoryRepo.findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED))
            .thenReturn(categories.get(CAT_H_INDIVIDUAL_RESPONSE_REQUESTED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED,
            ACTIONABLE_HOUSEHOLD_CASE_FK);
    Case newCase = caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    caseService.createCaseEvent(caseEvent, newCase);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED);
    verify(caseRepo, times(1)).saveAndFlush(any(Case.class));
    Case oldCase = caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(oldCase.getIac());
    // action service should be told of case state change
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    // no new action to be created
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(caseEventRepository, times(1)).save(caseEvent);
  }


  /**
   * Bluesky test for creating an individual paper request event
   * 
   * @throws Exception exception thrown
   */
  @Test
  public void testBlueSkyIndividualPaperRequested() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK)).thenReturn(cases.get(ACTIONABLE_H_INDIVIDUAL_CASE_FK));
    when(caseRepo.findOne(NEW_H_INDIVIDUAL_CASE_FK)).thenReturn(cases.get(NEW_H_INDIVIDUAL_CASE_FK));
    when(caseRepo.saveAndFlush(any(Case.class))).thenReturn(
            cases.get(NEW_H_INDIVIDUAL_CASE_FK));  // the new case
    when(categoryRepo.findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_PAPER_REQUESTED))
            .thenReturn(categories.get(CAT_H_INDIVIDUAL_PAPER_REQUESTED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.H_INDIVIDUAL_PAPER_REQUESTED,
        ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    Case newCase = caseRepo.findOne(NEW_H_INDIVIDUAL_CASE_FK);
    caseService.createCaseEvent(caseEvent, newCase);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_PAPER_REQUESTED);
    verify(caseRepo, times(2)).saveAndFlush(any(Case.class));
    Case oldCase = caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(oldCase.getIac());
    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  @Test
  public void testIACDisabledAfterOnlineResponseAfterRefusal() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK)).thenReturn(cases.get(ACTIONABLE_H_INDIVIDUAL_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.REFUSAL)).thenReturn(categories.get(CAT_REFUSAL));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE)).thenReturn(categories.
            get(CAT_ONLINE_QUESTIONNAIRE_RESPONSE));

    CaseEvent refusalCaseEvent = fabricateEvent(CategoryDTO.CategoryName.REFUSAL, ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    caseService.createCaseEvent(refusalCaseEvent, null);

    CaseEvent onlineResponseCaseEvent = fabricateEvent(CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE,
            ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    caseService.createCaseEvent(onlineResponseCaseEvent, null);

    Case oldCase = caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    verify(internetAccessCodeSvcClientService, times(1)).disableIAC(oldCase.getIac());
  }

  /**
   * Tries to create an individual response requested against an individual case
   * - should be household case so should throw and not do anything
   * 
   * @throws Exception exception thrown
   */
  @Test
  public void testIndividualResponseRequestedAgainstIndividualCaseNotAllowed() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK)).thenReturn(cases.get(ACTIONABLE_H_INDIVIDUAL_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED)).thenReturn(categories.
            get(CAT_H_INDIVIDUAL_RESPONSE_REQUESTED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED,
            ACTIONABLE_H_INDIVIDUAL_CASE_FK);

    Case oldCase = caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    try {
      caseService.createCaseEvent(caseEvent, oldCase);
      fail();
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "HI", "H"), e.getMessage());
    }

    verify(caseRepo, times(2)).findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    // IAC should not be disabled
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * Tries to apply a Household event against an Individual Case NOT ALLOWED!.
   * Should throw and not save anything
   *
   * @throws Exception exception thrown
   */
  @Test
  public void testHouseholdPaperRequestedAgainstIndividualCaseNotAllowed() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK)).thenReturn(
            cases.get(ACTIONABLE_H_INDIVIDUAL_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.HOUSEHOLD_PAPER_REQUESTED)).thenReturn(categories.
            get(CAT_HOUSEHOLD_PAPER_REQUESTED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.HOUSEHOLD_PAPER_REQUESTED,
            ACTIONABLE_H_INDIVIDUAL_CASE_FK);

    Case oldCase = caseRepo.findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    try {
      caseService.createCaseEvent(caseEvent, oldCase);
      fail();
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "HI", "H"), e.getMessage());
    }

    verify(caseRepo, times(2)).findOne(ACTIONABLE_H_INDIVIDUAL_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.HOUSEHOLD_PAPER_REQUESTED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * Tries to create a individual request without providing the individual case.
   * Should throw and not save anything
   * 
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testIndividualResponseRequestedAgainstIndividualCaseWithoutNewCase() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_FK)).thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED)).thenReturn(categories.
            get(CAT_H_INDIVIDUAL_RESPONSE_REQUESTED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED,
            ACTIONABLE_HOUSEHOLD_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(MISSING_NEW_CASE_MSG, UUID_FOR_ACTIONABLE_HOUSEHOLD_CASE_FK), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_HOUSEHOLD_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.H_INDIVIDUAL_RESPONSE_REQUESTED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category CASE_CREATED on an initial BRES case
   * (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventCaseCreated() throws Exception {
    when(caseRepo.findOne(INITIAL_BUSINESS_UNIT_CASE_FK)).thenReturn(cases.get(INITIAL_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.CASE_CREATED)).thenReturn(categories.
            get(CAT_CASE_CREATED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.CASE_CREATED, INITIAL_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(INITIAL_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.CASE_CREATED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category ACTION_CREATED on an ACTIONABLE BRES case
   * (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCreated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(
            cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_CREATED)).thenReturn(categories.
            get(CAT_ACTION_CREATED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.ACTION_CREATED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_CREATED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category ACTION_UPDATED on an ACTIONABLE BRES case
   * (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionUpdated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(
            cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_UPDATED)).thenReturn(categories.
            get(CAT_ACTION_UPDATED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.ACTION_UPDATED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_UPDATED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category ACTION_COMPLETED on an ACTIONABLE BRES case
   * (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCompleted() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(
            cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_COMPLETED)).thenReturn(categories.
            get(CAT_ACTION_COMPLETED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.ACTION_COMPLETED, ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_COMPLETED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  @Test
  public void testCaseGroupStatusIsTransitioned() throws Exception {
    Case targetCase = cases.get(ACTIONABLE_BI_CASE_FK);
    CaseGroup caseGroup = caseGroups.get(1);

    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(
            cases.get(ACTIONABLE_BI_CASE_FK));
    when(caseGroupRepo.findOne(cases.get(ACTIONABLE_BI_CASE_FK).getCaseGroupFK())).thenReturn(caseGroup);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD)).thenReturn(
            categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD));


    CaseEvent caseEvent1 = fabricateEvent(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, ACTIONABLE_BI_CASE_FK);

    caseService.createCaseEvent(caseEvent1, null);

    verify(caseGroupService, times(1)).transitionCaseGroupStatus(caseGroup,
        CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, targetCase.getPartyId());
  }

  @Test
  public void testCaseGroupStatusNotUpdated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(
            cases.get(ACTIONABLE_BI_CASE_FK));
    when(caseGroupRepo.findOne(cases.get(ACTIONABLE_BI_CASE_FK).getCaseGroupFK())).thenReturn(caseGroups.get(1));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.GENERAL_COMPLAINT)).thenReturn(
            categories.get(CAT_GENERAL_COMPLAINT));


    CaseEvent caseEvent1 = fabricateEvent(CategoryDTO.CategoryName.GENERAL_COMPLAINT, ACTIONABLE_BI_CASE_FK);

    caseService.createCaseEvent(caseEvent1, null);

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
    Case targetCase = cases.get(ACTIONABLE_BI_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(targetCase);
    Category category = categories.get(CAT_ACTION_CANCELLATION_COMPLETED);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED)).
            thenReturn(category);

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED,
            ACTIONABLE_BI_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(caseGroupService, times(1)).transitionCaseGroupStatus(caseGroups.get(CASEGROUP_PK - 1),
            CategoryDTO.CategoryName.ACTION_CANCELLATION_COMPLETED, targetCase.getPartyId());
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category ACTION_CANCELLATION_COMPLETED on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventActionCancellationCreated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(cases.get(ACTIONABLE_BI_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED)).
            thenReturn(categories.get(CAT_ACTION_CANCELLATION_CREATED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED,
            ACTIONABLE_BI_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category ACCESS_CODE_AUTHENTICATION_ATTEMPT on an ACTIONABLE BRES case
   * (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventAccessCodeAuthenticationAttempt() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(
            cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT)).
            thenReturn(categories.get(CAT_ACCESS_CODE_AUTHENTICATION_ATTEMPT));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category ACCESS_CODE_AUTHENTICATION_ATTEMPT versus a Case of wrong sampleUnitType
   * (ie NOT a B)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventAccessCodeAuthenticationAttemptVersusWrongCaseType() throws Exception {
    Case existingCase = cases.get(ACTIONABLE_BI_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT)).
            thenReturn(categories.get(CAT_ACCESS_CODE_AUTHENTICATION_ATTEMPT));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT,
            ACTIONABLE_BI_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "BI", "B"), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category RESPONDENT_ACCOUNT_CREATED on an ACTIONABLE BRES case
   * (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventRespondentAccountCreated() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(cases.
            get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED)).
            thenReturn(categories.get(CAT_RESPONDENT_ACCOUNT_CREATED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, times(1)).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, times(2)).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category RESPONDENT_ACCOUNT_CREATED on an INACTIONABLE BRES case
   * (the one created for a business unit B, Tesco for instance)
   *
   * The validation should fail as the BRES case should NOT be INACTIONABLE.
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventRespondentAccountCreatedVersusCaseInWrongState() throws Exception {
    when(caseRepo.findOne(INACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(cases.
        get(INACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED)).
        thenReturn(categories.get(CAT_RESPONDENT_ACCOUNT_CREATED));
    // To mimick what BasicStateTransitionManager does
    String errorMsg = String.format(TRANSITION_ERROR_MSG, CaseState.INACTIONABLE, ACCOUNT_CREATED);
    when(caseSvcStateTransitionManager.transition(CaseState.INACTIONABLE, ACCOUNT_CREATED)).thenThrow(
        new CTPException(CTPException.Fault.BAD_REQUEST, errorMsg));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED,
        INACTIONABLE_BUSINESS_UNIT_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(errorMsg, e.getMessage());
    }

    verify(caseRepo, times(1)).findOne(INACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED);
    verify(caseEventRepository, never()).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, times(1)).transition(any(CaseState.class),
        any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
        any(String.class));
  }

  /**
   * We create a CaseEvent with category RESPONDENT_ACCOUNT_CREATED versus a Case of wrong sampleUnitType
   * (ie NOT a B)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventRespondentAccountCreatedVersusWrongCaseType() throws Exception {
    Case existingCase = cases.get(ACTIONABLE_BI_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED)).
            thenReturn(categories.get(CAT_RESPONDENT_ACCOUNT_CREATED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED, ACTIONABLE_BI_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "BI", "B"), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.RESPONDENT_ACCOUNT_CREATED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category RESPONDENT_ENROLED on an ACTIONABLE BRES case
   * (the one created for a business unit B, Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventRespondentEnrolled() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(
            cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK));
    when(caseRepo.findOne(ENROLMENT_CASE_INDIVIDUAL_FK)).thenReturn(cases.get(ENROLMENT_CASE_INDIVIDUAL_FK));
    when(caseRepo.saveAndFlush(any(Case.class))).thenReturn(cases.get(ENROLMENT_CASE_INDIVIDUAL_FK)); //new case

    Category respondentEnrolledCategory = categories.get(CAT_RESPONDENT_ENROLED);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED)).thenReturn(
            respondentEnrolledCategory);

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.RESPONDENT_ENROLED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    Case newCase = caseRepo.findOne(ENROLMENT_CASE_INDIVIDUAL_FK);

    // new mocks

    List<CollectionExerciseDTO> listCollex = Collections.singletonList(makeCollectionExercise());
    when(collectionExerciseSvcClientService.getCollectionExercises(null)).thenReturn(listCollex);
    List<CaseGroup> theseCaseGroups = Collections.singletonList(makeCaseGroup());
    when(caseGroupService.transitionOtherCaseGroups(any())).thenReturn(theseCaseGroups);
    CaseGroup caseGroup = makeCaseGroup();
    when(caseGroupRepo.findOne(ENROLMENT_CASE_INDIVIDUAL_FK)).thenReturn(caseGroup);
    List<Case> c = Collections.singletonList(makeCase());
    when(caseRepo.findByCaseGroupFKOrderByCreatedDateTimeDesc(any())).thenReturn(c);

    // execute tests
    caseService.createCaseEvent(caseEvent, newCase);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(2)).saveAndFlush(argument.capture());

    verify(internetAccessCodeSvcClientService, times(1)).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, times(2)).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));    // action service should be told of the old case state change
    // Now verifying that the old case has been moved to INACTIONABLE and the new case is at REPLACEMENT_INIT
    List<Case> casesList = argument.getAllValues();
    boolean oldCaseStateVerified = false;
    boolean newCaseStateVerified = false;
    for (Case caze : casesList) {
      if (caze.getSampleUnitType().name().equals(respondentEnrolledCategory.getOldCaseSampleUnitTypes())) {
        assertEquals(CaseState.INACTIONABLE, caze.getState());
        oldCaseStateVerified = true;
      }
      if (caze.getSampleUnitType().name().equals(respondentEnrolledCategory.getNewCaseSampleUnitType())) {
        assertEquals(CaseState.REPLACEMENT_INIT, caze.getState());
        newCaseStateVerified = true;
      }
    }
    assertTrue(oldCaseStateVerified);
    assertTrue(newCaseStateVerified);

    verify(notificationPublisher, times(1)).sendNotification(any(CaseNotification.class));
    // no new action to be created
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
  }

  /**
   * Make a test collection exercise
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
    Case existingCase = cases.get(ACTIONABLE_BI_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED)).
            thenReturn(categories.get(CAT_RESPONDENT_ENROLED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.RESPONDENT_ENROLED, ACTIONABLE_BI_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "BI", "B"), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category RESPONDENT_ENROLED versus a Case with the correct sampleUnitType
   * (ie a B) BUT we do not provide a new Case.
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventRespondentEnrolledNewCaseMissing() throws Exception {
    Case existingCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED)).
            thenReturn(categories.get(CAT_RESPONDENT_ENROLED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.RESPONDENT_ENROLED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(MISSING_NEW_CASE_MSG, UUID_FOR_ACTIONABLE_BUSINESS_UNIT_CASE_FK), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.RESPONDENT_ENROLED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category COLLECTION_INSTRUMENT_DOWNLOADED on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventCollectionInstrumentDownloaded() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(cases.get(ACTIONABLE_BI_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED)).
            thenReturn(categories.get(CAT_COLLECTION_INSTRUMENT_DOWNLOADED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
            ACTIONABLE_BI_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class), any(String.class));
  }

  /**
   * We create a CaseEvent with category COLLECTION_INSTRUMENT_DOWNLOADED versus a Case of wrong sampleUnitType
   * (ie NOT a BI)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventCollectionInstrumentDownloadedVersusWrongCaseType() throws Exception {
    Case existingCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED)).
            thenReturn(categories.get(CAT_COLLECTION_INSTRUMENT_DOWNLOADED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "B", "BI"), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category UNSUCCESSFUL_RESPONSE_UPLOAD on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventUnsuccessfulResponseUploaded() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(cases.get(ACTIONABLE_BI_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD)).
            thenReturn(categories.get(CAT_UNSUCCESSFUL_RESPONSE_UPLOAD));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD,
            ACTIONABLE_BI_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category UNSUCCESSFUL_RESPONSE_UPLOAD versus a Case of wrong sampleUnitType
   * (ie NOT a BI)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventUnsuccessfulResponseUploadedVersusWrongCaseType() throws Exception {
    Case existingCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD)).
            thenReturn(categories.get(CAT_UNSUCCESSFUL_RESPONSE_UPLOAD));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "B", "BI"), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category OFFLINE_RESPONSE_PROCESSED on an ACTIONABLE BRES case
   * (the one created for a respondent BI, accountant replying on behalf of Tesco for instance)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventOfflineResponseProcessed() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(cases.get(ACTIONABLE_BI_CASE_FK));
    when(categoryRepo.findOne(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED)).
            thenReturn(categories.get(CAT_OFFLINE_RESPONSE_PROCESSED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED,
            ACTIONABLE_BI_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);
    verify(caseEventRepository, times(1)).save(caseEvent);
    verify(caseRepo, never()).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, never()).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, never()).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));
    verify(notificationPublisher, never()).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, never()).createAndPostAction(any(String.class), any(UUID.class),
            any(String.class));
  }

  /**
   * We create a CaseEvent with category OFFLINE_RESPONSE_PROCESSED versus a Case of wrong sampleUnitType
   * (ie NOT a BI)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventOfflineResponseProcessedVersusWrongCaseType() throws Exception {
    Case existingCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED)).
            thenReturn(categories.get(CAT_OFFLINE_RESPONSE_PROCESSED));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "B", "BI"), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * We create a CaseEvent with category SUCCESSFUL_RESPONSE_UPLOAD versus a Case of wrong sampleUnitType
   * (ie NOT a BI)
   *
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventSuccessfulResponseUploadedVersusWrongCaseType() throws Exception {
    Case existingCase = cases.get(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    when(caseRepo.findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK)).thenReturn(existingCase);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD)).
            thenReturn(categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD,
            ACTIONABLE_BUSINESS_UNIT_CASE_FK);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch(CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(String.format(WRONG_OLD_SAMPLE_UNIT_TYPE_MSG, "B", "BI"), e.getMessage());
    }

    verify(caseRepo).findOne(ACTIONABLE_BUSINESS_UNIT_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(notificationPublisher, times(0)).sendNotification(any(CaseNotification.class));
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    verify(caseEventRepository, times(0)).save(caseEvent);
  }

  /**
   * A SUCCESSFUL_RESPONSE_UPLOAD event transitions an actionable BI case to INACTIONABLE, and all associated BI cases in the case group.
   * The action service is notified of the transition of all BI Cases to stop them receiving communications.
   * @throws Exception if fabricateEvent does
   */
  @Test
  public void testEventSuccessfulResponseUploaded() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(cases.get(ACTIONABLE_BI_CASE_FK));

    Category successfulResponseUploadedCategory = categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD)).thenReturn(
            successfulResponseUploadedCategory);

    when(caseRepo.findByCaseGroupId(null)).thenReturn(Arrays.asList(cases.get(ACTIONABLE_BI_CASE_FK),
            cases.get(ANOTHER_ACTIONABLE_BI_CASE_FK)));

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, ACTIONABLE_BI_CASE_FK);
    caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo, times(1)).findOne(ACTIONABLE_BI_CASE_FK);
    verify(categoryRepo).findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);
    verify(caseEventRepository, times(1)).save(caseEvent);
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(2)).saveAndFlush(argument.capture());

    verify(internetAccessCodeSvcClientService, times(2)).disableIAC(any(String.class));
    verify(caseSvcStateTransitionManager, times(3)).transition(any(CaseState.class),
            any(CaseDTO.CaseEvent.class));    // action service should be told of the old case state change for both cases

    // Now verifying that both cases has been moved to INACTIONABLE
    Case oldCase = argument.getAllValues().get(0);
    assertEquals(CaseState.INACTIONABLE, oldCase.getState());

    Case associatedOldCase = argument.getAllValues().get(1);
    assertEquals(CaseState.INACTIONABLE, associatedOldCase.getState());

    verify(notificationPublisher, times(2)).sendNotification(any(CaseNotification.class));
    // no new action to be created
    verify(actionSvcClientService, times(0)).createAndPostAction(any(String.class),
            any(UUID.class), any(String.class));
  }

  /**
   * caseService.createCaseEvent will be called with invalid state transitions
   * but suppress the exception. This is expected behaviour. This code smells but keeping as is.
   */
  @Test
  public void testGivenCaseGroupChangeIsInvalidWhenTransitionFailGracefully() throws Exception {
    when(caseRepo.findOne(ACTIONABLE_BI_CASE_FK)).thenReturn(cases.get(ACTIONABLE_BI_CASE_FK));
    Category successfulResponseUploadedCategory = categories.get(CAT_SUCCESSFUL_RESPONSE_UPLOAD);
    when(categoryRepo.findOne(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD)).thenReturn(
            successfulResponseUploadedCategory);

    doThrow(new CTPException(CTPException.Fault.BAD_REQUEST))
            .when(caseGroupService).transitionCaseGroupStatus(any(), any(), any());
    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, ACTIONABLE_BI_CASE_FK);

    caseService.createCaseEvent(caseEvent, null);

    verify(caseGroupService).transitionCaseGroupStatus(any(), any(), any());
  }

  /**
   * To mock the behaviour of caseGroupRepo
   * @throws Exception if loadClassFixtures does
   */
  private void mockupCaseGroupRepo() throws Exception {
    List<CaseGroup> caseGroups = FixtureHelper.loadClassFixtures(CaseGroup[].class);
    when(caseGroupRepo.findOne(CASEGROUP_PK))
        .thenReturn(caseGroups.get(CASEGROUP_PK - 1));
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

  /**
   * mock loading data
   */
  private void mockupCaseEventRepo() {
    when(caseEventRepository.save(any(CaseEvent.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  /**
   * mock the collection exercise service
   * 
   * @throws Exception if fixtures loading fails
   */
  private void mockupCollectionExerciseServiceClient() throws Exception {
    List<CollectionExerciseDTO> collectionExerciseDTOs = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    when(collectionExerciseSvcClientService.getCollectionExercise(any())).thenAnswer(
            invocation -> collectionExerciseDTOs.get(0));
  }

  /**
   * mock state transitions
   * @throws CTPException if case state transition errors
   */
  private void mockStateTransitions() throws CTPException {
    when(caseSvcStateTransitionManager.transition(CaseState.ACTIONABLE, CaseDTO.CaseEvent.DISABLED))
            .thenReturn(CaseState.INACTIONABLE);
    when(caseSvcStateTransitionManager.transition(CaseState.ACTIONABLE, CaseDTO.CaseEvent.DEACTIVATED))
            .thenReturn(CaseState.INACTIONABLE);
    when(caseSvcStateTransitionManager.transition(CaseState.ACTIONABLE, ACCOUNT_CREATED))
            .thenReturn(CaseState.ACTIONABLE);
    when(caseSvcStateTransitionManager.transition(CaseState.INACTIONABLE, CaseDTO.CaseEvent.DISABLED))
            .thenReturn(CaseState.INACTIONABLE);
    when(caseSvcStateTransitionManager.transition(CaseState.INACTIONABLE, CaseDTO.CaseEvent.DEACTIVATED))
            .thenReturn(CaseState.INACTIONABLE);
  }

  /**
   * mock loading data
   */
  private void mockAppConfigUse() {
    InternetAccessCodeSvc iacSvc = new InternetAccessCodeSvc();
    iacSvc.setIacPutPath(IAC_SVC_PUT_PATH);
    iacSvc.setIacPostPath(IAC_SVC_POST_PATH);
    when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvc);
  }
}
