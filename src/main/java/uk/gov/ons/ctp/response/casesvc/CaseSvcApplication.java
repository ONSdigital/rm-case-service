package uk.gov.ons.ctp.response.casesvc;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.godaddy.logging.LoggingConfigs;
import java.time.Clock;
import javax.annotation.PostConstruct;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.PubSubCaseReceiptSubscriptionAdapter;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.state.CaseSvcStateTransitionManagerFactory;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedInstanceManager;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedInstanceManagerRedissonImpl;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedLatchManager;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedLatchManagerRedissonImpl;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedListManager;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedListManagerRedissonImpl;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedLockManagerRedissonImpl;
import uk.gov.ons.ctp.response.lib.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.response.lib.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtility;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;

/** The 'main' entry point for the CaseSvc SpringBoot Application. */
@SpringBootApplication
@EnableTransactionManagement
@IntegrationComponentScan
@ComponentScan(basePackages = {"uk.gov.ons.ctp.response"})
@EnableJpaRepositories(basePackages = {"uk.gov.ons.ctp.response"})
@EntityScan("uk.gov.ons.ctp.response")
@EnableAsync
@EnableCaching
@EnableScheduling
@ImportResource("springintegration/main.xml")
public class CaseSvcApplication {

  public static final String CASE_DISTRIBUTION_LIST = "casesvc.case.distribution";
  public static final String REPORT_EXECUTION_LOCK = "casesvc.report.execution";

  private AppConfig appConfig;
  private StateTransitionManagerFactory caseSvcStateTransitionManagerFactory;

  private static final Logger log = LoggerFactory.getLogger(CaseSvcApplication.class);

  /** Constructor for CaseSvcApplication */
  @Autowired
  public CaseSvcApplication(
      final AppConfig appConfig,
      final StateTransitionManagerFactory caseSvcStateTransitionManagerFactory) {
    this.appConfig = appConfig;
    this.caseSvcStateTransitionManagerFactory = caseSvcStateTransitionManagerFactory;
  }

  /**
   * The main entry point for this applicaion.
   *
   * @param args runtime command line args
   */
  public static void main(final String[] args) {

    SpringApplication.run(CaseSvcApplication.class, args);
  }

  @PostConstruct
  public void initJsonLogging() {
    if (appConfig.getLogging().isUseJson()) {
      LoggingConfigs.setCurrent(LoggingConfigs.getCurrent().useJson());
    }
  }

  /**
   * Bean to allow application to make controlled state transitions of Cases
   *
   * @return the state transition manager specifically for Cases
   */
  @Bean
  public StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager() {
    return caseSvcStateTransitionManagerFactory.getStateTransitionManager(
        CaseSvcStateTransitionManagerFactory.CASE_ENTITY);
  }

  /**
   * Bean to allow application to make controlled state transitions of CaseGroupStatus
   *
   * @return the state transition manager specifically for CaseGroup
   */
  @Bean
  public StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName>
      caseGroupStatusTransitionManager() {
    return caseSvcStateTransitionManagerFactory.getStateTransitionManager(
        CaseSvcStateTransitionManagerFactory.CASE_GROUP);
  }

  /**
   * The DistributedListManager for CaseDistribution
   *
   * @param redissonClient the redissonClient
   * @return the DistributedListManager
   */
  @Bean
  public DistributedListManager<Integer> caseDistributionListManager(
      RedissonClient redissonClient) {
    return new DistributedListManagerRedissonImpl<>(
        CASE_DISTRIBUTION_LIST,
        redissonClient,
        appConfig.getDataGrid().getListTimeToWaitSeconds(),
        appConfig.getDataGrid().getListTimeToLiveSeconds());
  }

