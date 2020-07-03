package uk.gov.ons.ctp.response.casesvc.message;

import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

public class MessageFilter implements MessageSelector {

    @Override
    public boolean accept(Message<?> message) {
        //how do we validate this?
        return true;
    }
}
