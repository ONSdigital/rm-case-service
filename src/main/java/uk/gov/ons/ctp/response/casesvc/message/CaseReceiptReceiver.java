package uk.gov.ons.ctp.response.casesvc.message;

import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;

/** The reader of CaseReceipts from queue */
@MessageEndpoint
public class CaseReceiptReceiver {
  private static final Logger log = LoggerFactory.getLogger(CaseReceiptReceiver.class);

  private static final String EXISTING_CASE_NOT_FOUND = "No existing case found";

  @Autowired private CaseService caseService;

  @Autowired private DateTimeUtil dateTimeUtil;

  /**
   * To process CaseReceipts read from queue
   *
   * @param caseReceipt to process
   * @throws CTPException CTPException
   */
  @Transactional(propagation = Propagation.REQUIRED, value = "transactionManager")
  @ServiceActivator(inputChannel = "caseReceiptTransformed", adviceChain = "caseReceiptRetryAdvice")
  public void process(CaseReceipt caseReceipt) throws CTPException {
    log.with("case_receipt", caseReceipt).debug("entering process with caseReceipt");
    UUID caseId = UUID.fromString(caseReceipt.getCaseId());
    Timestamp responseTimestamp = dateTimeUtil.getNowUTC();

    Case existingCase = caseService.findCaseById(caseId);
    log.with("existing_case", existingCase).info("Found existing case");

    if (existingCase == null) {
      log.with("case_id", caseId).error(EXISTING_CASE_NOT_FOUND);
    } else {
      CaseEvent caseEvent = new CaseEvent();
      String partyId = caseReceipt.getPartyId();
      if (partyId != null && !partyId.isEmpty()) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("partyId", partyId);
        caseEvent.setMetadata(metadata);
      }
      caseEvent.setCaseFK(existingCase.getCasePK());
      caseEvent.setCategory(OFFLINE_RESPONSE_PROCESSED);
      log.with("case_event", caseEvent.getCategory()).info("New case event");
      caseEvent.setCreatedBy(SYSTEM);
      caseEvent.setDescription(QUESTIONNAIRE_RESPONSE);
      log.info("about to invoke the event creation...");
      caseService.createCaseEvent(caseEvent, responseTimestamp);
    }
  }
}