  /**
   * The RedissonClient
   *
   * @return the RedissonClient
   */
  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config
        .useSingleServer()
        .setAddress(appConfig.getDataGrid().getAddress())
        .setPassword(appConfig.getDataGrid().getPassword());
    return Redisson.create(config);
  }

  /**
   * The restTemplate bean injected in REST client classes
   *
   * @return the restTemplate used in REST calls
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   * The RestUtility bean for the IAC service
   *
   * @return the RestUtility bean for the IAC service
   */
  @Bean
  @Qualifier("iacServiceRestUtility")
  public RestUtility iacServiceRestUtility() {
    return new RestUtility(appConfig.getInternetAccessCodeSvc().getConnectionConfig());
  }

  /**
   * The RestUtility bean for the Action service
   *
   * @return the RestUtility bean for the Action service
   */
  @Bean
  @Qualifier("actionServiceRestUtility")
  public RestUtility actionServiceRestUtility() {
    return new RestUtility(appConfig.getActionSvc().getConnectionConfig());
  }

  /**
   * The RestUtility bean for the CollectionExercise service
   *
   * @return the RestUtility bean for the CollectionExercise service
   */
  @Bean
  @Qualifier("collectionExerciseRestUtility")
  public RestUtility collectionExerciseRestUtility() {
    return new RestUtility(appConfig.getCollectionExerciseSvc().getConnectionConfig());
  }

  /**
   * The RestExceptionHandler to handle exceptions thrown in our endpoints
   *
   * @return the RestExceptionHandler
   */
  @Bean
  public RestExceptionHandler restExceptionHandler() {
    return new RestExceptionHandler();
  }

  /**
   * The CustomObjectMapper to output dates in the json in our agreed format
   *
   * @return the CustomObjectMapper
   */
  @Bean
  @Primary
  public CustomObjectMapper customObjectMapper() {
    return new CustomObjectMapper();
  }

  /**
   * Bean used to access Distributed Lock Manager
   *
   * @param redissonClient Redisson Client
   * @return the Distributed Lock Manager
   */
  @Bean
  public DistributedInstanceManager reportDistributedInstanceManager(
      RedissonClient redissonClient) {
    return new DistributedInstanceManagerRedissonImpl(REPORT_EXECUTION_LOCK, redissonClient);
  }

  /**
   * Bean used to access Distributed Latch Manager
   *
   * @param redissonClient Redisson Client
   * @return the Distributed Lock Manager
   */
  @Bean
  public DistributedLatchManager reportDistributedLatchManager(RedissonClient redissonClient) {
    return new DistributedLatchManagerRedissonImpl(
        REPORT_EXECUTION_LOCK,
        redissonClient,
        appConfig.getDataGrid().getReportLockTimeToLiveSeconds());
  }

  /**
   * Bean used to access Distributed Execution Lock Manager
   *
   * @param redissonClient Redisson Client
   * @return the Distributed Lock Manager
   */
  @Bean
  public DistributedLockManager reportDistributedLockManager(RedissonClient redissonClient) {
    return new DistributedLockManagerRedissonImpl(
        REPORT_EXECUTION_LOCK,
        redissonClient,
        appConfig.getDataGrid().getReportLockTimeToLiveSeconds());
  }

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  public static final String COLLEX_CACHE_NAME = "collectionexercises";

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(COLLEX_CACHE_NAME);
  }

  @CacheEvict(
      allEntries = true,
      cacheNames = {COLLEX_CACHE_NAME})
  @Scheduled(fixedDelay = 60000)
  public void cacheEvict() {
    /* This is getting rid of the cached entries in case anything's been changed. We imagine that
     * the maximum of 1 minute delay to seeing changes reflected in the case service will not
     * cause any issues*/
  }

  @Bean
  public DateTimeUtil dateTimeUtil(){
    return new DateTimeUtil();
  }

  @Bean
  public MessageChannel pubsubInputChannel() {
    return new DirectChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "pubsubInputChannel")
  public MessageHandler messageReceiver() {
    return message -> {
      log.info("Message arrived! Payload: " + new String((byte[]) message.getPayload()));
      BasicAcknowledgeablePubsubMessage originalMessage =
              message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
      originalMessage.ack();
    };
  }

  @Bean
  public PubSubInboundChannelAdapter messageChannelAdapter(
          @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
          PubSubTemplate pubSubTemplate) {
    log.info("Receipt subscription [" + appConfig.getGcp().getReceiptSubscription() + "]");
    PubSubInboundChannelAdapter adapter =
            new PubSubInboundChannelAdapter(pubSubTemplate, appConfig.getGcp().getReceiptSubscription());
    adapter.setOutputChannel(inputChannel);
    adapter.setAckMode(AckMode.MANUAL);

    return adapter;
  }

}
