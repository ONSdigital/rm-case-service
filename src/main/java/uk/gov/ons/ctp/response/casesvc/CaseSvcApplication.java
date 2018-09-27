package uk.gov.ons.ctp.response.casesvc;

import com.godaddy.logging.LoggingConfigs;
import java.time.Clock;
import javax.annotation.PostConstruct;
import net.sourceforge.cobertura.CoverageIgnore;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.common.distributed.DistributedInstanceManager;
import uk.gov.ons.ctp.common.distributed.DistributedInstanceManagerRedissonImpl;
import uk.gov.ons.ctp.common.distributed.DistributedLatchManager;
import uk.gov.ons.ctp.common.distributed.DistributedLatchManagerRedissonImpl;
import uk.gov.ons.ctp.common.distributed.DistributedListManager;
import uk.gov.ons.ctp.common.distributed.DistributedListManagerRedissonImpl;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.distributed.DistributedLockManagerRedissonImpl;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.state.CaseSvcStateTransitionManagerFactory;

/** The 'main' entry point for the CaseSvc SpringBoot Application. */
@CoverageIgnore
@SpringBootApplication
@EnableTransactionManagement
@IntegrationComponentScan
@ComponentScan(basePackages = {"uk.gov.ons.ctp.response"})
@EnableJpaRepositories(basePackages = {"uk.gov.ons.ctp.response"})
@EntityScan("uk.gov.ons.ctp.response")
@EnableAsync
@ImportResource("springintegration/main.xml")
public class CaseSvcApplication {

  public static final String CASE_DISTRIBUTION_LIST = "casesvc.case.distribution";
  public static final String REPORT_EXECUTION_LOCK = "casesvc.report.execution";

  private AppConfig appConfig;
  private StateTransitionManagerFactory caseSvcStateTransitionManagerFactory;

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
}
