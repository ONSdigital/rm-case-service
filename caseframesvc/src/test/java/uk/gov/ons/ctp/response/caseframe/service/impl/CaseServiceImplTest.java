package uk.gov.ons.ctp.response.caseframe.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;

import java.sql.Timestamp;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

/**
 * Created by philippe.brossier on 3/31/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseServiceImplTest {

  @Mock
  CaseRepository caseRepo;

  @Mock
  QuestionnaireRepository questionnaireRepo;

  @Mock
  CaseEventRepository caseEventRepository;

  @Mock
  CategoryRepository categoryRepo;

  @InjectMocks
  CaseServiceImpl caseService;

  private static final Integer NON_EXISTING_PARENT_CASE_ID = 1;

  @Test
  public void testCreateCaseEventNoParentCase() {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    CaseEvent caseEvent = new CaseEvent(1, NON_EXISTING_PARENT_CASE_ID, "a desc", "created by ", currentTime, "cat", "subcat");
    Mockito.when(caseRepo.findOne(NON_EXISTING_PARENT_CASE_ID)).thenReturn(null);

    CaseEvent result = caseService.createCaseEvent(caseEvent);
    verify(caseRepo).findOne(NON_EXISTING_PARENT_CASE_ID);
    assertNull(result);
  }
}
