package uk.gov.ons.ctp.response.casesvc;

import javax.inject.Named;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;

import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jaxrs.JAXRSRegister;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.endpoint.AddressEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.CaseTypeEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.CategoryEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.SampleEndpoint;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.GeographyDTO;
import uk.gov.ons.ctp.response.casesvc.state.CaseSvcStateTransitionManagerFactory;

/**
 * The 'main' entry point for the CaseSvc SpringBoot Application.
 */
@SpringBootApplication
@EnableTransactionManagement
@IntegrationComponentScan
@EnableAsync
@ImportResource("main-int.xml")
public class CaseSvcApplication {

  public static final String CASE_DISTRIBUTION_MAP = "actionsvc.case.distribution";

  @Autowired
  private AppConfig appConfig;

  @Autowired
  private StateTransitionManagerFactory caseSvcStateTransitionManagerFactory;

  /**
   * Bean to allow application to make controlled state transitions of Actions
   * @return the state transition manager specifically for Actions
   */
  @Bean
  public StateTransitionManager<CaseDTO.CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager() {
    return caseSvcStateTransitionManagerFactory.getStateTransitionManager(
        CaseSvcStateTransitionManagerFactory.CASE_ENTITY);
  }

  /**
   * To config Hazelcast
   * @return the config
   */
  @Bean
  public Config hazelcastConfig() {
    Config hazelcastConfig = new Config();
    hazelcastConfig.addMapConfig(new MapConfig().setName(CASE_DISTRIBUTION_MAP));
    NetworkConfig networkConfig = hazelcastConfig.getNetworkConfig();

    JoinConfig joinConfig = networkConfig.getJoin();
    MulticastConfig multicastConfig = joinConfig.getMulticastConfig();
    multicastConfig.setEnabled(true);

    return hazelcastConfig;
  }
  
  /**
   * The IAC service client bean
   * @return the RestClient for the IAC service
   */
  @Bean
  @Qualifier("internetAccessCodeServiceClient")
  public RestClient internetAccessCodeServiceClient() {
    RestClient restHelper = new RestClient(appConfig.getInternetAccessCodeSvc().getConnectionConfig());
    return restHelper;
  }
  
  /**
   * The action service client bean
   * @return the RestClient for the action service
   */
  @Bean
  @Qualifier("actionServiceClient")
  public RestClient actionServiceClient() {
    RestClient restHelper = new RestClient(appConfig.getActionSvc().getConnectionConfig());
    return restHelper;
  }

  /**
   * The JerseyConfig class used to config the JAX RS implementation.
   */
  @Named
  public static class JerseyConfig extends ResourceConfig {
    /**
     * Required default constructor.
     */
    public JerseyConfig() {
      JAXRSRegister.listCommonTypes().forEach(t->register(t));

      // Register Frame JAX-RS components
      register(AddressEndpoint.class);

      // Register Case JAX-RS components
      register(CaseEndpoint.class);
      register(CaseTypeEndpoint.class);
      register(CategoryEndpoint.class);
      register(SampleEndpoint.class);
      // XXX register new endpoints here

      register(new CTPMessageBodyReader<GeographyDTO>(GeographyDTO.class) {
      });
      register(new CTPMessageBodyReader<CaseEventDTO>(CaseEventDTO.class) {
      });
    }
  }

  /**
   * The main entry point for this applicaion.
   *
   * @param args runtime command line args
   */
  public static void main(final String[] args) {
    SpringApplication.run(CaseSvcApplication.class, args);
  }
}
