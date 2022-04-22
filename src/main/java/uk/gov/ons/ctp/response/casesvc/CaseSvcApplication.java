package uk.gov.ons.ctp.response.casesvc;

import com.godaddy.logging.LoggingConfigs;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.time.Clock;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.state.CaseSvcStateTransitionManagerFactory;
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
@Slf4j
public class CaseSvcApplication {

  @Autowired private AppConfig appConfig;
  @Autowired private DataSource dataSource;
  @Autowired private StateTransitionManagerFactory caseSvcStateTransitionManagerFactory;

  /** Constructor for CaseSvcApplication */
  @Autowired
  public CaseSvcApplication(
      final AppConfig appConfig,
      final StateTransitionManagerFactory caseSvcStateTransitionManagerFactory) {
    this.appConfig = appConfig;
    this.caseSvcStateTransitionManagerFactory = caseSvcStateTransitionManagerFactory;
  }

  @Bean
  public LiquibaseProperties liquibaseProperties() {
    return new LiquibaseProperties();
  }

  @Bean
  @DependsOn(value = "entityManagerFactory")
  @DependsOnDatabaseInitialization
  public CustomSpringLiquibase liquibase() {
    LiquibaseProperties liquibaseProperties = liquibaseProperties();
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setChangeLog(liquibaseProperties.getChangeLog());
    liquibase.setContexts(liquibaseProperties.getContexts());
    liquibase.setDataSource(getDataSource(liquibaseProperties));
    liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
    liquibase.setDropFirst(liquibaseProperties.isDropFirst());
    liquibase.setShouldRun(true);
    liquibase.setLabels(liquibaseProperties.getLabels());
    liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
    return new CustomSpringLiquibase(liquibase);
  }

  private DataSource getDataSource(LiquibaseProperties liquibaseProperties) {
    if (liquibaseProperties.getUrl() == null) {
      return this.dataSource;
    }

    return DataSourceBuilder.create()
        .url(liquibaseProperties.getUrl())
        .username(liquibaseProperties.getUser())
        .password(liquibaseProperties.getPassword())
        .build();
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
   * Bean used to access party frame service through REST calls
   *
   * @return the service client
   */
  @Bean
  @Qualifier("partySvcClient")
  public RestUtility partyClient() {
    return new RestUtility(appConfig.getPartySvc().getConnectionConfig());
  }

  /**
   * Bean used to access survey service through REST calls
   *
   * @return the service client
   */
  @Bean
  @Qualifier("surveySvcClient")
  public RestUtility surveyClient() {
    return new RestUtility(appConfig.getSurveySvc().getConnectionConfig());
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
  public DateTimeUtil dateTimeUtil() {
    return new DateTimeUtil();
  }

  @Bean
  public PubSubInboundChannelAdapter caseCreationChannelAdapter(
      @Qualifier("caseCreationChannel") MessageChannel inputChannel,
      PubSubTemplate pubSubTemplate) {
    String subscriptionName = appConfig.getGcp().getCaseNotificationSubscription();
    log.info("Application is listening for case creation on subscription id {}", subscriptionName);
    PubSubInboundChannelAdapter adapter =
        new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
    adapter.setOutputChannel(inputChannel);
    adapter.setAckMode(AckMode.MANUAL);
    return adapter;
  }

  @Bean
  public MessageChannel caseCreationChannel() {
    return new PublishSubscribeChannel();
  }

  /** Bean used to create PubSub email channel */
  @Bean
  @ServiceActivator(inputChannel = "notifyEmailChannel")
  public MessageHandler emailMessageSender(PubSubTemplate pubsubTemplate) {
    return new PubSubMessageHandler(pubsubTemplate, appConfig.getGcp().getNotifyTopic());
  }

  /** Bean used to publish PubSub email message */
  @MessagingGateway(defaultRequestChannel = "notifyEmailChannel")
  public interface PubSubOutboundEmailGateway {
    void sendToPubSub(String text);
  }

  /** Bean used to create PubSub print file channel */
  @Bean
  @ServiceActivator(inputChannel = "printFileChannel")
  public MessageHandler printFileMessageSender(PubSubTemplate pubsubTemplate) {
    return new PubSubMessageHandler(pubsubTemplate, appConfig.getGcp().getPrintFileTopic());
  }

  /** Bean used to publish PubSub print file message */
  @MessagingGateway(defaultRequestChannel = "printFileChannel")
  public interface PubSubOutboundPrintFileGateway {
    void sendToPubSub(String text, @Header("printFilename") String header);
  }

  /**
   * Bean used to create and configure GCS Client
   *
   * @return the Storage Client
   */
  @Bean
  public Storage storage() {
    return StorageOptions.getDefaultInstance().getService();
  }

  @Bean
  @ServiceActivator(inputChannel = "collectionExerciseEventStatusChannel")
  public MessageHandler collectionExerciseEventStatusMessageSender(PubSubTemplate pubsubTemplate) {
    String topicId = appConfig.getGcp().getCollectionExerciseEventStatusTopic();
    log.info(
        "Application started with publisher for collection exercise event status with topic Id {}",
        topicId);
    return new PubSubMessageHandler(pubsubTemplate, topicId);
  }

  @MessagingGateway(defaultRequestChannel = "collectionExerciseEventStatusChannel")
  public interface PubSubOutboundCollectionExerciseEventStatusGateway {
    void sendToPubSub(String text);
  }
}
