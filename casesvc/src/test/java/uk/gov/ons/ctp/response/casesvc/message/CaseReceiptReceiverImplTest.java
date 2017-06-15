package uk.gov.ons.ctp.response.casesvc.message;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.sql.Timestamp;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

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
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.UnlinkedCaseReceiptService;

/**
 * To unit test CaseReceiptReceiverImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseReceiptReceiverImplTest {

  private static final Integer LINKED_CASE_ID = 1;
  private static final String LINKED_CASE_REF = "123";
  private static final String UNLINKED_CASE_REF = "456";

  @InjectMocks
  private CaseReceiptReceiverImpl caseReceiptReceiver;

  @Mock
  private CaseService caseService;

  @Mock
  private UnlinkedCaseReceiptService unlinkedCaseReceiptService;

  /**
   * ProcessLinkedOnlineCaseReceipt
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   */
  @Test
  public void testProcessLinkedOnlineCaseReceipt() throws DatatypeConfigurationException {
    Case existingCase = new Case();
    existingCase.setCasePK(LINKED_CASE_ID);
    Mockito.when(caseService.findCaseByCaseRef(LINKED_CASE_REF)).thenReturn(existingCase);

    XMLGregorianCalendar calendar = DateTimeUtil.giveMeCalendarForNow();
    caseReceiptReceiver.process(buildCaseReceipt(LINKED_CASE_REF, InboundChannel.ONLINE, calendar));

    verify(caseService, times(1)).createCaseEvent(eq(buildCaseEvent(LINKED_CASE_ID, ONLINE_QUESTIONNAIRE_RESPONSE)),
            eq(null), eq(new Timestamp(calendar.toGregorianCalendar().getTimeInMillis())));
    verify(unlinkedCaseReceiptService, times(0)).createUnlinkedCaseReceipt(any(UnlinkedCaseReceipt.class));
  }

  /**
   * ProcessLinkedPaperCaseReceipt
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   */
  @Test
  public void testProcessLinkedPaperCaseReceipt() throws DatatypeConfigurationException {
    Case existingCase = new Case();
    existingCase.setCasePK(LINKED_CASE_ID);
    Mockito.when(caseService.findCaseByCaseRef(LINKED_CASE_REF)).thenReturn(existingCase);

    XMLGregorianCalendar calendar = DateTimeUtil.giveMeCalendarForNow();
    caseReceiptReceiver.process(buildCaseReceipt(LINKED_CASE_REF, InboundChannel.PAPER, calendar));

    verify(caseService, times(1)).createCaseEvent(eq(buildCaseEvent(LINKED_CASE_ID, PAPER_QUESTIONNAIRE_RESPONSE)),
            eq(null), eq(new Timestamp(calendar.toGregorianCalendar().getTimeInMillis())));
    verify(unlinkedCaseReceiptService, times(0)).createUnlinkedCaseReceipt(any(UnlinkedCaseReceipt.class));
  }

  /**
   * ProcessUnlinkedOnlineCaseReceipt
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   */
  @Test
  public void testProcessUnlinkedOnlineCaseReceipt() throws DatatypeConfigurationException {
    Mockito.when(caseService.findCaseByCaseRef(UNLINKED_CASE_REF)).thenReturn(null);

    XMLGregorianCalendar calendar = DateTimeUtil.giveMeCalendarForNow();
    CaseReceipt caseReceipt = buildCaseReceipt(UNLINKED_CASE_REF, InboundChannel.ONLINE, calendar);
    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class), any(Case.class));

    UnlinkedCaseReceipt unlinkedCaseReceipt = new UnlinkedCaseReceipt();
    unlinkedCaseReceipt.setCaseRef(UNLINKED_CASE_REF);
    unlinkedCaseReceipt.setInboundChannel(uk.gov.ons.ctp.response.casesvc.representation.InboundChannel.ONLINE);
    unlinkedCaseReceipt.setResponseDateTime(new Timestamp(calendar.toGregorianCalendar().getTimeInMillis()));
    verify(unlinkedCaseReceiptService, times(1)).createUnlinkedCaseReceipt(eq(unlinkedCaseReceipt));
  }

  /**
   * ProcessUnlinkedPaperCaseReceipt
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   */
  @Test
  public void testProcessUnlinkedPaperCaseReceipt() throws DatatypeConfigurationException {
    Mockito.when(caseService.findCaseByCaseRef(UNLINKED_CASE_REF)).thenReturn(null);

    XMLGregorianCalendar calendar = DateTimeUtil.giveMeCalendarForNow();
    CaseReceipt caseReceipt = buildCaseReceipt(UNLINKED_CASE_REF, InboundChannel.PAPER, calendar);
    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class), any(Case.class));

    UnlinkedCaseReceipt unlinkedCaseReceipt = new UnlinkedCaseReceipt();
    unlinkedCaseReceipt.setCaseRef(UNLINKED_CASE_REF);
    unlinkedCaseReceipt.setInboundChannel(uk.gov.ons.ctp.response.casesvc.representation.InboundChannel.PAPER);
    unlinkedCaseReceipt.setResponseDateTime(new Timestamp(calendar.toGregorianCalendar().getTimeInMillis()));
    verify(unlinkedCaseReceiptService, times(1)).createUnlinkedCaseReceipt(eq(unlinkedCaseReceipt));
  }

  /**
   *
   * @param caseRef the caseRef
   * @param inboundChannel the inboundChannel
   * @param xmlGregorianCalendar the xmlGregorianCalendar
   * @return the CaseReceipt
   * @throws DatatypeConfigurationException
   */
  private CaseReceipt buildCaseReceipt(String caseRef, InboundChannel inboundChannel,
                                       XMLGregorianCalendar xmlGregorianCalendar)
          throws DatatypeConfigurationException {
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(caseRef);
    caseReceipt.setInboundChannel(inboundChannel);
    caseReceipt.setResponseDateTime(xmlGregorianCalendar);
    return caseReceipt;
  }

  /**
   * Build a CaseEvent
   * @param casePK the Case Primary Key
   * @param categoryName the name of the category
   * @return the CaseEvent
   */
  private CaseEvent buildCaseEvent(int casePK, CategoryDTO.CategoryName categoryName) {
    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseFK(casePK);
    caseEvent.setCategory(categoryName);
    caseEvent.setCreatedBy(SYSTEM);
    caseEvent.setDescription(QUESTIONNAIRE_RESPONSE);
    return caseEvent;
  }

}
