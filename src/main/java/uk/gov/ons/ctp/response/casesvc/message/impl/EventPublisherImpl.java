package uk.gov.ons.ctp.response.casesvc.message.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.message.EventPublisher;

@MessageEndpoint
@Slf4j
public class EventPublisherImpl implements EventPublisher{


  @Qualifier("amqpTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Override
  public void publishEvent(String event) {
    log.debug("Publish Event", event);
    rabbitTemplate.convertAndSend(event);
  }
}