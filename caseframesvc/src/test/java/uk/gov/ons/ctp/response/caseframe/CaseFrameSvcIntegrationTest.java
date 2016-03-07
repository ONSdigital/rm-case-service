package uk.gov.ons.ctp.response.caseframe;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import uk.gov.ons.ctp.response.caseframe.domain.model.Survey;
import uk.gov.ons.ctp.response.caseframe.domain.repository.SurveyRepository;
import uk.gov.ons.ctp.response.caseframe.service.impl.SurveyServiceImpl;

/**
 * Example Integration Test
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = uk.gov.ons.ctp.response.caseframe.CaseFrameSvcApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=8171")
public class CaseFrameSvcIntegrationTest {

  @Autowired
  private ApplicationContext context;

  private RestTemplate restTemplate = new TestRestTemplate();

  /**
   * hardwired disabling of liquibase
   */
  @BeforeClass
  public static void switchOffLiquibase() {
    Properties sysProp = System.getProperties();
    sysProp.setProperty("liquibase.enabled", "false");
  }

  /**
   * test example
   */
  @Test
  public final void surveyEndpointTest() {

    // To use real repository and go to DB simply remove setting of mock
    // Repository below.
    Survey survey = new Survey(1, "CTP", "Test case 1");

    SurveyRepository mockRepo = Mockito.mock(SurveyRepository.class);
    Mockito.when(mockRepo.findOne(1)).thenReturn(survey);
    // Remove above to use DB rather than mock

    SurveyServiceImpl service = context.getBean(SurveyServiceImpl.class);
    service.setSurveyRepo(mockRepo);

    ResponseEntity<Survey> entity = restTemplate.getForEntity("http://localhost:8171/surveys/1", Survey.class);
    assertTrue(entity.getStatusCode().is2xxSuccessful());
  }
}
