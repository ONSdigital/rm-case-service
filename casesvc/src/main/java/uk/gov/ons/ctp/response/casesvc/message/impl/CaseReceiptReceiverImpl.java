package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
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

@Slf4j
@MessageEndpoint
public class CaseReceiptReceiverImpl implements CaseReceiptReceiver {

  @Inject
  private CaseService caseService;

  @Inject
  private UnlinkedCaseReceiptService unlinkedCaseReceiptService;

  @ServiceActivator(inputChannel = "caseReceiptTransformed")
  public void process(CaseReceipt caseReceipt) {
    log.debug("entering process with caseReceipt {}", caseReceipt);
    String receiptCaseRef = caseReceipt.getCaseRef();
    InboundChannel inboundChannel = caseReceipt.getInboundChannel();

    Case existingCase = caseService.findCaseByCaseRef(receiptCaseRef);
    log.debug("existingCase is {}", existingCase);

    if (existingCase == null) {
      UnlinkedCaseReceipt unlinkedCaseReceipt = new UnlinkedCaseReceipt();
      unlinkedCaseReceipt.setCaseRef(receiptCaseRef);
      unlinkedCaseReceipt.setInboundChannel(
              uk.gov.ons.ctp.response.casesvc.domain.model.InboundChannel.valueOf(inboundChannel.name()));
      XMLGregorianCalendar responseDateTime = caseReceipt.getResponseDateTime();
      unlinkedCaseReceipt.setResponseDateTime(new Timestamp(responseDateTime.toGregorianCalendar().getTimeInMillis()));
      unlinkedCaseReceiptService.createUnlinkedCaseReceipt(unlinkedCaseReceipt);
    } else {
      CaseEvent caseEvent = new CaseEvent();
      caseEvent.setCaseId(existingCase.getCaseId());
      caseEvent.setCategory(
              inboundChannel == InboundChannel.ONLINE ? ONLINE_QUESTIONNAIRE_RESPONSE : PAPER_QUESTIONNAIRE_RESPONSE);
      caseService.createCaseEvent(caseEvent, null);
    }
  }
}
