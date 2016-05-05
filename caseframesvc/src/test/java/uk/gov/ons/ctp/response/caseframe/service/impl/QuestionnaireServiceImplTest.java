package uk.gov.ons.ctp.response.caseframe.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.caseframe.config.ActionSvc;
import uk.gov.ons.ctp.response.caseframe.config.AppConfig;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Chris Parker on 27/4/2016
 */
//@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireServiceImplTest {

  @Mock
  QuestionnaireRepository questionnaireRepo;

  @Mock
  CaseService caseService;
  
  @Mock
  private MapperFacade mapperFacade;
  
  @InjectMocks
  QuestionnaireServiceImpl questionnaireService;

  private static final Integer EXISTING_PARENT_CASE_ID = 2;
  private static final Integer NON_EXISTING_PARENT_CASE_ID = 1;

  private static final String CASEEVENT_CATEGORY = "category";
  private static final String CASEEVENT_CREATEDBY = "unit test";
  private static final String CASEEVENT_DESCRIPTION = "a desc";
  private static final String CASEEVENT_SUBCATEGORY = "sub category";

  private static final Integer CASE_UPRN = 1;
  private static final Integer CASE_TYPEID = 1;
  private static final String CASE_STATUS = "caseStatus";
  private static final String CASE_CREATEDBY = "unit test";
  private static final String CASE_QUESTIONSET = "case question set";
  private static final Integer CASE_SAMPLEID = 1;
  private static final Integer CASE_ACTIONPLANID = 1;
  private static final Integer CASE_SURVEYID = 1;

  private static final String CATEGORY_NAME = "category name";
  private static final String CATEGORY_DESC = "category desc";
  private static final String CATEGORY_ROLE = "category role";
  private static final String CATEGORY_EMPTYACTIONTYPE = "";
  private static final Boolean CATEGORY_CLOSECASE_FALSE = new Boolean(false);
  private static final Boolean CATEGORY_CLOSECASE_TRUE = new Boolean(true);
  private static final Boolean CATEGORY_MANUAL_FALSE = new Boolean(false);

  private static final Integer QUESTIONNAIRE_ID = 1;
  private static final String QUESTIONNAIRE_IAC = "A1B2C3";
  private static final String QUESTIONNAIRE_STATUS = "quest status";
  private static final String QUESTIONNAIRE_QUESTIONSET = "question set";

  private static final String ACTIONSVC_CANCEL_ACTIONS_PATH = "actions/case/123/cancel";


//  @Test
  public void testRecordResponseForQuestionWithCase() {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    Questionnaire questionnaire = new Questionnaire(QUESTIONNAIRE_ID, QUESTIONNAIRE_IAC, EXISTING_PARENT_CASE_ID,
        QUESTIONNAIRE_STATUS, currentTime, currentTime, currentTime, QUESTIONNAIRE_QUESTIONSET);

    Mockito.when(questionnaireRepo.findOne(QUESTIONNAIRE_ID)).thenReturn(questionnaire);

    Mockito.when(questionnaireRepo.setResponseDatetimeFor(any(Timestamp.class), any(Integer.class))).thenReturn(1);
    
    CaseEvent caseEventResult = new CaseEvent(1, EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CASEEVENT_CATEGORY, CASEEVENT_SUBCATEGORY);

    CaseEventDTO caseEventDTO = new CaseEventDTO();
    caseEventDTO.setCaseId(EXISTING_PARENT_CASE_ID);
    Mockito.when(mapperFacade.map(caseEventDTO, CaseEvent.class)).thenReturn(caseEventResult);

    Mockito.when(caseService.createCaseEvent(caseEventResult)).thenReturn(caseEventResult);
    
    Questionnaire questionnaire1 = questionnaireService.recordResponse(QUESTIONNAIRE_ID);

    verify(questionnaireRepo).findOne(QUESTIONNAIRE_ID);
    verify(questionnaireRepo).setResponseDatetimeFor(any(Timestamp.class), any(Integer.class));
    verify(mapperFacade).map(caseEventDTO, CaseEvent.class);
    verify(caseService).createCaseEvent(caseEventResult);

    assertEquals(questionnaire, questionnaire1);
  }

//  @Test
  public void testRecordResponseForQuestionWithoutCase() {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    Questionnaire questionnaire = new Questionnaire(QUESTIONNAIRE_ID, QUESTIONNAIRE_IAC, EXISTING_PARENT_CASE_ID,
        QUESTIONNAIRE_STATUS, currentTime, currentTime, currentTime, QUESTIONNAIRE_QUESTIONSET);

    Mockito.when(questionnaireRepo.findOne(QUESTIONNAIRE_ID)).thenReturn(questionnaire);
    
    Mockito.when(questionnaireRepo.setResponseDatetimeFor(any(Timestamp.class), any(Integer.class))).thenReturn(1);

    CaseEvent caseEventResult = new CaseEvent(1, EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CASEEVENT_CATEGORY, CASEEVENT_SUBCATEGORY);

    CaseEventDTO caseEventDTO = new CaseEventDTO();
    caseEventDTO.setCaseId(EXISTING_PARENT_CASE_ID);
    Mockito.when(mapperFacade.map(caseEventDTO, CaseEvent.class)).thenReturn(caseEventResult);

    Mockito.when(caseService.createCaseEvent(caseEventResult)).thenReturn(null);
    
    Questionnaire questionnaire1 = questionnaireService.recordResponse(QUESTIONNAIRE_ID);

    verify(questionnaireRepo).findOne(QUESTIONNAIRE_ID);
    verify(questionnaireRepo).setResponseDatetimeFor(any(Timestamp.class), any(Integer.class));
    verify(mapperFacade).map(caseEventDTO, CaseEvent.class);
    verify(caseService).createCaseEvent(caseEventResult);

    assertEquals(null, questionnaire1);
  }
  
}
