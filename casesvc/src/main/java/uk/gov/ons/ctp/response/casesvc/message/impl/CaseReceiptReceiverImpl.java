package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.UnlinkedCaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptReceiver;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.UnlinkedCaseReceiptService;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;

import java.sql.Timestamp;

import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.ONLINE_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

/**
 * The reader of CaseReceipts from queue
 */
@Slf4j
@MessageEndpoint
public class CaseReceiptReceiverImpl implements CaseReceiptReceiver {

  @Inject
  private CaseService caseService;

  @Inject
  private UnlinkedCaseReceiptService unlinkedCaseReceiptService;

  /**
   * To process CaseReceipts read from queue
   * @param caseReceipt to process
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @ServiceActivator(inputChannel = "caseReceiptTransformed")
  public void process(CaseReceipt caseReceipt) {
    log.debug("entering process with caseReceipt {}", caseReceipt);
    String caseRef = caseReceipt.getCaseRef();
    InboundChannel inboundChannel = caseReceipt.getInboundChannel();
    Timestamp responseTimestamp = new Timestamp(caseReceipt.getResponseDateTime().toGregorianCalendar().getTimeInMillis());

    Case existingCase = caseService.findCaseByCaseRef(caseRef);
    log.debug("existingCase is {}", existingCase);

    if (existingCase == null) {
      UnlinkedCaseReceipt unlinkedCaseReceipt = new UnlinkedCaseReceipt();
      unlinkedCaseReceipt.setCaseRef(caseRef);
      unlinkedCaseReceipt.setInboundChannel(
              uk.gov.ons.ctp.response.casesvc.representation.InboundChannel.valueOf(inboundChannel.name()));
      unlinkedCaseReceipt.setResponseDateTime(responseTimestamp);
      unlinkedCaseReceiptService.createUnlinkedCaseReceipt(unlinkedCaseReceipt);
    } else {
      CaseEvent caseEvent = new CaseEvent();
      caseEvent.setCaseId(existingCase.getCaseId());
      caseEvent.setCategory(
              inboundChannel == InboundChannel.ONLINE ? ONLINE_QUESTIONNAIRE_RESPONSE : PAPER_QUESTIONNAIRE_RESPONSE);
      caseEvent.setCreatedBy(SYSTEM);
      caseEvent.setDescription(QUESTIONNAIRE_RESPONSE);
      log.debug("about to invoke the event creation...");
      caseService.createCaseEvent(caseEvent, null, responseTimestamp);
    }
  }
}