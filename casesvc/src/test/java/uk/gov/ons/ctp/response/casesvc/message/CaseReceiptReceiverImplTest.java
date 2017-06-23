package uk.gov.ons.ctp.response.casesvc.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.message.impl.CaseReceiptReceiverImpl;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

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

  /**
   * ProcessLinkedOnlineCaseReceipt
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessLinkedOnlineCaseReceipt() throws CTPException, DatatypeConfigurationException {
    Case existingCase = new Case();
    existingCase.setCasePK(LINKED_CASE_ID);
    Mockito.when(caseService.findCaseByCaseRef(LINKED_CASE_REF)).thenReturn(existingCase);

    XMLGregorianCalendar calendar = DateTimeUtil.giveMeCalendarForNow();
    caseReceiptReceiver.process(buildCaseReceipt(LINKED_CASE_REF, InboundChannel.ONLINE, calendar));

    verify(caseService, times(1)).createCaseEvent(eq(buildCaseEvent(LINKED_CASE_ID, ONLINE_QUESTIONNAIRE_RESPONSE)),
            eq(null), eq(new Timestamp(calendar.toGregorianCalendar().getTimeInMillis())));
  }

  /**
   * ProcessLinkedPaperCaseReceipt
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessLinkedPaperCaseReceipt() throws CTPException, DatatypeConfigurationException {
    Case existingCase = new Case();
    existingCase.setCasePK(LINKED_CASE_ID);
    Mockito.when(caseService.findCaseByCaseRef(LINKED_CASE_REF)).thenReturn(existingCase);

    XMLGregorianCalendar calendar = DateTimeUtil.giveMeCalendarForNow();
    caseReceiptReceiver.process(buildCaseReceipt(LINKED_CASE_REF, InboundChannel.PAPER, calendar));

    verify(caseService, times(1)).createCaseEvent(eq(buildCaseEvent(LINKED_CASE_ID, PAPER_QUESTIONNAIRE_RESPONSE)),
            eq(null), eq(new Timestamp(calendar.toGregorianCalendar().getTimeInMillis())));
  }

  /**
   * ProcessUnlinkedOnlineCaseReceipt
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessUnlinkedOnlineCaseReceipt() throws CTPException, DatatypeConfigurationException {
    Mockito.when(caseService.findCaseByCaseRef(UNLINKED_CASE_REF)).thenReturn(null);

    XMLGregorianCalendar calendar = DateTimeUtil.giveMeCalendarForNow();
    CaseReceipt caseReceipt = buildCaseReceipt(UNLINKED_CASE_REF, InboundChannel.ONLINE, calendar);

    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class), any(Case.class));
  }

  /**
   * ProcessUnlinkedPaperCaseReceipt
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessUnlinkedPaperCaseReceipt() throws CTPException, DatatypeConfigurationException {
    Mockito.when(caseService.findCaseByCaseRef(UNLINKED_CASE_REF)).thenReturn(null);

    XMLGregorianCalendar calendar = DateTimeUtil.giveMeCalendarForNow();
    CaseReceipt caseReceipt = buildCaseReceipt(UNLINKED_CASE_REF, InboundChannel.PAPER, calendar);

    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class), any(Case.class));
  }

  /**
   *
   * @param caseRef the caseRef
   * @param inboundChannel the inboundChannel
   * @param xmlGregorianCalendar the xmlGregorianCalendar
   * @return the CaseReceipt
   * @throws DatatypeConfigurationException datatype configuration exception thrown
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
