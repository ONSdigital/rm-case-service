package uk.gov.ons.ctp.response.casesvc.service.impl;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.MAPPING_ID;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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

  private static final Integer HOUSEHOLD_CASE_ID = 1;
  private static final Integer HOUSEHOLD_CASETYPE_ID = 1;

  private static final Integer HOTEL_CASE_ID = 2;
  private static final Integer HOTEL_CASETYPE_ID = 2;

  private static final Integer NON_EXISTING_PARENT_CASE_ID = 1;

  private static final CategoryDTO.CategoryType CASEEVENT_CATEGORY_QR = CategoryDTO.CategoryType.ONLINE_QUESTIONNAIRE_RESPONSE;
  private static final CategoryDTO.CategoryType CASEEVENT_CATEGORY_CI = CategoryDTO.CategoryType.CLASSIFICATION_INCORRECT;
  private static final CategoryDTO.CategoryType CASEEVENT_CATEGORY_R = CategoryDTO.CategoryType.REFUSAL;
  private static final CategoryDTO.CategoryType CASEEVENT_CATEGORY_U = CategoryDTO.CategoryType.UNDELIVERABLE;
  private static final String CASEEVENT_CREATEDBY = "unit test";
  private static final String CASEEVENT_DESCRIPTION = "a desc";
  private static final String CASEEVENT_SUBCATEGORY = "sub category";

  @Before
  public void init() throws Exception {
    mockStateTransitions();
  }

  /**
   * A test
   */
  @Test
  public void testCreateCaseEventNoParentCase() {
    Mockito.when(caseRepo.findOne(NON_EXISTING_PARENT_CASE_ID)).thenReturn(null);

    Timestamp currentTime = DateTimeUtil.nowUTC();
    CaseEvent caseEvent = new CaseEvent(1, NON_EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CASEEVENT_CATEGORY_QR, CASEEVENT_SUBCATEGORY);

    CaseEvent result = caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(NON_EXISTING_PARENT_CASE_ID);
    assertNull(result);
  }

  /**
   * A test
   *
   * @throws Exception oops
   */
  @Test
  public void testCreateCaseEventCloseHotelWithResponse() throws Exception {

    mockHotelCaseLoadSuccess();
    mockActionPlanMappingLoad();
    mockCaseEventCategoryForQuestionnaireResponseLoadSuccess();
    mockCaseTypeHotelLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(0);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOTEL_CASE_ID);
    verify(categoryRepo).findOne(CASEEVENT_CATEGORY_QR);
    verify(caseRepo).saveAndFlush(any(Case.class));
    verify(caseEventRepository).save(caseEvent);
  }

  /**
   * A test
   *
   * @throws Exception oops
   */
  @Test
  public void testCreateCaseEventCloseHotelWithClassificationIncorrect() throws Exception {

    mockHotelCaseLoadSuccess();
    mockActionPlanMappingLoad();
    mockCaseEventCategoryForClassificationIncorrectSuccess();
    mockCaseTypeHotelLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(1);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOTEL_CASE_ID);
    verify(categoryRepo).findOne(CASEEVENT_CATEGORY_CI);
    verify(caseRepo).saveAndFlush(any(Case.class));
    verify(caseEventRepository).save(caseEvent);
  }

  /**
   * A test
   *
   * @throws Exception oops
   */
  @Test
  public void testCreateCaseEventCloseHotelWithRefusal() throws Exception {

    mockHotelCaseLoadSuccess();
    mockActionPlanMappingLoad();
    mockCaseEventCategoryForRefusalSuccess();
    mockCaseTypeHotelLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(2);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOTEL_CASE_ID);
    verify(categoryRepo).findOne(CASEEVENT_CATEGORY_R);
    verify(caseRepo).saveAndFlush(any(Case.class));
    verify(caseEventRepository).save(caseEvent);
  }

  /**
   * A test
   *
   * @throws Exception oops
   */
  @Test
  public void testCreateCaseEventCloseHotelWithUndeliverable() throws Exception {

    mockHotelCaseLoadSuccess();
    mockActionPlanMappingLoad();
    mockCaseEventCategoryForUndeliverableSuccess();
    mockCaseTypeHotelLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(3);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOTEL_CASE_ID);
    verify(categoryRepo).findOne(CASEEVENT_CATEGORY_U);
    verify(caseRepo).saveAndFlush(any(Case.class));
    verify(caseEventRepository).save(caseEvent);
  }

  /**
   * A test
   *
   * @throws Exception oops
   */
  @Test
  public void testCreateCaseEventCloseHousehold() throws Exception {

    mockHouseholdCaseLoadSuccess();
    mockActionPlanMappingLoad();
    mockCaseEventCategoryForQuestionnaireResponseLoadSuccess();
    mockCaseTypeHouseholdLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(4);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOUSEHOLD_CASE_ID);
    verify(categoryRepo).findOne(CASEEVENT_CATEGORY_QR);
    verify(caseRepo).saveAndFlush(any(Case.class));
    verify(caseEventRepository).save(caseEvent);
  }

  /**
   * mock loading data
   *
   * @return list of mock cases
   * @throws Exception oops
   */
  private List<Case> mockHotelCaseLoadSuccess() throws Exception {
    List<Case> cases = FixtureHelper.loadClassFixtures(Case[].class);
    Case parentCase = cases.get(1);
    Mockito.when(caseRepo.findOne(HOTEL_CASE_ID)).thenReturn(parentCase);
    return cases;
  }

  /**
   * mock loading data
   *
   * @return list of mock cases
   * @throws Exception oops
   */
  private List<Case> mockHouseholdCaseLoadSuccess() throws Exception {
    List<Case> cases = FixtureHelper.loadClassFixtures(Case[].class);
    Case parentCase = cases.get(0);
    Mockito.when(caseRepo.findOne(HOUSEHOLD_CASE_ID)).thenReturn(parentCase);
    return cases;
  }

  /**
   * mock loading data
   *
   * @return list of mock cases
   * @throws Exception oops
   */
  private List<Category> mockCaseEventCategoryForQuestionnaireResponseLoadSuccess() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);
    Category category = categories.get(0);
    Mockito.when(categoryRepo.findOne(CASEEVENT_CATEGORY_QR)).thenReturn(category);
    return categories;
  }

  /**
   * mock loading data
   *
   * @return list of mock cases
   * @throws Exception oops
   */
  private List<Category> mockCaseEventCategoryForClassificationIncorrectSuccess() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);
    Category category = categories.get(1);
    Mockito.when(categoryRepo.findOne(CASEEVENT_CATEGORY_CI)).thenReturn(category);
    return categories;
  }

  /**
   * mock loading data
   *
   * @return list of mock cases
   * @throws Exception oops
   */
  private List<Category> mockCaseEventCategoryForRefusalSuccess() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);
    Category category = categories.get(2);
    Mockito.when(categoryRepo.findOne(CASEEVENT_CATEGORY_R)).thenReturn(category);
    return categories;
  }

  /**
   * mock loading data
   *
   * @return list of mock categories
   * @throws Exception oops
   */
  private List<Category> mockCaseEventCategoryForUndeliverableSuccess() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);
    Category category = categories.get(3);
    Mockito.when(categoryRepo.findOne(CASEEVENT_CATEGORY_U)).thenReturn(category);
    return categories;
  }

  /**
   * mock loading data
   *
   * @return list of mock cases types
   * @throws Exception oops
   */
  private List<CaseType> mockCaseTypeHouseholdLoadSuccess() throws Exception {
    List<CaseType> caseTypes = FixtureHelper.loadClassFixtures(CaseType[].class);
    CaseType caseType = caseTypes.get(0);
    Mockito.when(caseTypeRepo.findOne(HOUSEHOLD_CASETYPE_ID)).thenReturn(caseType);
    return caseTypes;
  }

  /**
   * mock loading data
   *
   * @return list of mock cases types
   * @throws Exception oops
   */
  private List<CaseType> mockCaseTypeHotelLoadSuccess() throws Exception {
    List<CaseType> caseTypes = FixtureHelper.loadClassFixtures(CaseType[].class);
    CaseType caseType = caseTypes.get(1);
    Mockito.when(caseTypeRepo.findOne(HOTEL_CASETYPE_ID)).thenReturn(caseType);
    return caseTypes;
  }

  /**
   * mock loading data
   *
   * @param caseEventIndex which case event to load
   * @return a mock case event
   * @throws Exception oops
   */
  private CaseEvent caseEventFixtureLoad(int caseEventIndex) throws Exception {
    List<CaseEvent> caseEvents = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    CaseEvent caseEvent = caseEvents.get(caseEventIndex);
    return caseEvent;
  }

  /**
   * mock loading data
   *
   * @return a mock case event
   * @throws Exception oops
   */
  private CaseEvent mockCaseEventSave() throws Exception {
    List<CaseEvent> caseEvents = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    CaseEvent caseEvent = caseEvents.get(0);
    Mockito.when(caseEventRepository.save(caseEvent)).thenReturn(caseEvent);
    return caseEvent;
  } 

  /**
   * mock state transitions
   *
   * @throws Exception oops
   */
  private void mockStateTransitions() throws Exception {
    Mockito.when(
        caseSvcStateTransitionManager.transition(CaseState.ACTIONABLE, uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent.DISABLED))
        .thenReturn(CaseState.INACTIONABLE);
    Mockito.when(
        caseSvcStateTransitionManager.transition(CaseState.ACTIONABLE, uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent.DEACTIVATED))
        .thenReturn(CaseState.INACTIONABLE);
  }

  /**
   * mock loading data
   *
   * @return a mock action plan mapping
   * @throws Exception oops
   */
  private ActionPlanMapping mockActionPlanMappingLoad() throws Exception {
    List<ActionPlanMapping> actionPlanMappings = FixtureHelper.loadClassFixtures(ActionPlanMapping[].class);
    ActionPlanMapping actionPlanMapping = actionPlanMappings.get(0);
    Mockito.when(actionPlanMappingRepo.findOne(MAPPING_ID)).thenReturn(actionPlanMapping);
    return actionPlanMapping;
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
