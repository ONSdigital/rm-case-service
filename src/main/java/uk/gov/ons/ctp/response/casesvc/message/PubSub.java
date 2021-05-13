package uk.gov.ons.ctp.response.casesvc.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

@Component
public class PubSub {
  private static final Logger log = LoggerFactory.getLogger(PubSub.class);
  @Autowired AppConfig appConfig;

  private Publisher publisherSupplier(String project, String topic) throws IOException {
    log.info("creating pubsub publish for topic " + topic + " in project " + project);
    log.info("Pubsub emulator host is set to " + System.getenv("PUBSUB_EMULATOR_HOST"));
    TopicName topicName = TopicName.of(project, topic);
    if (StringUtil.isEmpty(System.getenv("PUBSUB_EMULATOR_HOST")))
      return Publisher.newBuilder(topicName).build();
    else return new PubSubEmulator().getEmulatorPublisher(topicName);
  }

  public Publisher caseNotificationPublisher() throws IOException {
    return publisherSupplier(
        appConfig.getGcp().getProject(), appConfig.getGcp().getCaseNotificationTopic());
  }

  public void shutdown() {
    if (StringUtil.isEmpty(System.getenv("PUBSUB_EMULATOR_HOST"))) {
      PubSubEmulator.CHANNEL.shutdown();
    }
  }
}
