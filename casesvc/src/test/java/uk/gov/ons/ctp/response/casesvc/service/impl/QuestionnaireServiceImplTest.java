package uk.gov.ons.ctp.response.casesvc.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.casesvc.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * Created by Chris Parker on 27/4/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireServiceImplTest {

  @Mock
  private QuestionnaireRepository questionnaireRepo;

  @Mock
  private CaseService caseService;

  @Mock
  private MapperFacade mapperFacade;

  @InjectMocks
  private QuestionnaireServiceImpl questionnaireService;

  private static final Integer EXISTING_PARENT_CASE_ID = 2;
  private static final String CASEEVENT_CATEGORY = "category";
  private static final String CASEEVENT_CREATEDBY = "unit test";
  private static final String CASEEVENT_DESCRIPTION = "a desc";
  private static final String CASEEVENT_SUBCATEGORY = "sub category";

  private static final Integer QUESTIONNAIRE_ID = 1;
  private static final String QUESTIONNAIRE_IAC = "A1B2C3";
  private static final String QUESTIONNAIRE_STATUS = "quest status";
  private static final String QUESTIONNAIRE_QUESTIONSET = "question set";


  /**
   * A test
   */
  @Test
  public void testRecordResponseForQuestionWithCase() {
    Timestamp currentTime = DateTimeUtil.nowUTC();

    Questionnaire questionnaire = new Questionnaire(QUESTIONNAIRE_ID, QUESTIONNAIRE_IAC, EXISTING_PARENT_CASE_ID,
        QUESTIONNAIRE_STATUS, currentTime, currentTime, currentTime, QUESTIONNAIRE_QUESTIONSET);

    Mockito.when(questionnaireRepo.findOne(QUESTIONNAIRE_ID)).thenReturn(questionnaire);

    CaseEvent caseEventResult = new CaseEvent(1, EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CASEEVENT_CATEGORY, CASEEVENT_SUBCATEGORY);

    CaseEventDTO caseEventDTO = new CaseEventDTO();
    caseEventDTO.setCaseId(EXISTING_PARENT_CASE_ID);
    Mockito.when(mapperFacade.map(caseEventDTO, CaseEvent.class)).thenReturn(caseEventResult);

    Mockito.when(caseService.createCaseEvent(caseEventResult)).thenReturn(caseEventResult);

    Questionnaire questionnaire1 = questionnaireService.recordResponse(QUESTIONNAIRE_ID);

    verify(questionnaireRepo).findOne(QUESTIONNAIRE_ID);
    verify(mapperFacade).map(caseEventDTO, CaseEvent.class);
    verify(caseService).createCaseEvent(caseEventResult);

    assertEquals(questionnaire, questionnaire1);
  }

}
