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
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.InternetAccessCodeSvc;
import uk.gov.ons.ctp.response.casesvc.utility.RestUtility;
import uk.gov.ons.ctp.response.iac.representation.CreateInternetAccessCodeDTO;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

/**
 * A test of the case frame service client service
 */
@RunWith(MockitoJUnitRunner.class)
public class InternetAccessCodeSvcClientServiceImplTest {

  private static final String HTTP = "http";
  private static final String LOCALHOST = "localhost";
  private static final String IAC_PATH = "/iacs";

  @Mock
  private AppConfig appConfig;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private RestUtility restUtility;

  @InjectMocks
  private InternetAccessCodeSvcClientServiceImpl iacSvcClientService;

  /**
   * Set up Mockito for tests
   */
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Testing happy path for generateIACs
   */
  @Test
  public void testGenerateIACs() {
    InternetAccessCodeSvc iacSvcConfig = new InternetAccessCodeSvc();
    iacSvcConfig.setIacPostPath(IAC_PATH);
    when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvcConfig);

    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme(HTTP)
        .host(LOCALHOST)
        .port(80)
        .path(IAC_PATH)
        .build();
    when(restUtility.createUriComponents(any(String.class), any(MultiValueMap.class))).thenReturn(uriComponents);

    int count = 3;
    CreateInternetAccessCodeDTO createInternetAccessCodeDTO = new CreateInternetAccessCodeDTO(count, SYSTEM);
    HttpEntity httpEntity = new HttpEntity<>(createInternetAccessCodeDTO, null);
    when(restUtility.createHttpEntity(any(CreateInternetAccessCodeDTO.class))).thenReturn(httpEntity);

    String[] body = new String[] {"1", "2", "3"};
    ResponseEntity<String[]> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);
    when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .thenReturn(responseEntity);

    List<String> codes = iacSvcClientService.generateIACs(count);
    assertTrue(codes != null);
    assertEquals(3, codes.size());
    assertTrue(codes.containsAll(Arrays.asList(new String[] {"1", "2", "3"})));

    verify(restUtility, times(1)).createUriComponents(IAC_PATH, null);
    verify(restUtility, times(1)).createHttpEntity(eq(createInternetAccessCodeDTO));
    verify(restTemplate, times(1)).exchange(eq(uriComponents.toUri()), eq(HttpMethod.POST),
        eq(httpEntity), eq(String[].class));
  }

  // TODO Test disableIAC happy path
  // TODO Test error scenarios
}
