package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptReceiver;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

import java.sql.Timestamp;

import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.ONLINE_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.PAPER_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

/**
 * The reader of CaseReceipts from queue
 */
@Slf4j
@MessageEndpoint
public class CaseReceiptReceiverImpl implements CaseReceiptReceiver {

  private final static String EXISTING_CASE_NOT_FOUND = "No existing case found for caseRef %s";

  @Autowired
  private CaseService caseService;

  // TODO CTPA-1340
  /**
   * To process CaseReceipts read from queue
   *
   * @param caseReceipt to process
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, value = "transactionManager")
  @ServiceActivator(inputChannel = "caseReceiptTransformed", adviceChain = "caseReceiptRetryAdvice")
  public void process(CaseReceipt caseReceipt) throws CTPException {
    log.debug("entering process with caseReceipt {}", caseReceipt);
    String caseRef = caseReceipt.getCaseRef().trim();
    InboundChannel inboundChannel = caseReceipt.getInboundChannel();
    Timestamp responseTimestamp = new Timestamp(caseReceipt.getResponseDateTime().toGregorianCalendar()
            .getTimeInMillis());

    Case existingCase = caseService.findCaseByCaseRef(caseRef);
    log.debug("existingCase is {}", existingCase);

    if (existingCase == null) {
      log.error(String.format(EXISTING_CASE_NOT_FOUND, caseRef));
    } else {
      CaseEvent caseEvent = new CaseEvent();
      caseEvent.setCaseFK(existingCase.getCasePK());
      caseEvent.setCategory(
              inboundChannel == InboundChannel.ONLINE ? ONLINE_QUESTIONNAIRE_RESPONSE : PAPER_QUESTIONNAIRE_RESPONSE);
      caseEvent.setCreatedBy(SYSTEM);
      caseEvent.setDescription(QUESTIONNAIRE_RESPONSE);
      log.debug("about to invoke the event creation...");
      caseService.createCaseEvent(caseEvent, null, responseTimestamp);
    }
  }
}
