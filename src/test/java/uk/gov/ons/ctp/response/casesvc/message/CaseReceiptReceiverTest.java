package uk.gov.ons.ctp.response.casesvc.message;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Before;
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
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.time.TimeHelper;

/** To unit test CaseReceiptReceiver */
@RunWith(MockitoJUnitRunner.class)
public class CaseReceiptReceiverTest {

  private static final String LINKED_CASE_ID = "fa622b71-f158-4d51-82dd-c3417e31e32c";
  private static final String UNLINKED_CASE_ID = "fa622b71-f158-4d51-82dd-c3417e31e32d";
  private static final Integer LINKED_CASE_PK = 1;
  private static final String LINKED_CASE_REF = "123";
  private static final String UNLINKED_CASE_REF = "456";
  private static final String LINKED_PARTY_ID = "fa622b71-f158-4d51-82dd-c3417e31e32e";
  private static final long TIME_IN_MILLISECONDS = 1594646202887L;

  @InjectMocks private CaseReceiptReceiver caseReceiptReceiver;

  @Mock private CaseService caseService;

  @Mock(name = "timeHelper") private TimeHelper timeHelper;

  @Before
  public void setup(){
    Mockito.when(timeHelper.getNowUTC())
            .thenReturn(new Timestamp(TIME_IN_MILLISECONDS));
  }

  /**
   * ProcessLinkedOnlineCaseReceipt
   *
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessLinkedOnlineCaseReceipt()
      throws CTPException, DatatypeConfigurationException {
    Case existingCase = new Case();
    existingCase.setCasePK(LINKED_CASE_PK);
    existingCase.setPartyId(UUID.fromString(LINKED_PARTY_ID));
    Mockito.when(caseService.findCaseById(UUID.fromString(LINKED_CASE_ID)))
        .thenReturn(existingCase);

    caseReceiptReceiver.process(
        buildCaseReceipt(
            LINKED_CASE_ID, LINKED_CASE_REF, InboundChannel.ONLINE, LINKED_PARTY_ID));
    Map<String, String> metadata = new HashMap<>();
    metadata.put("partyId", LINKED_PARTY_ID);
    verify(caseService, times(1))
        .createCaseEvent(
            eq(buildCaseEvent(LINKED_CASE_PK, ONLINE_QUESTIONNAIRE_RESPONSE, metadata)),
            eq(new Timestamp(TIME_IN_MILLISECONDS)));
  }

  /**
   * ProcessLinkedPaperCaseReceipt
   *
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessLinkedPaperCaseReceipt()
      throws CTPException, DatatypeConfigurationException {
    Case existingCase = new Case();
    existingCase.setCasePK(LINKED_CASE_PK);
    existingCase.setPartyId(UUID.fromString(LINKED_PARTY_ID));
    Mockito.when(caseService.findCaseById(UUID.fromString(LINKED_CASE_ID)))
        .thenReturn(existingCase);

    caseReceiptReceiver.process(
        buildCaseReceipt(
            LINKED_CASE_ID, LINKED_CASE_REF, InboundChannel.PAPER, LINKED_PARTY_ID));
    Map<String, String> metadata = new HashMap<>();
    metadata.put("partyId", LINKED_PARTY_ID);

    verify(caseService, times(1))
        .createCaseEvent(
            eq(buildCaseEvent(LINKED_CASE_PK, PAPER_QUESTIONNAIRE_RESPONSE, metadata)),
            eq(new Timestamp(TIME_IN_MILLISECONDS)));
  }

  /**
   * ProcessLinkedPaperCaseReceipt
   *
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessLinkedOfflineCaseReceipt()
      throws CTPException, DatatypeConfigurationException {
    Case existingCase = new Case();
    existingCase.setCasePK(LINKED_CASE_PK);
    existingCase.setPartyId(UUID.fromString(LINKED_PARTY_ID));
    Mockito.when(caseService.findCaseById(UUID.fromString(LINKED_CASE_ID)))
        .thenReturn(existingCase);

    caseReceiptReceiver.process(
        buildCaseReceipt(LINKED_CASE_ID, null, InboundChannel.OFFLINE, LINKED_PARTY_ID));

    Map<String, String> metadata = new HashMap<>();
    metadata.put("partyId", LINKED_PARTY_ID);
    verify(caseService, times(1))
        .createCaseEvent(
            eq(buildCaseEvent(LINKED_CASE_PK, OFFLINE_RESPONSE_PROCESSED, metadata)),
            eq(new Timestamp(TIME_IN_MILLISECONDS)));
  }

  /**
   * ProcessUnlinkedOnlineCaseReceipt
   *
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessUnlinkedOnlineCaseReceipt()
      throws CTPException, DatatypeConfigurationException {
    Mockito.when(caseService.findCaseById(UUID.fromString(LINKED_CASE_ID))).thenReturn(null);

    CaseReceipt caseReceipt =
        buildCaseReceipt(
            UNLINKED_CASE_ID, UNLINKED_CASE_REF, InboundChannel.ONLINE, LINKED_PARTY_ID);

    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class));
  }

  /**
   * ProcessUnlinkedPaperCaseReceipt
   *
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessUnlinkedPaperCaseReceipt()
      throws CTPException, DatatypeConfigurationException {
    Mockito.when(caseService.findCaseById(UUID.fromString(LINKED_CASE_ID))).thenReturn(null);

    CaseReceipt caseReceipt =
        buildCaseReceipt(
            UNLINKED_CASE_ID, UNLINKED_CASE_REF, InboundChannel.PAPER, LINKED_PARTY_ID);

    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class));
  }

  /**
   * ProcessUnlinkedPaperCaseReceipt
   *
   * @throws DatatypeConfigurationException if giveMeCalendarForNow fails
   * @throws CTPException if case state transition errors
   */
  @Test
  public void testProcessUnlinkedOfflineCaseReceipt()
      throws CTPException, DatatypeConfigurationException {
    Mockito.when(caseService.findCaseById(UUID.fromString(LINKED_CASE_ID))).thenReturn(null);

    CaseReceipt caseReceipt =
        buildCaseReceipt(UNLINKED_CASE_ID, null, InboundChannel.PAPER, LINKED_PARTY_ID);

    caseReceiptReceiver.process(caseReceipt);

    verify(caseService, times(0)).createCaseEvent(any(CaseEvent.class));
  }

  /**
   * @param caseId the caseId
   * @param caseRef the caseRef
   * @param inboundChannel the inboundChannel
   * @return the CaseReceipt
   * @throws DatatypeConfigurationException datatype configuration exception thrown
   */
  private CaseReceipt buildCaseReceipt(
      String caseId,
      String caseRef,
      InboundChannel inboundChannel,
      String partyId)
      throws DatatypeConfigurationException {
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseId(caseId);
    caseReceipt.setCaseRef(caseRef);
    caseReceipt.setInboundChannel(inboundChannel);
    caseReceipt.setPartyId(partyId);
    return caseReceipt;
  }

  /**
   * Build a CaseEvent
   *
   * @param casePK the Case Primary Key
   * @param categoryName the name of the category
   * @return the CaseEvent
   */
  private CaseEvent buildCaseEvent(
      int casePK, CategoryDTO.CategoryName categoryName, Map<String, String> metadata) {
    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseFK(casePK);
    caseEvent.setCategory(categoryName);
    caseEvent.setCreatedBy(SYSTEM);
    caseEvent.setDescription(QUESTIONNAIRE_RESPONSE);
    caseEvent.setMetadata(metadata);
    return caseEvent;
  }

}
