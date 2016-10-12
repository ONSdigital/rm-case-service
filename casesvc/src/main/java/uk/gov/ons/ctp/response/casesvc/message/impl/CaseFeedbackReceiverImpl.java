package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.message.CaseFeedbackReceiver;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

import javax.inject.Inject;

import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.PAPER_QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryType.ONLINE_QUESTIONNAIRE_RESPONSE;

@Slf4j
@MessageEndpoint
public class CaseFeedbackReceiverImpl implements CaseFeedbackReceiver {

  @Inject
  private CaseService caseService;

  @ServiceActivator(inputChannel = "caseFeedbackTransformed")
  public void process(CaseFeedback caseFeedback) {
    log.debug("entering process with caseFeedback {}", caseFeedback);
    Case existingCase = caseService.findCaseByCaseRef(caseFeedback.getCaseRef());
    log.debug("existingCase is {}", existingCase);
    if (existingCase == null) {
      // TODO store case feedback as unlinked
    } else {
      CaseEvent caseEvent = new CaseEvent();
      caseEvent.setCaseId(existingCase.getCaseId());
      // TODO there is an InboundChannel under domain as well. Do we need both?
      InboundChannel inboundChannel = caseFeedback.getInboundChannel();
      if (inboundChannel == InboundChannel.ONLINE) {
        caseEvent.setCategory(ONLINE_QUESTIONNAIRE_RESPONSE);
      }
      if (inboundChannel == InboundChannel.PAPER) {
        caseEvent.setCategory(PAPER_QUESTIONNAIRE_RESPONSE);
      }
      caseService.createCaseEvent(caseEvent) ;
    }
  }
}
