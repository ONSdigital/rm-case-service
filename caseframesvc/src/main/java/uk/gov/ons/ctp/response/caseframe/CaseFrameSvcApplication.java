package uk.gov.ons.ctp.response.caseframe;

import javax.inject.Named;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import uk.gov.ons.ctp.response.caseframe.endpoint.AddressEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.CaseEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.CaseTypeEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.LocalAuthorityEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.MsoaEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.QuestionSetEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.QuestionnaireEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.RegionEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.SampleEndpoint;
import uk.gov.ons.ctp.response.caseframe.endpoint.SurveyEndpoint;

/**
 * The 'main' entry point for the CaseFrame SpringBoot Application.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class CaseFrameSvcApplication {

  /**
   * The JerseyConfig class used to config the JAX RS implementation.
   */
  @Named
  public static class JerseyConfig extends ResourceConfig {

    /**
     * Required default constructor.
     */
    public JerseyConfig() {
      packages("uk.gov.ons.ctp");

      // AddressFrame
      register(RegionEndpoint.class);
      register(LocalAuthorityEndpoint.class);
      register(MsoaEndpoint.class);
      register(AddressEndpoint.class);

      // Response
      register(CaseEndpoint.class);
      register(QuestionnaireEndpoint.class);
      register(QuestionSetEndpoint.class);
      register(CaseTypeEndpoint.class);
      register(SampleEndpoint.class);
      register(SurveyEndpoint.class);
    }
  }

  /**
   * The main entry point for this applicaion.
   * 
   * @param args
   *          runtime command line args
   */
  public static void main(final String[] args) {
    SpringApplication.run(CaseFrameSvcApplication.class, args);
  }
}
