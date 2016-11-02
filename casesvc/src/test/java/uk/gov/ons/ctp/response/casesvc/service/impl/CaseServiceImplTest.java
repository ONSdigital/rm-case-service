package uk.gov.ons.ctp.response.casesvc.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.InternetAccessCodeSvc;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionPlanMappingRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseTypeRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;

/**
 * Test the CaseServiceImpl
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseServiceImplTest {

  private static final String IAC_SVC_PUT_PATH = "iacs/123";
  private static final String IAC_SVC_POST_PATH = "iacs/123";
  private static final String NEW_CASE_MISSING_EX = "New Case definition missing";
  private static final String CASE_NO_LONGER_ACTIONABLE_EX = "Case is no longer actionable";
  private static final String WRONG_NEW_CASE_TYPE_EX = "New Case definition has incorrect casetype";
  private static final String WRONG_OLD_CASE_TYPE_EX = "Old Case definition has incorrect casetype";

  private static final int CAT_ACCESSIBILITY_MATERIALS = 0;
  private static final int CAT_ACTION_CANCELLATION_COMPLETED = 1;
  private static final int CAT_ACTION_CANCELLATION_CREATED = 2;
  private static final int CAT_ACTION_COMPLETED = 3;
  private static final int CAT_ACTION_CREATED = 4;
  private static final int CAT_ACTION_UPDATED = 5;
  private static final int CAT_ADDRESS_DETAILS_INCORRECT = 6;
  private static final int CAT_CASE_CREATED = 7;
  private static final int CAT_CLASSIFICATION_INCORRECT = 8;
  private static final int CAT_CLOSE_ESCALATION = 9;
  private static final int CAT_FIELD_COMPLAINT_ESCALATED = 10;
  private static final int CAT_FIELD_EMERGENCY_ESCALATED = 11;
  private static final int CAT_GENERAL_COMPLAINT = 12;
  private static final int CAT_GENERAL_COMPLAINT_ESCALATED = 13;
  private static final int CAT_GENERAL_ENQUIRY = 14;
  private static final int CAT_GENERAL_ENQUIRY_ESCALATED = 15;
  private static final int CAT_HOUSEHOLD_PAPER_REQUESTED = 16;
  private static final int CAT_HOUSEHOLD_REPLACEMENT_IAC_REQUESTED = 17;
  private static final int CAT_INCORRECT_ESCALATION = 18;
  private static final int CAT_INDIVIDUAL_PAPER_REQUESTED = 19;
  private static final int CAT_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED = 20;
  private static final int CAT_INDIVIDUAL_RESPONSE_REQUESTED = 21;
  private static final int CAT_MISCELLANEOUS = 22;
  private static final int CAT_ONLINE_QUESTIONNAIRE_RESPONSE = 23;
  private static final int CAT_PAPER_QUESTIONNAIRE_RESPONSE = 24;
  private static final int CAT_PENDING = 25;
  private static final int CAT_REFUSAL = 26;
  private static final int CAT_TECHNICAL_QUERY = 27;
  private static final int CAT_TRANSLATION_ARABIC = 28;
  private static final int CAT_TRANSLATION_BENGALI = 29;
  private static final int CAT_TRANSLATION_CANTONESE = 30;
  private static final int CAT_TRANSLATION_GUJARATI = 31;
  private static final int CAT_TRANSLATION_LITHUANIAN = 32;
  private static final int CAT_TRANSLATION_MANDARIN = 33;
  private static final int CAT_TRANSLATION_POLISH = 34;
  private static final int CAT_TRANSLATION_PORTUGUESE = 35;
  private static final int CAT_TRANSLATION_PUNJABI_GURMUKHI = 36;
  private static final int CAT_TRANSLATION_PUNJABI_SHAHMUKI = 37;
  private static final int CAT_TRANSLATION_SOMALI = 38;
  private static final int CAT_TRANSLATION_SPANISH = 39;
  private static final int CAT_TRANSLATION_TURKISH = 40;
  private static final int CAT_TRANSLATION_URDU = 41;
  private static final int CAT_UNDELIVERABLE = 42;

  private static final Integer ACTIONABLE_HOUSEHOLD_CASE_ID = 1;
  private static final Integer ACTIONABLE_HOUSEHOLD_CASE_ID_1 = 1;
  private static final Integer ACTIONABLE_HOUSEHOLD_CASE_ID_5 = 5;
  private static final Integer INACTIONABLE_HOUSEHOLD_CASE_ID = 2;

  private static final Integer ACTIONABLE_INDIVIDUAL_CASE_ID = 3;
  private static final Integer INACTIONABLE_INDIVIDUAL_CASE_ID = 4;

  private static final Integer NON_EXISTING_PARENT_CASE_ID = 1;

  private static final String CASEEVENT_CREATEDBY = "unit test";
  private static final String CASEEVENT_DESCRIPTION = "a desc";
  private static final String CASEEVENT_SUBCATEGORY = "sub category";
  @Mock
  private CaseRepository caseRepo;

  @Mock
  private CaseEventRepository caseEventRepository;

  @Mock
  private CategoryRepository categoryRepo;

  @Mock
  private ActionPlanMappingRepository actionPlanMappingRepo;

  @Mock
  private CaseTypeRepository caseTypeRepo;

  @Mock
  private AppConfig appConfig;

  @Mock
  private CaseNotificationPublisher notificationPublisher;

  @Mock
  private InternetAccessCodeSvcClientService internetAccessCodeSvcClientService;

  @InjectMocks
  private CaseServiceImpl caseService;

  @Mock
  private StateTransitionManager<CaseDTO.CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;


  @Before
  public void init() throws Exception {
    mockStateTransitions();
    mockupCaseRepo();
    mockupCategoryRepo();
    mockupCaseTypeRepo();
    mockupActionPlanMappingRepo();
    mockAppConfigUse();
    mockupCaseEventRepo();
  }

  /**
   * A test
   */
  @Test
  public void testCreateCaseEventAgainstNonExistentCase() {
    Mockito.when(caseRepo.findOne(NON_EXISTING_PARENT_CASE_ID)).thenReturn(null);

    Timestamp currentTime = DateTimeUtil.nowUTC();
    CaseEvent caseEvent = new CaseEvent(1, NON_EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CategoryDTO.CategoryType.ADDRESS_DETAILS_INCORRECT, CASEEVENT_SUBCATEGORY);

    CaseEvent result = caseService.createCaseEvent(caseEvent, null);

    verify(caseRepo).findOne(NON_EXISTING_PARENT_CASE_ID);
    assertNull(result);
  }

  /**
   * Tries to apply a Translation fulfillment request event against a case
   * already inactionable. Should throw and not save anything
   * 
   * @throws Exception
   */
  @Test
  public void testCreateActionableEventAgainstInactionableCase() throws Exception {

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.TRANSLATION_ARABIC, INACTIONABLE_HOUSEHOLD_CASE_ID);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch (RuntimeException re) {
      assertThat(re.getMessage().startsWith(CASE_NO_LONGER_ACTIONABLE_EX));
      verify(caseRepo).findOne(INACTIONABLE_HOUSEHOLD_CASE_ID);
      verify(categoryRepo).findOne(CategoryDTO.CategoryType.TRANSLATION_ARABIC);
      verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
      verify(caseEventRepository, times(0)).save(caseEvent);
    }
  }

  /**
   * Tries to apply a general event against a case already inactionable. Should
   * allow it.
   * 
   * @throws Exception
   */
  @Test
  public void testCreateNonActionableEventAgainstInactionableCase() throws Exception {

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.GENERAL_COMPLAINT, INACTIONABLE_HOUSEHOLD_CASE_ID);

    caseService.createCaseEvent(caseEvent, null);
    verify(caseRepo).findOne(INACTIONABLE_HOUSEHOLD_CASE_ID);
    verify(categoryRepo).findOne(CategoryDTO.CategoryType.GENERAL_COMPLAINT);
    // there was no change to case - no state transition
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
  }
  // actionSvcClientService.createAndPostAction(actionType,
  // caseEvent.getCaseId(), caseEvent.getCreatedBy());

  /**
   * Tries to apply a response event against an actionable case Should allow it
   * and record response.
   * 
   * @throws Exception
   */
  @Test
  public void testCreatePaperResponseEventAgainstActionableCase() throws Exception {

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE,
        ACTIONABLE_HOUSEHOLD_CASE_ID);

    caseService.createCaseEvent(caseEvent, null);
    verify(caseRepo).findOne(ACTIONABLE_HOUSEHOLD_CASE_ID);
    verify(categoryRepo).findOne(CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(1, caseSaved.getResponses().size());
    assertEquals(CaseDTO.CaseState.INACTIONABLE, caseSaved.getState());

    // IAC should not be disabled for paper responses
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));

    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Tries to apply a response event against an actionable case Should allow it
   * and record response.
   * 
   * @throws Exception
   */
  @Test
  public void testCreateOnlineResponseEventAgainstActionableCase() throws Exception {

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.ONLINE_QUESTIONNAIRE_RESPONSE,
        ACTIONABLE_HOUSEHOLD_CASE_ID);

    caseService.createCaseEvent(caseEvent, null);
    verify(caseRepo).findOne(ACTIONABLE_HOUSEHOLD_CASE_ID);
    verify(categoryRepo).findOne(CategoryDTO.CategoryType.ONLINE_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(1, caseSaved.getResponses().size());
    assertEquals(CaseDTO.CaseState.INACTIONABLE, caseSaved.getState());

    // IAC should be disabled for online responses
    verify(internetAccessCodeSvcClientService, times(1)).disableIAC(any(String.class));

    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Tries to apply a response event against an already inactionable case Should
   * allow it and record response but the state should remain inactionable.
   * 
   * @throws Exception
   */
  @Test
  public void testCreateResponseEventAgainstInActionableCase() throws Exception {

    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE,
        INACTIONABLE_HOUSEHOLD_CASE_ID);

    caseService.createCaseEvent(caseEvent, null);
    verify(caseRepo).findOne(INACTIONABLE_HOUSEHOLD_CASE_ID);
    verify(categoryRepo).findOne(CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE);

    // there was a change to case - state transition and response saved
    ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(1)).save(argument.capture());
    Case caseSaved = argument.getValue();
    assertEquals(2, caseSaved.getResponses().size());
    assertEquals(CaseDTO.CaseState.INACTIONABLE, caseSaved.getState());

    // IAC should not be disabled again!
    verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
    // event was saved
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Bluesky test for creating a replacement household case
   * 
   * @throws Exception
   */
  @Test
  public void testBlueSkyHouseholdIACRequested() throws Exception {
    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.HOUSEHOLD_REPLACEMENT_IAC_REQUESTED,
        ACTIONABLE_HOUSEHOLD_CASE_ID);
    Case oldCase = caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_ID_1);
    Case newCase = caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_ID_5);
    caseService.createCaseEvent(caseEvent, newCase);
    // one of the caseRepo calls is the test loading indCase
    verify(caseRepo, times(2)).findOne(ACTIONABLE_HOUSEHOLD_CASE_ID);
    verify(categoryRepo).findOne(CategoryDTO.CategoryType.HOUSEHOLD_REPLACEMENT_IAC_REQUESTED);
    verify(caseRepo, times(2)).saveAndFlush(any(Case.class));
    verify(internetAccessCodeSvcClientService, times(1)).disableIAC(oldCase.getIac());
    verify(caseEventRepository, times(1)).save(caseEvent);
  }

  /**
   * Tries to apply a Translation fulfillment request event against a case
   * already inactionable. Should throw and not save anything
   * 
   * @throws Exception
   */
  @Test
  public void testIndividualResponseRequestedAgainstIndividualCaseNotAllowed() throws Exception {
    // now kick it off
    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.INDIVIDUAL_RESPONSE_REQUESTED,
        ACTIONABLE_INDIVIDUAL_CASE_ID);
    Case indCase = caseRepo.findOne(ACTIONABLE_INDIVIDUAL_CASE_ID);
    try {
      caseService.createCaseEvent(caseEvent, indCase);
      fail();
    } catch (RuntimeException re) {
      assertThat(re.getMessage().startsWith(WRONG_OLD_CASE_TYPE_EX));
      // one of the caseRepo calls is the test loading indCase
      verify(caseRepo, times(2)).findOne(ACTIONABLE_INDIVIDUAL_CASE_ID);
      verify(categoryRepo).findOne(CategoryDTO.CategoryType.INDIVIDUAL_RESPONSE_REQUESTED);
      verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
      // IAC should not be disabled
      verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
      verify(caseEventRepository, times(0)).save(caseEvent);
    }
  }

  /**
   * Tries to apply a Household event against an Individual Case NOT ALLOWED!.
   * Should throw and not save anything
   * 
   * @throws Exception
   */
  @Test
  public void testHouseholdPaperRequestedAgainstIndividualCaseNotAllowed() throws Exception {
    // now kick it off
    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.HOUSEHOLD_PAPER_REQUESTED,
        ACTIONABLE_INDIVIDUAL_CASE_ID);
    Case indCase = caseRepo.findOne(ACTIONABLE_INDIVIDUAL_CASE_ID);
    try {
      caseService.createCaseEvent(caseEvent, indCase);
      fail();
    } catch (RuntimeException re) {
      assertThat(re.getMessage().startsWith(WRONG_NEW_CASE_TYPE_EX));
      // one of the caseRepo calls is the test loading indCase
      verify(caseRepo, times(2)).findOne(ACTIONABLE_INDIVIDUAL_CASE_ID);
      verify(categoryRepo).findOne(CategoryDTO.CategoryType.HOUSEHOLD_PAPER_REQUESTED);
      verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
      // IAC should not be disabled
      verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
      verify(caseEventRepository, times(0)).save(caseEvent);
    }
  }

  /**
   * Tries to apply a Translation fulfillment request event against a case
   * already inactionable. Should throw and not save anything
   * 
   * @throws Exception
   */
  @Test
  public void testIndividualResponseRequestedAgainstIndividualCaseWithoutNewCase() throws Exception {
    // now kick it off
    CaseEvent caseEvent = fabricateEvent(CategoryDTO.CategoryType.INDIVIDUAL_RESPONSE_REQUESTED,
        ACTIONABLE_INDIVIDUAL_CASE_ID);

    try {
      caseService.createCaseEvent(caseEvent, null);
      fail();
    } catch (RuntimeException re) {
      assertThat(re.getMessage().startsWith(NEW_CASE_MISSING_EX));
      verify(caseRepo).findOne(ACTIONABLE_INDIVIDUAL_CASE_ID);
      verify(categoryRepo).findOne(CategoryDTO.CategoryType.INDIVIDUAL_RESPONSE_REQUESTED);
      verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
      // IAC should not be disabled
      verify(internetAccessCodeSvcClientService, times(0)).disableIAC(any(String.class));
      verify(caseEventRepository, times(0)).save(caseEvent);
    }
  }

  /**
   * mock loading case data
   *
   * @return list of mock cases
   * @throws Exception oops
   */
  private List<Case> mockupCaseRepo() throws Exception {
    List<Case> cases = FixtureHelper.loadClassFixtures(Case[].class);

    Mockito.when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_ID))
        .thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_ID - 1));
    Mockito.when(caseRepo.findOne(INACTIONABLE_HOUSEHOLD_CASE_ID))
        .thenReturn(cases.get(INACTIONABLE_HOUSEHOLD_CASE_ID - 1));
    Mockito.when(caseRepo.findOne(ACTIONABLE_INDIVIDUAL_CASE_ID))
        .thenReturn(cases.get(ACTIONABLE_INDIVIDUAL_CASE_ID - 1));
    Mockito.when(caseRepo.findOne(INACTIONABLE_INDIVIDUAL_CASE_ID))
        .thenReturn(cases.get(INACTIONABLE_INDIVIDUAL_CASE_ID - 1));
    Mockito.when(caseRepo.findOne(ACTIONABLE_HOUSEHOLD_CASE_ID_5))
        .thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_ID_5 - 1));

    Mockito.when(caseRepo.saveAndFlush(any(Case.class)))
        .thenReturn(cases.get(ACTIONABLE_HOUSEHOLD_CASE_ID - 1));
    return cases;
  }

  /**
   * mock loading data
   *
   * @return list of mock categories
   * @throws Exception oops
   */
  private List<Category> mockupCategoryRepo() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);

    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.ACTION_CANCELLATION_COMPLETED))
        .thenReturn(categories.get(CAT_ACTION_CANCELLATION_COMPLETED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.ACTION_CANCELLATION_CREATED))
        .thenReturn(categories.get(CAT_ACTION_CANCELLATION_CREATED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.ACTION_COMPLETED))
        .thenReturn(categories.get(CAT_ACTION_COMPLETED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.ACTION_CREATED))
        .thenReturn(categories.get(CAT_ACTION_CREATED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.ACTION_UPDATED))
        .thenReturn(categories.get(CAT_ACTION_UPDATED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.ADDRESS_DETAILS_INCORRECT))
        .thenReturn(categories.get(CAT_ADDRESS_DETAILS_INCORRECT));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.CASE_CREATED))
        .thenReturn(categories.get(CAT_CASE_CREATED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.CLASSIFICATION_INCORRECT))
        .thenReturn(categories.get(CAT_CLASSIFICATION_INCORRECT));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.GENERAL_COMPLAINT))
        .thenReturn(categories.get(CAT_GENERAL_COMPLAINT));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.GENERAL_ENQUIRY))
        .thenReturn(categories.get(CAT_GENERAL_ENQUIRY));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.MISCELLANEOUS))
        .thenReturn(categories.get(CAT_MISCELLANEOUS));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TECHNICAL_QUERY))
        .thenReturn(categories.get(CAT_TECHNICAL_QUERY));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.ACCESSIBILITY_MATERIALS))
        .thenReturn(categories.get(CAT_ACCESSIBILITY_MATERIALS));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE))
        .thenReturn(categories.get(CAT_PAPER_QUESTIONNAIRE_RESPONSE));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.ONLINE_QUESTIONNAIRE_RESPONSE))
        .thenReturn(categories.get(CAT_ONLINE_QUESTIONNAIRE_RESPONSE));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.UNDELIVERABLE))
        .thenReturn(categories.get(CAT_UNDELIVERABLE));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_SOMALI))
        .thenReturn(categories.get(CAT_TRANSLATION_SOMALI));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_BENGALI))
        .thenReturn(categories.get(CAT_TRANSLATION_BENGALI));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_SPANISH))
        .thenReturn(categories.get(CAT_TRANSLATION_SPANISH));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_POLISH))
        .thenReturn(categories.get(CAT_TRANSLATION_POLISH));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_CANTONESE))
        .thenReturn(categories.get(CAT_TRANSLATION_CANTONESE));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_MANDARIN))
        .thenReturn(categories.get(CAT_TRANSLATION_MANDARIN));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_PUNJABI_SHAHMUKI))
        .thenReturn(categories.get(CAT_TRANSLATION_PUNJABI_SHAHMUKI));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_LITHUANIAN))
        .thenReturn(categories.get(CAT_TRANSLATION_LITHUANIAN));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.FIELD_COMPLAINT_ESCALATED))
        .thenReturn(categories.get(CAT_FIELD_COMPLAINT_ESCALATED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.FIELD_EMERGENCY_ESCALATED))
        .thenReturn(categories.get(CAT_FIELD_EMERGENCY_ESCALATED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.GENERAL_COMPLAINT_ESCALATED))
        .thenReturn(categories.get(CAT_GENERAL_COMPLAINT_ESCALATED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.GENERAL_ENQUIRY_ESCALATED))
        .thenReturn(categories.get(CAT_GENERAL_ENQUIRY_ESCALATED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.INCORRECT_ESCALATION))
        .thenReturn(categories.get(CAT_INCORRECT_ESCALATION));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.PENDING)).thenReturn(categories.get(CAT_PENDING));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.REFUSAL)).thenReturn(categories.get(CAT_REFUSAL));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_PUNJABI_GURMUKHI))
        .thenReturn(categories.get(CAT_TRANSLATION_PUNJABI_GURMUKHI));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_TURKISH))
        .thenReturn(categories.get(CAT_TRANSLATION_TURKISH));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_ARABIC))
        .thenReturn(categories.get(CAT_TRANSLATION_ARABIC));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_PORTUGUESE))
        .thenReturn(categories.get(CAT_TRANSLATION_PORTUGUESE));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_URDU))
        .thenReturn(categories.get(CAT_TRANSLATION_URDU));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.TRANSLATION_GUJARATI))
        .thenReturn(categories.get(CAT_TRANSLATION_GUJARATI));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.CLOSE_ESCALATION))
        .thenReturn(categories.get(CAT_CLOSE_ESCALATION));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.INDIVIDUAL_RESPONSE_REQUESTED))
        .thenReturn(categories.get(CAT_INDIVIDUAL_RESPONSE_REQUESTED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.INDIVIDUAL_REPLACEMENT_IAC_REQUESTED))
        .thenReturn(categories.get(CAT_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.INDIVIDUAL_PAPER_REQUESTED))
        .thenReturn(categories.get(CAT_INDIVIDUAL_PAPER_REQUESTED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.HOUSEHOLD_REPLACEMENT_IAC_REQUESTED))
        .thenReturn(categories.get(CAT_HOUSEHOLD_REPLACEMENT_IAC_REQUESTED));
    Mockito.when(categoryRepo.findOne(CategoryDTO.CategoryType.HOUSEHOLD_PAPER_REQUESTED))
        .thenReturn(categories.get(CAT_HOUSEHOLD_PAPER_REQUESTED));

    return categories;
  }

  /**
   * mock loading data
   *
   * @return list of mock cases types
   * @throws Exception oops
   */
  private List<CaseType> mockupCaseTypeRepo() throws Exception {
    List<CaseType> caseTypes = FixtureHelper.loadClassFixtures(CaseType[].class);
    for (int i = 1; i <= 4; i++) {
      Mockito.when(caseTypeRepo.findOne(i)).thenReturn(caseTypes.get(i - 1));
    }
    return caseTypes;
  }

  /**
   * mock loading data
   *
   * @param caseEventIndex which case event to load
   * @return a mock case event
   * @throws Exception oops
   */
  private CaseEvent fabricateEvent(CategoryDTO.CategoryType categoryType, int caseId) throws Exception {
    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseId(caseId);
    caseEvent.setCategory(categoryType);
    caseEvent.setCreatedBy(CASEEVENT_CREATEDBY);
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    caseEvent.setDescription(CASEEVENT_DESCRIPTION);
    caseEvent.setSubCategory(CASEEVENT_SUBCATEGORY);
    return caseEvent;
  }

  /**
   * mock loading data
   *
   * @return a mock case event
   * @throws Exception oops
   */
  private List<CaseEvent> mockupCaseEventRepo() throws Exception {
    List<CaseEvent> caseEvents = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    Mockito.when(caseEventRepository.save(any(CaseEvent.class))).thenAnswer(new Answer<CaseEvent>() {
      public CaseEvent answer(InvocationOnMock invocation) {
        return (CaseEvent) invocation.getArguments()[0];
      }
    });
    return caseEvents;
  }

  // /**
  // * mock the iac service client
  // *
  // * @throws Exception oops
  // */
  // private void mockupIacServiceClient() throws Exception {
  // Mockito.when(internetAccessCodeSvcClientService.save(any(CaseEvent.class))).thenAnswer(new
  // Answer<CaseEvent>() {
  // public CaseEvent answer(InvocationOnMock invocation) {
  // return (CaseEvent) invocation.getArguments()[0];
  // }
  // });
  // return caseEvents;
  // }

  /**
   * mock state transitions
   *
   * @throws Exception oops
   */
  private void mockStateTransitions() throws Exception {
    Mockito.when(
        caseSvcStateTransitionManager.transition(CaseState.ACTIONABLE,
            uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent.DISABLED))
        .thenReturn(CaseState.INACTIONABLE);
    Mockito.when(
        caseSvcStateTransitionManager.transition(CaseState.ACTIONABLE,
            uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent.DEACTIVATED))
        .thenReturn(CaseState.INACTIONABLE);
    Mockito.when(
        caseSvcStateTransitionManager.transition(CaseState.INACTIONABLE,
            uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent.DISABLED))
        .thenReturn(CaseState.INACTIONABLE);
    Mockito.when(
        caseSvcStateTransitionManager.transition(CaseState.INACTIONABLE,
            uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent.DEACTIVATED))
        .thenReturn(CaseState.INACTIONABLE);
  }

  /**
   * mock loading data
   *
   * @return a mock action plan mapping
   * @throws Exception oops
   */
  private List<ActionPlanMapping> mockupActionPlanMappingRepo() throws Exception {
    List<ActionPlanMapping> actionPlanMappings = FixtureHelper.loadClassFixtures(ActionPlanMapping[].class);
    for (int i = 1; i <= 12; i++) {
      Mockito.when(actionPlanMappingRepo.findOne(i)).thenReturn(actionPlanMappings.get(i - 1));
    }
    return actionPlanMappings;
  }

  /**
   * mock loading data
   *
   * @throws Exception oops
   */
  private void mockAppConfigUse() throws Exception {
    InternetAccessCodeSvc iacSvc = new InternetAccessCodeSvc();
    iacSvc.setIacPutPath(IAC_SVC_PUT_PATH);
    iacSvc.setIacPostPath(IAC_SVC_POST_PATH);
    Mockito.when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvc);
  }
}
