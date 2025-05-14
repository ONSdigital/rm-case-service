package uk.gov.ons.ctp.response.casesvc.message;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.QUESTIONNAIRE_RESPONSE;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
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
  public void process(CaseReceipt caseReceipt) throws CTPException {
    log.info("Processing case receipt", kv("case_receipt", caseReceipt));
    UUID caseId = UUID.fromString(caseReceipt.getCaseId());
    Timestamp responseTimestamp = dateTimeUtil.getNowUTC();

    Case existingCase = caseService.findCaseById(caseId);
    log.debug("Found existing case", kv("existing_case", existingCase));

    if (existingCase == null) {
      log.error(EXISTING_CASE_NOT_FOUND, kv("case_id", caseId));
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
      log.info("New case event", kv("case_event", caseEvent.getCategory()));
      caseEvent.setCreatedBy(SYSTEM);
      caseEvent.setDescription(QUESTIONNAIRE_RESPONSE);
      log.debug("about to invoke the event creation...");
      caseService.createCaseEvent(caseEvent, responseTimestamp);
    }
    log.info("Case receipt processing complete", kv("case_receipt", caseReceipt));
  }
}
