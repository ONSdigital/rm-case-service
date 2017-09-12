package uk.gov.ons.ctp.response.casesvc.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BespokeReturnCallback implements RabbitTemplate.ReturnCallback {

  @Override
  public void returnedMessage(Message var1, int var2, String var3, String var4, String var5){
    log.debug("Returned Message");
    // TODO Do we really want to do the below?
    throw new RuntimeException("");
  }
}
