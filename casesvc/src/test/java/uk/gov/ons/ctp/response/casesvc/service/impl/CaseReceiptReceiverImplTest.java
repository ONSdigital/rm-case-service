package uk.gov.ons.ctp.response.casesvc.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import java.sql.Timestamp;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.casesvc.message.impl.CaseReceiptReceiverImpl.CREATED_BY_SYSTEM;
import static uk.gov.ons.ctp.response.casesvc.message.impl.CaseReceiptReceiverImpl.QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE;

/**
 * Test the CaseReceiptReceiverImpl
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseReceiptReceiverImplTest {

  private static final Integer LINKED_CASE_ID = 1;
  private static final String UNLINKED_CASE_REF = "123";
  private static final String LINKED_CASE_REF = "456";

  @InjectMocks
  private CaseReceiptReceiverImpl caseReceiptReceiver;

  @Mock
  private CaseService caseService;

  @Mock
  private UnlinkedCaseReceiptService unlinkedCaseReceiptService;

  @Test
  public void testProcessUnlinkedPaperResponse() throws DatatypeConfigurationException {
    when(caseService.findCaseByCaseRef(any(String.class))).thenReturn(null);

    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(UNLINKED_CASE_REF);
    InboundChannel inboundChannel = InboundChannel.PAPER;
    caseReceipt.setInboundChannel(inboundChannel);
    XMLGregorianCalendar responseDateTime = DateTimeUtil.giveMeCalendarForNow();
    caseReceipt.setResponseDateTime(responseDateTime);
    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class), any(Case.class));
    UnlinkedCaseReceipt unlinkedCaseReceipt = new UnlinkedCaseReceipt();
    unlinkedCaseReceipt.setCaseRef(UNLINKED_CASE_REF);
    unlinkedCaseReceipt.setInboundChannel(uk.gov.ons.ctp.response.casesvc.domain.model.InboundChannel.valueOf(inboundChannel.name()));
    unlinkedCaseReceipt.setResponseDateTime(new Timestamp(responseDateTime.toGregorianCalendar().getTimeInMillis()));
    verify(unlinkedCaseReceiptService, times(1)).createUnlinkedCaseReceipt(eq(unlinkedCaseReceipt));
  }

  @Test
  public void testProcessLinkedPaperResponse() throws DatatypeConfigurationException{
    Case existingCase = new Case();
    existingCase.setCaseId(LINKED_CASE_ID);
    when(caseService.findCaseByCaseRef(any(String.class))).thenReturn(existingCase);

    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(LINKED_CASE_REF);
    InboundChannel inboundChannel = InboundChannel.PAPER;
    caseReceipt.setInboundChannel(inboundChannel);
    XMLGregorianCalendar responseDateTime = DateTimeUtil.giveMeCalendarForNow();
    caseReceipt.setResponseDateTime(responseDateTime);
    caseReceiptReceiver.process(caseReceipt);

    verify(unlinkedCaseReceiptService, times(0)).createUnlinkedCaseReceipt(any(UnlinkedCaseReceipt.class));
    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseId(LINKED_CASE_ID);
    caseEvent.setCategory(PAPER_QUESTIONNAIRE_RESPONSE);
    caseEvent.setCreatedBy(CREATED_BY_SYSTEM);
    caseEvent.setDescription(QUESTIONNAIRE_RESPONSE);
    verify(caseService, times(1)).createCaseEvent(eq(caseEvent), eq(null));
  }

}
