package uk.gov.ons.ctp.response.casesvc.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.casesvc.config.ActionSvc;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.utility.RestUtility;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

/**
 * A test of the case frame service client service
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionSvcClientServiceImplTest {

  private static final String ACTION_PATH = "/actions";
  private static final String GENERAL_ESCALATION = "GeneralEscalation";
  private static final String HTTP = "http";
  private static final String LOCALHOST = "localhost";

  private static final UUID EXISTING_CASE_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd1");

  @InjectMocks
  private ActionSvcClientServiceImpl actionSvcClientService;

  @Mock
  private AppConfig appConfig;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private RestUtility restUtility;

  /**
   * Sets up Mockito for tests
   */
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Happy path scenario for createAndPostAction
   */
  @Test
  public void testCreateAndPostAction() {
    ActionSvc actionSvcConfig = new ActionSvc();
    actionSvcConfig.setActionsPath(ACTION_PATH);
    Mockito.when(appConfig.getActionSvc()).thenReturn(actionSvcConfig);

    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme(HTTP)
        .host(LOCALHOST)
        .port(80)
        .path(ACTION_PATH)
        .build();
    when(restUtility.createUriComponents(any(String.class), any(MultiValueMap.class))).
        thenReturn(uriComponents);

    ActionDTO actionDTO = new ActionDTO();
    actionDTO.setCaseId(EXISTING_CASE_ID);
    actionDTO.setActionTypeName(GENERAL_ESCALATION);
    actionDTO.setCreatedBy(SYSTEM);
    HttpEntity httpEntity = new HttpEntity<>(actionDTO, null);
    when(restUtility.createHttpEntity(any(ActionDTO.class))).thenReturn(httpEntity);

    actionSvcClientService.createAndPostAction(GENERAL_ESCALATION, EXISTING_CASE_ID, SYSTEM);

    verify(restUtility, times(1)).createUriComponents(ACTION_PATH, null);
    verify(restUtility, times(1)).createHttpEntity(eq(actionDTO));
    verify(restTemplate, times(1)).exchange(eq(uriComponents.toUri()), eq(HttpMethod.POST),
        eq(httpEntity), eq(ActionDTO.class));

  }
}
