package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.tools.rabbit.Rabbitmq;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

import java.util.concurrent.BlockingQueue;

@Slf4j
@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseEndpointIT {

  @LocalServerPort
  private int port;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private AppConfig appConfig;

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

//  @Rule
//  public WireMockRule wireMockRule = new WireMockRule(options().port(18002));

  @Test
  public void test() {
    SimpleMessageSender sender = getMessageSender();
    // sending message

    String xml = "";

    sender.sendMessage("collection-inbound-exchange", "Case.CaseDelivery.binding",
                       xml);
  }

  /**
   * Creates a new SimpleMessageSender based on the config in AppConfig
   *
   * @return a new SimpleMessageSender
   */
  private SimpleMessageSender getMessageSender() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageSender(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
  }

}
