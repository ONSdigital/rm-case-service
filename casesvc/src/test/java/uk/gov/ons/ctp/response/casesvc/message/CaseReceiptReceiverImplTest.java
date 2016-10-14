package uk.gov.ons.ctp.response.casesvc.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.message.impl.CaseReceiptReceiverImpl;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.ONLINE_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE;

/**
 * To unit test CaseReceiptReceiverImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseReceiptReceiverImplTest {

  private static final String EXISTING_CASE_REF = "123";

  @InjectMocks
  CaseReceiptReceiverImpl caseReceiptReceiver;

  @Mock
  private CaseService caseService;

  @Test
  public void testProcessLinkedOnlineCaseReceipt() {
    Case existingCase = new Case();
    Integer caseId = new Integer(EXISTING_CASE_REF);
    existingCase.setCaseId(caseId);
    Mockito.when(caseService.findCaseByCaseRef(EXISTING_CASE_REF)).thenReturn(existingCase);

    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(EXISTING_CASE_REF);
    caseReceipt.setInboundChannel(InboundChannel.ONLINE);
    caseReceiptReceiver.process(caseReceipt);

    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseId(caseId);
    caseEvent.setCategory(ONLINE_QUESTIONNAIRE_RESPONSE);
    verify(caseService, times(1)).createCaseEvent(eq(caseEvent));
  }

  @Test
  public void testProcessLinkedPaperCaseReceipt() {
    Case existingCase = new Case();
    Integer caseId = new Integer(EXISTING_CASE_REF);
    existingCase.setCaseId(caseId);
    Mockito.when(caseService.findCaseByCaseRef(EXISTING_CASE_REF)).thenReturn(existingCase);

    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(EXISTING_CASE_REF);
    caseReceipt.setInboundChannel(InboundChannel.PAPER);
    caseReceiptReceiver.process(caseReceipt);

    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseId(caseId);
    caseEvent.setCategory(PAPER_QUESTIONNAIRE_RESPONSE);
    verify(caseService, times(1)).createCaseEvent(eq(caseEvent));
  }
  // TODO test other scenarios
}
