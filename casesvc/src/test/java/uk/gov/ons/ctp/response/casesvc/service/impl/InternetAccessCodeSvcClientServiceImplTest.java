package uk.gov.ons.ctp.response.casesvc.service.impl;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.cloud.sleuth.*;
import org.springframework.http.*;
import org.springframework.test.web.client.*;
import org.springframework.web.client.*;
import uk.gov.ons.ctp.common.rest.*;
import uk.gov.ons.ctp.response.casesvc.config.*;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * A test of the case frame service client service
 */
@RunWith(MockitoJUnitRunner.class)
public class InternetAccessCodeSvcClientServiceImplTest {

  @Mock
  private Tracer tracer;
  @Mock
  private Span span;

  @Mock
  private AppConfig appConfig;

  @Spy
  private RestClient restClient = new RestClient();

  @InjectMocks
  private InternetAccessCodeSvcClientServiceImpl iacSvcClientService;

  /**
   * Set up Mockito for tests
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
  public void testCreateCodes() {
    InternetAccessCodeSvc iacSvcConfig = new InternetAccessCodeSvc();
    iacSvcConfig.setIacPostPath("/iacs");
    Mockito.when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvcConfig);
    RestTemplate restTemplate = this.restClient.getRestTemplate();

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo("http://localhost:8080/iacs"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().string(containsString("\"count\":" + 3 + ",")))
        .andExpect(content().string(containsString("\"createdBy\":" + "\"SYSTEM\"")))
        .andRespond(withSuccess("[1,2,3]", MediaType.APPLICATION_JSON));

    List<String> codes = iacSvcClientService.generateIACs(3);
    assertTrue(codes != null);
    assertTrue(codes.containsAll(Arrays.asList(new String[] {"1", "2", "3"})));
    mockServer.verify();
  }

}
