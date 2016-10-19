package uk.gov.ons.ctp.response.casesvc.message;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.ONLINE_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE;

import java.sql.Timestamp;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.UnlinkedCaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.message.impl.CaseReceiptReceiverImpl;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.UnlinkedCaseReceiptService;

/**
 * To unit test CaseReceiptReceiverImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseReceiptReceiverImplTest {

  private static final String EXISTING_CASE_REF = "123";
  private static final String NON_EXISTING_CASE_REF = "456";

  @InjectMocks
  CaseReceiptReceiverImpl caseReceiptReceiver;

  @Mock
  private CaseService caseService;

  @Mock
  private UnlinkedCaseReceiptService unlinkedCaseReceiptService;

  @Test
  public void testProcessLinkedOnlineCaseReceipt() throws DatatypeConfigurationException {
    Case existingCase = new Case();
    Integer caseId = new Integer(EXISTING_CASE_REF);
    existingCase.setCaseId(caseId);
    Mockito.when(caseService.findCaseByCaseRef(EXISTING_CASE_REF)).thenReturn(existingCase);

    caseReceiptReceiver.process(buildCaseReceipt(EXISTING_CASE_REF, InboundChannel.ONLINE));

    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseId(caseId);
    caseEvent.setCategory(ONLINE_QUESTIONNAIRE_RESPONSE);
    verify(caseService, times(1)).createCaseEvent(eq(caseEvent), any(Case.class));

    verify(unlinkedCaseReceiptService, times(0)).createUnlinkedCaseReceipt(any(UnlinkedCaseReceipt.class));
  }

  @Test
  public void testProcessLinkedPaperCaseReceipt() throws DatatypeConfigurationException {
    Case existingCase = new Case();
    Integer caseId = new Integer(EXISTING_CASE_REF);
    existingCase.setCaseId(caseId);
    Mockito.when(caseService.findCaseByCaseRef(EXISTING_CASE_REF)).thenReturn(existingCase);

    caseReceiptReceiver.process(buildCaseReceipt(EXISTING_CASE_REF, InboundChannel.PAPER));

    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseId(caseId);
    caseEvent.setCategory(PAPER_QUESTIONNAIRE_RESPONSE);
    verify(caseService, times(1)).createCaseEvent(eq(caseEvent), any(Case.class));

    verify(unlinkedCaseReceiptService, times(0)).createUnlinkedCaseReceipt(any(UnlinkedCaseReceipt.class));
  }

  @Test
  public void testProcessUnlinkedOnlineCaseReceipt() throws DatatypeConfigurationException {
    Mockito.when(caseService.findCaseByCaseRef(NON_EXISTING_CASE_REF)).thenReturn(null);

    CaseReceipt caseReceipt = buildCaseReceipt(NON_EXISTING_CASE_REF, InboundChannel.ONLINE);
    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class), any(Case.class));

    UnlinkedCaseReceipt unlinkedCaseReceipt = new UnlinkedCaseReceipt();
    unlinkedCaseReceipt.setCaseRef(NON_EXISTING_CASE_REF);
    unlinkedCaseReceipt.setInboundChannel(uk.gov.ons.ctp.response.casesvc.domain.model.InboundChannel.ONLINE);
    unlinkedCaseReceipt.setResponseDateTime(new Timestamp(caseReceipt.getResponseDateTime().toGregorianCalendar().getTimeInMillis()));
    verify(unlinkedCaseReceiptService, times(1)).createUnlinkedCaseReceipt(eq(unlinkedCaseReceipt));
  }

  @Test
  public void testProcessUnlinkedPaperCaseReceipt() throws DatatypeConfigurationException {
    Mockito.when(caseService.findCaseByCaseRef(NON_EXISTING_CASE_REF)).thenReturn(null);

    CaseReceipt caseReceipt = buildCaseReceipt(NON_EXISTING_CASE_REF, InboundChannel.PAPER);
    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class), any(Case.class));

    UnlinkedCaseReceipt unlinkedCaseReceipt = new UnlinkedCaseReceipt();
    unlinkedCaseReceipt.setCaseRef(NON_EXISTING_CASE_REF);
    unlinkedCaseReceipt.setInboundChannel(uk.gov.ons.ctp.response.casesvc.domain.model.InboundChannel.PAPER);
    unlinkedCaseReceipt.setResponseDateTime(new Timestamp(caseReceipt.getResponseDateTime().toGregorianCalendar().getTimeInMillis()));
    verify(unlinkedCaseReceiptService, times(1)).createUnlinkedCaseReceipt(eq(unlinkedCaseReceipt));
  }

  private CaseReceipt buildCaseReceipt(String caseRef, InboundChannel inboundChannel)
          throws DatatypeConfigurationException {
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(caseRef);
    caseReceipt.setInboundChannel(inboundChannel);
    caseReceipt.setResponseDateTime(DateTimeUtil.giveMeCalendarForNow());
    return caseReceipt;
  }
}
