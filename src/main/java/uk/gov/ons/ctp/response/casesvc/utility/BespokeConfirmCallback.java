package uk.gov.ons.ctp.response.casesvc.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BespokeConfirmCallback implements RabbitTemplate.ConfirmCallback {

  @Override
  public void confirm(CorrelationData correlationData, boolean b, String string) {
    log.debug("confirmed Message");
  }
}
