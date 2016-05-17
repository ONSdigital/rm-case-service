package uk.gov.ons.ctp.response.caseframe.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.caseframe.config.ActionSvc;
import uk.gov.ons.ctp.response.caseframe.config.AppConfig;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseType;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseTypeRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.representation.CaseDTO;
import uk.gov.ons.ctp.response.caseframe.service.ActionSvcClientService;

/**
 * Test the CaseServiceImpl
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseServiceImplTest {

  @Mock
  ActionSvcClientService actionSvcClientService;

  @Mock
  CaseRepository caseRepo;

  @Mock
  QuestionnaireRepository questionnaireRepo;

  @Mock
  CaseEventRepository caseEventRepository;

  @Mock
  CategoryRepository categoryRepo;

  @Mock
  CaseTypeRepository caseTypeRepo;

  @Mock
  AppConfig appConfig;

  @InjectMocks
  CaseServiceImpl caseService;

  private static final Integer HOUSEHOLD_CASE_ID = 1;
  private static final Integer HOUSEHOLD_CASETYPE_ID = 1;

  private static final Integer HOTEL_CASE_ID = 2;
  private static final Integer HOTEL_CASETYPE_ID = 2;

  private static final Integer NON_EXISTING_PARENT_CASE_ID = 1;

  private static final String CASEEVENT_CATEGORY_QR = "QuestionnaireResponse";
  private static final String CASEEVENT_CATEGORY_CI = "Classification Incorrect";
  private static final String CASEEVENT_CATEGORY_R = "Refusal";
  private static final String CASEEVENT_CATEGORY_U = "Undeliverable";
  private static final String CASEEVENT_CREATEDBY = "unit test";
  private static final String CASEEVENT_DESCRIPTION = "a desc";
  private static final String CASEEVENT_SUBCATEGORY = "sub category";

  private static final String ACTIONSVC_CANCEL_ACTIONS_PATH = "actions/case/123/cancel";

  @Test
  public void testCreateCaseEventNoParentCase() {
    Mockito.when(caseRepo.findOne(NON_EXISTING_PARENT_CASE_ID)).thenReturn(null);

    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    CaseEvent caseEvent = new CaseEvent(1, NON_EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CASEEVENT_CATEGORY_QR, CASEEVENT_SUBCATEGORY);

    CaseEvent result = caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(NON_EXISTING_PARENT_CASE_ID);
    assertNull(result);
  }

  @Test
  public void testCreateCaseEventCloseHotelWithResponse() throws Exception {

    mockHotelCaseLoadSuccess();
    mockCaseEventCategoryForQuestionnaireResponseLoadSuccess();
    mockCaseTypeHotelLoadSuccess();
    mockQuestionnairesForHotelCaseLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(0);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOTEL_CASE_ID);
    verify(categoryRepo).findByName(CASEEVENT_CATEGORY_QR);
    verify(caseRepo).setState(HOTEL_CASE_ID, CaseDTO.CaseState.RESPONDED.name());
    verify(questionnaireRepo).findByCaseId(HOTEL_CASE_ID);
    verify(caseEventRepository).save(caseEvent);
    // this is key for this test - we do NOT cancel actions
    verify(actionSvcClientService, times(0)).cancelActions(HOTEL_CASE_ID);
  }

  @Test
  public void testCreateCaseEventCloseHotelWithClassificationIncorrect() throws Exception {

    mockHotelCaseLoadSuccess();
    mockCaseEventCategoryForClassificationIncorrectSuccess();
    mockCaseTypeHotelLoadSuccess();
    mockQuestionnairesForHotelCaseLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(1);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOTEL_CASE_ID);
    verify(categoryRepo).findByName(CASEEVENT_CATEGORY_CI);
    verify(caseRepo).setState(HOTEL_CASE_ID, CaseDTO.CaseState.CLOSED.name());
    verify(questionnaireRepo).findByCaseId(HOTEL_CASE_ID);
    verify(questionnaireRepo).setResponseDatetimeFor(any(Timestamp.class), any(Integer.class));
    verify(caseEventRepository).save(caseEvent);
    verify(actionSvcClientService).cancelActions(HOTEL_CASE_ID);
  }
  
  @Test
  public void testCreateCaseEventCloseHotelWithRefusal() throws Exception {

    mockHotelCaseLoadSuccess();
    mockCaseEventCategoryForRefusalSuccess();
    mockCaseTypeHotelLoadSuccess();
    mockQuestionnairesForHotelCaseLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(2);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOTEL_CASE_ID);
    verify(categoryRepo).findByName(CASEEVENT_CATEGORY_R);
    verify(caseRepo).setState(HOTEL_CASE_ID, CaseDTO.CaseState.CLOSED.name());
    verify(questionnaireRepo).findByCaseId(HOTEL_CASE_ID);
    verify(questionnaireRepo).setResponseDatetimeFor(any(Timestamp.class), any(Integer.class));
    verify(caseEventRepository).save(caseEvent);
    verify(actionSvcClientService).cancelActions(HOTEL_CASE_ID);
  }
  
  @Test
  public void testCreateCaseEventCloseHotelWithUndeliverable() throws Exception {

    mockHotelCaseLoadSuccess();
    mockCaseEventCategoryForUndeliverableSuccess();
    mockCaseTypeHotelLoadSuccess();
    mockQuestionnairesForHotelCaseLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(3);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOTEL_CASE_ID);
    verify(categoryRepo).findByName(CASEEVENT_CATEGORY_U);
    verify(caseRepo).setState(HOTEL_CASE_ID, CaseDTO.CaseState.CLOSED.name());
    verify(questionnaireRepo).findByCaseId(HOTEL_CASE_ID);
    verify(questionnaireRepo).setResponseDatetimeFor(any(Timestamp.class), any(Integer.class));
    verify(caseEventRepository).save(caseEvent);
    verify(actionSvcClientService).cancelActions(HOTEL_CASE_ID);
  }

  @Test
  public void testCreateCaseEventCloseHousehold() throws Exception {

    mockHouseholdCaseLoadSuccess();
    mockCaseEventCategoryForQuestionnaireResponseLoadSuccess();
    mockCaseTypeHouseholdLoadSuccess();
    mockQuestionnairesForHouseholdCaseLoadSuccess();
    mockCaseEventSave();
    mockAppConfigUse();

    // now kick it off
    CaseEvent caseEvent = caseEventFixtureLoad(4);
    caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(HOUSEHOLD_CASE_ID);
    verify(categoryRepo).findByName(CASEEVENT_CATEGORY_QR);
    verify(caseRepo).setState(HOUSEHOLD_CASE_ID, CaseDTO.CaseState.CLOSED.name());
    verify(questionnaireRepo).findByCaseId(HOUSEHOLD_CASE_ID);
    verify(questionnaireRepo).setResponseDatetimeFor(any(Timestamp.class), any(Integer.class));
    verify(caseEventRepository).save(caseEvent);
    verify(actionSvcClientService).cancelActions(HOUSEHOLD_CASE_ID);
  }

  private List<Case> mockHotelCaseLoadSuccess() throws Exception {
    List<Case> cases = FixtureHelper.loadClassFixtures(Case[].class);
    Case parentCase = cases.get(1);
    Mockito.when(caseRepo.findOne(HOTEL_CASE_ID)).thenReturn(parentCase);
    return cases;
  }

  private List<Case> mockHouseholdCaseLoadSuccess() throws Exception {
    List<Case> cases = FixtureHelper.loadClassFixtures(Case[].class);
    Case parentCase = cases.get(0);
    Mockito.when(caseRepo.findOne(HOUSEHOLD_CASE_ID)).thenReturn(parentCase);
    return cases;
  } 
  private List<Category> mockCaseEventCategoryForQuestionnaireResponseLoadSuccess() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);
    Category category = categories.get(0);
    Mockito.when(categoryRepo.findByName(CASEEVENT_CATEGORY_QR)).thenReturn(category);
    return categories;
  }

  private List<Category> mockCaseEventCategoryForClassificationIncorrectSuccess() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);
    Category category = categories.get(1);
    Mockito.when(categoryRepo.findByName(CASEEVENT_CATEGORY_CI)).thenReturn(category);
    return categories;
  }
  private List<Category> mockCaseEventCategoryForRefusalSuccess() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);
    Category category = categories.get(2);
    Mockito.when(categoryRepo.findByName(CASEEVENT_CATEGORY_R)).thenReturn(category);
    return categories;
  }  private List<Category> mockCaseEventCategoryForUndeliverableSuccess() throws Exception {
    List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);
    Category category = categories.get(3);
    Mockito.when(categoryRepo.findByName(CASEEVENT_CATEGORY_U)).thenReturn(category);
    return categories;
  }
  private List<CaseType> mockCaseTypeHouseholdLoadSuccess() throws Exception {
    List<CaseType> caseTypes = FixtureHelper.loadClassFixtures(CaseType[].class);
    CaseType caseType = caseTypes.get(0);
    Mockito.when(caseTypeRepo.findOne(HOUSEHOLD_CASETYPE_ID)).thenReturn(caseType);
    return caseTypes;
  }

  private List<CaseType> mockCaseTypeHotelLoadSuccess() throws Exception {
    List<CaseType> caseTypes = FixtureHelper.loadClassFixtures(CaseType[].class);
    CaseType caseType = caseTypes.get(1);
    Mockito.when(caseTypeRepo.findOne(HOTEL_CASETYPE_ID)).thenReturn(caseType);
    return caseTypes;
  }

  private List<Questionnaire> mockQuestionnairesForHotelCaseLoadSuccess() throws Exception {
    List<Questionnaire> questionnaires = FixtureHelper.loadClassFixtures(Questionnaire[].class);
    Questionnaire questionnaire = questionnaires.get(0);
    List<Questionnaire> associatedQuestionnaires = new ArrayList<>();
    associatedQuestionnaires.add(questionnaire);
    Mockito.when(questionnaireRepo.findByCaseId(HOTEL_CASE_ID)).thenReturn(associatedQuestionnaires);
    return questionnaires;
  }

  private List<Questionnaire> mockQuestionnairesForHouseholdCaseLoadSuccess() throws Exception {
    List<Questionnaire> questionnaires = FixtureHelper.loadClassFixtures(Questionnaire[].class);
    Questionnaire questionnaire = questionnaires.get(0);
    List<Questionnaire> associatedQuestionnaires = new ArrayList<>();
    associatedQuestionnaires.add(questionnaire);
    Mockito.when(questionnaireRepo.findByCaseId(HOUSEHOLD_CASE_ID)).thenReturn(associatedQuestionnaires);
    return questionnaires;
  }

  private CaseEvent caseEventFixtureLoad(int caseEventIndex) throws Exception {
    List<CaseEvent> caseEvents = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    CaseEvent caseEvent = caseEvents.get(caseEventIndex);
    return caseEvent;
  }

  private CaseEvent mockCaseEventSave() throws Exception {
    List<CaseEvent> caseEvents = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    CaseEvent caseEvent = caseEvents.get(0);
    Mockito.when(caseEventRepository.save(caseEvent)).thenReturn(caseEvent);
    return caseEvent;
  }

  private void mockAppConfigUse() throws Exception {
    ActionSvc actionSvc = new ActionSvc();
    actionSvc.setCancelActionsPath(ACTIONSVC_CANCEL_ACTIONS_PATH);
    Mockito.when(appConfig.getActionSvc()).thenReturn(actionSvc);
  }
}
