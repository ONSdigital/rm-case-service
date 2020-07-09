package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

import java.io.IOException;

/**
 * This class is for filtering the incoming receipts
 * based on whether they pass validation.
 *
 * The validation at this stage is based purely
 * around whether the json can be parsed and
 * unmarshalled to an instance of CaseRecipt.
 *
 * The resulting instance is discarded.
 */
public class ReceiptFilter implements MessageSelector {

    private static final Logger log = LoggerFactory.getLogger(ReceiptFilter.class);

    @Override
    public boolean accept(Message<?> message) {
        ObjectMapper obj = new ObjectMapper();
        try{
            obj.readValue(message.getPayload().toString(), CaseReceipt.class);
        }
        catch (JsonParseException jpe){
            log.error("Failed to parse receipt", jpe);
            return false;
        }
        catch (IOException ioe){
            log.error("Unexpected exception occurred", ioe);
            return false;
        }
        return true;
    }
}
