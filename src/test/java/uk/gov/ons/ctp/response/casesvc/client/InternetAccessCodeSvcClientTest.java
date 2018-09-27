package uk.gov.ons.ctp.response.casesvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.InternetAccessCodeSvc;
import uk.gov.ons.ctp.response.iac.representation.CreateInternetAccessCodeDTO;
import uk.gov.ons.ctp.response.iac.representation.InternetAccessCodeDTO;
import uk.gov.ons.ctp.response.iac.representation.UpdateInternetAccessCodeDTO;

/** Testing InternetAccessCodeSvcClientService */
@RunWith(MockitoJUnitRunner.class)
public class InternetAccessCodeSvcClientTest {

  private static final String HTTP = "http";
  private static final String LOCALHOST = "localhost";
  private static final String IAC = "ABCD-EFGH-IJKL-MNOP";
  private static final String IAC_GET_PATH = "/iacs/{}";
  private static final String IAC_POST_PATH = "/iacs";
  private static final String IAC_PUT_PATH = "/iacs/{iac}";

  @Mock private AppConfig appConfig;
  @Mock private RestTemplate restTemplate;
  @Mock private RestUtility restUtility;

  @InjectMocks private InternetAccessCodeSvcClient internetAccessCodeSvcClient;

  /** Set up Mockito for tests */
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /** Testing happy path for generateIACs */
  @Test
  public void testGenerateIACs() {
    InternetAccessCodeSvc iacSvcConfig = new InternetAccessCodeSvc();
    iacSvcConfig.setIacPostPath(IAC_POST_PATH);
    when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvcConfig);

    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .scheme(HTTP)
            .host(LOCALHOST)
            .port(80)
            .path(IAC_POST_PATH)
            .build();
    when(restUtility.createUriComponents(any(String.class), any(MultiValueMap.class)))
        .thenReturn(uriComponents);

    int count = 3;
    CreateInternetAccessCodeDTO createInternetAccessCodeDTO =
        new CreateInternetAccessCodeDTO(count, SYSTEM);
    HttpEntity httpEntity = new HttpEntity<>(createInternetAccessCodeDTO, null);
    when(restUtility.createHttpEntity(any(CreateInternetAccessCodeDTO.class)))
        .thenReturn(httpEntity);

    String[] body = new String[] {"1", "2", "3"};
    ResponseEntity<String[]> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);
    when(restTemplate.exchange(
            any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .thenReturn(responseEntity);

    List<String> codes = internetAccessCodeSvcClient.generateIACs(count);
    assertTrue(codes != null);
    assertEquals(3, codes.size());
    assertTrue(codes.containsAll(Arrays.asList(new String[] {"1", "2", "3"})));

    verify(restUtility, times(1)).createUriComponents(IAC_POST_PATH, null);
    verify(restUtility, times(1)).createHttpEntity(eq(createInternetAccessCodeDTO));
    verify(restTemplate, times(1))
        .exchange(
            eq(uriComponents.toUri()), eq(HttpMethod.POST), eq(httpEntity), eq(String[].class));
  }

  /** Testing happy path for disableIAC */
  @Test
  public void testDisableIAC() {
    InternetAccessCodeSvc iacSvcConfig = new InternetAccessCodeSvc();
    iacSvcConfig.setIacPutPath(IAC_PUT_PATH);
    when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvcConfig);

    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .scheme(HTTP)
            .host(LOCALHOST)
            .port(80)
            .path(IAC_PUT_PATH)
            .build();
    when(restUtility.createUriComponents(any(String.class), eq(null), eq(IAC)))
        .thenReturn(uriComponents);

    UpdateInternetAccessCodeDTO updateInternetAccessCodeDTO =
        new UpdateInternetAccessCodeDTO("SYSTEM");
    HttpEntity httpEntity = new HttpEntity<>(updateInternetAccessCodeDTO, null);
    when(restUtility.createHttpEntity(any(UpdateInternetAccessCodeDTO.class)))
        .thenReturn(httpEntity);
    when(restTemplate.exchange(
            uriComponents.toUri(), HttpMethod.PUT, httpEntity, InternetAccessCodeDTO.class))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    internetAccessCodeSvcClient.disableIAC(IAC);

    verify(restUtility, times(1)).createUriComponents(IAC_PUT_PATH, null, IAC);
    verify(restUtility, times(1)).createHttpEntity(eq(updateInternetAccessCodeDTO));
    verify(restTemplate, times(1))
        .exchange(
            eq(uriComponents.toUri()),
            eq(HttpMethod.PUT),
            eq(httpEntity),
            eq(InternetAccessCodeDTO.class));
  }

  @Test
  public void testFailedToDisableIAC() {

    // Given
    InternetAccessCodeSvc iacSvcConfig = new InternetAccessCodeSvc();
    iacSvcConfig.setIacPutPath(IAC_PUT_PATH);
    when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvcConfig);

    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .scheme(HTTP)
            .host(LOCALHOST)
            .port(80)
            .path(IAC_PUT_PATH)
            .build();
    when(restUtility.createUriComponents(any(String.class), eq(null), eq(IAC)))
        .thenReturn(uriComponents);

    UpdateInternetAccessCodeDTO updateInternetAccessCodeDTO =
        new UpdateInternetAccessCodeDTO("SYSTEM");
    HttpEntity httpEntity = new HttpEntity<>(updateInternetAccessCodeDTO, null);
    when(restUtility.createHttpEntity(any(UpdateInternetAccessCodeDTO.class)))
        .thenReturn(httpEntity);
    when(restTemplate.exchange(
            uriComponents.toUri(), HttpMethod.PUT, httpEntity, InternetAccessCodeDTO.class))
        .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

    // When
    Boolean isDisableIACSuccessful = internetAccessCodeSvcClient.disableIAC(IAC);

    // Then
    assertFalse(isDisableIACSuccessful);
  }

  @Test
  public void testIsIACActive() {
    InternetAccessCodeSvc iacSvcConfig = new InternetAccessCodeSvc();
    iacSvcConfig.setIacPostPath(IAC_GET_PATH);
    when(appConfig.getInternetAccessCodeSvc()).thenReturn(iacSvcConfig);
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .scheme(HTTP)
            .host(LOCALHOST)
            .port(80)
            .path(IAC_GET_PATH)
            .build();
    when(restUtility.createUriComponents(any(String.class), eq(null), eq(IAC)))
        .thenReturn(uriComponents);
    when(restUtility.createHttpEntity(any())).thenReturn(null);
    InternetAccessCodeDTO iacDTO = new InternetAccessCodeDTO();
    iacDTO.setActive(true);
    ResponseEntity<InternetAccessCodeDTO> responseEntity =
        new ResponseEntity<>(iacDTO, HttpStatus.OK);
    when(restTemplate.exchange(
            uriComponents.toUri(), HttpMethod.GET, null, InternetAccessCodeDTO.class))
        .thenReturn(responseEntity);

    Boolean isActive = internetAccessCodeSvcClient.isIacActive(IAC);

    assertEquals(isActive, true);
    verify(restTemplate, times(1))
        .exchange(uriComponents.toUri(), HttpMethod.GET, null, InternetAccessCodeDTO.class);
  }
}
