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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * To unit test CaseReceiptReceiverImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseReceiptReceiverImplTest {

  private static final String EXISTING_CASE_REF = "123";

  @InjectMocks
  CaseReceiptReceiverImpl caseFeedbackReceiver;

  @Mock
  private CaseService caseService;

  @Test
  public void testProcessLinkedCaseFeedback() {
    Case existingCase = new Case();
    existingCase.setCaseId(123);
    Mockito.when(caseService.findCaseByCaseRef(EXISTING_CASE_REF)).thenReturn(existingCase);

    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(EXISTING_CASE_REF);
    caseReceipt.setInboundChannel(InboundChannel.ONLINE);
    caseFeedbackReceiver.process(caseReceipt);

    // TODO be more specific below
    verify(caseService, times(1)).createCaseEvent(any(CaseEvent.class));
  }

  // TODO test other scenarios
}
