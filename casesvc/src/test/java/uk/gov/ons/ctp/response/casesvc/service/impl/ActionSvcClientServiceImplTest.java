package uk.gov.ons.ctp.response.casesvc.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.casesvc.config.ActionSvc;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

/**
 * A test of the case frame service client service
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionSvcClientServiceImplTest {

  private static final String GENERAL_ESCALATION = "GeneralEscalation";

  private static final UUID EXISTING_CASE_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd1");

  @Mock
  private Tracer tracer;

  @Mock
  private Span span;

  @Mock
  private AppConfig appConfig;

  @Spy
  private RestClient restClient = new RestClient();

  @InjectMocks
  private ActionSvcClientServiceImpl actionSvcClientService;

  /**
   * Sets up Mockito for tests
   */
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(tracer.getCurrentSpan()).thenReturn(span);
    Mockito.when(tracer.createSpan(any(String.class))).thenReturn(span);
    restClient.setTracer(tracer);
  }

  /**
   * Guess what? - a test!
   */
  @Test
  public void testCreateAction() {
    ActionSvc actionSvcConfig = new ActionSvc();
    actionSvcConfig.setActionsPath("/actions");
    Mockito.when(appConfig.getActionSvc()).thenReturn(actionSvcConfig);
    RestTemplate restTemplate = this.restClient.getRestTemplate();
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo("http://localhost:8080/actions"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().string(containsString("\"actionTypeName\":\"" + GENERAL_ESCALATION + "\",")))
        .andExpect(content().string(containsString("\"caseId\":\"" + EXISTING_CASE_ID.toString() + "\",")))
        .andExpect(content().string(containsString("\"createdBy\":\"" + SYSTEM + "\"")))
        .andRespond(withSuccess());

    actionSvcClientService.createAndPostAction(GENERAL_ESCALATION, EXISTING_CASE_ID, SYSTEM);

    mockServer.verify();
  }
}
