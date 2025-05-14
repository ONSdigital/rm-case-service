package uk.gov.ons.ctp.response.casesvc.message;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import org.junit.Test;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

public class ReceiptFilterTest {

  @Test
  public void willValidateJson() throws JsonParseException, JsonMappingException, IOException {
    String payload =
        "{\"caseId\":\"34597808-ec88-4e93-af2f-228e33ff7946\",\"partyId\":\"34597808-ec88-4e93-af2f-228e33ff7946\",\"caseRef\":\"12343543\"}";

    Message<?> m = MessageBuilder, kvPayload(payload.getBytes()).build();

    ReceiptFilter filter = new ReceiptFilter();
    assertTrue(filter.accept(m));
  }
}
