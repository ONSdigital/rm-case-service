package uk.gov.ons.ctp.response.casesvc;

import javax.inject.Named;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jaxrs.JAXRSRegister;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.endpoint.AddressEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.CaseTypeEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.CategoryEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.QuestionSetEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.QuestionnaireEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.SampleEndpoint;
import uk.gov.ons.ctp.response.casesvc.endpoint.SurveyEndpoint;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.GeographyDTO;

/**
 * The 'main' entry point for the CaseSvc SpringBoot Application.
 */
@SpringBootApplication
@EnableTransactionManagement
@IntegrationComponentScan
@EnableAsync
@ImportResource("main-int.xml")
public class CaseSvcApplication {

  @Autowired
  private AppConfig appConfig;

  /**
   * The action service client bean
   * @return the RestClient for the action service
   */
  @Bean
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
      register(QuestionnaireEndpoint.class);
      register(QuestionSetEndpoint.class);
      register(SampleEndpoint.class);
      register(SurveyEndpoint.class);

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
