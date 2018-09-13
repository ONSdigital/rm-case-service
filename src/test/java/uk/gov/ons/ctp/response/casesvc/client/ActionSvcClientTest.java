package uk.gov.ons.ctp.response.casesvc.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.casesvc.config.ActionSvc;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

/** A test of the case frame service client service */
@RunWith(MockitoJUnitRunner.class)
public class ActionSvcClientTest {

  private static final String ACTION_PATH = "/actions";
  private static final String GENERAL_ESCALATION = "GeneralEscalation";
  private static final String HTTP = "http";
  private static final String LOCALHOST = "localhost";

  private static final UUID EXISTING_CASE_ID =
      UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd1");
  private static final UUID COLLECTION_EXERCISE_ID = UUID.randomUUID();

  @InjectMocks private ActionSvcClient actionSvcClient;

  @Mock private AppConfig appConfig;

  @Mock private RestTemplate restTemplate;

  @Mock private RestUtility restUtility;

  private ResponseEntity<List<ActionPlanDTO>> responseEntity;

  private List<ActionPlanDTO> actionPlans;

  /** Sets up Mockito for tests */
  @Before
  public void setUp() {
    ActionPlanDTO actionPlan = new ActionPlanDTO();
    actionPlan.setName("Test");
    actionPlan.setId(UUID.fromString("14fb3e68-4dca-46db-bf49-04b84e07e77c"));
    HashMap<String, String> selectors = new HashMap<>();
    selectors.put("collectionExerciseId", COLLECTION_EXERCISE_ID.toString());
    actionPlan.setSelectors(selectors);

    actionPlans = new ArrayList<>();
    actionPlans.add(actionPlan);

    responseEntity = new ResponseEntity(actionPlans, HttpStatus.OK);

    MockitoAnnotations.initMocks(this);
  }

  /** Happy path scenario for PostAction */
  @Test
  public void testPostAction() {
    ActionSvc actionSvcConfig = new ActionSvc();
    actionSvcConfig.setActionsPath(ACTION_PATH);
    Mockito.when(appConfig.getActionSvc()).thenReturn(actionSvcConfig);

    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .scheme(HTTP)
            .host(LOCALHOST)
            .port(80)
            .path(ACTION_PATH)
            .build();
    when(restUtility.createUriComponents(any(String.class), any(MultiValueMap.class)))
        .thenReturn(uriComponents);

    ActionDTO actionDTO = new ActionDTO();
    actionDTO.setCaseId(EXISTING_CASE_ID);
    actionDTO.setActionTypeName(GENERAL_ESCALATION);
    actionDTO.setCreatedBy(SYSTEM);
    HttpEntity httpEntity = new HttpEntity<>(actionDTO, null);
    when(restUtility.createHttpEntity(any(ActionDTO.class))).thenReturn(httpEntity);

    actionSvcClient.postAction(GENERAL_ESCALATION, EXISTING_CASE_ID, SYSTEM);

    verify(restUtility, times(1)).createUriComponents(ACTION_PATH, null);
    verify(restUtility, times(1)).createHttpEntity(eq(actionDTO));
    verify(restTemplate, times(1))
        .exchange(
            eq(uriComponents.toUri()), eq(HttpMethod.POST), eq(httpEntity), eq(ActionDTO.class));
  }

  @Test
  public void testGetActionPlans() {
    // Given
    ActionSvc actionSvcConfig = new ActionSvc();
    actionSvcConfig.setActionPlansPath("test:path");
    when(appConfig.getActionSvc()).thenReturn(actionSvcConfig);
    HttpEntity httpEntity = new HttpEntity(null, null);
    when(restUtility.createHttpEntityWithAuthHeader()).thenReturn(httpEntity);
    when(appConfig.getActionSvc()).thenReturn(actionSvcConfig);
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("collectionExerciseId", (COLLECTION_EXERCISE_ID).toString());
    queryParams.add("activeEnrolment", Boolean.toString(true));
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .path(actionSvcConfig.getActionPlansPath())
            .queryParams(queryParams)
            .build();
    when(restUtility.createUriComponents(any(String.class), any(MultiValueMap.class)))
        .thenReturn(uriComponents);
    when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            eq(httpEntity),
            eq(new ParameterizedTypeReference<List<ActionPlanDTO>>() {})))
        .thenReturn(responseEntity);

    // When
    List<ActionPlanDTO> actionPlanList =
        actionSvcClient.getActionPlans(COLLECTION_EXERCISE_ID, true);

    // Then
    verify(restTemplate, times(1))
        .exchange(
            String.format(
                "test:path?collectionExerciseId=%s&activeEnrolment=true", COLLECTION_EXERCISE_ID),
            HttpMethod.GET,
            httpEntity,
            new ParameterizedTypeReference<List<ActionPlanDTO>>() {});
    assertEquals(actionPlanList.get(0).getId(), actionPlans.get(0).getId());
  }

  @Test(expected = HttpClientErrorException.class)
  public void testGetActionPlansHTTPClientException() {
    // Given
    ActionSvc actionSvcConfig = new ActionSvc();
    actionSvcConfig.setActionPlansPath("test:path");
    when(appConfig.getActionSvc()).thenReturn(actionSvcConfig);
    HttpEntity httpEntity = new HttpEntity(null, null);
    when(restUtility.createHttpEntityWithAuthHeader()).thenReturn(httpEntity);
    when(appConfig.getActionSvc()).thenReturn(actionSvcConfig);
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("collectionExerciseId", (COLLECTION_EXERCISE_ID).toString());
    queryParams.add("activeEnrolment", Boolean.toString(true));
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .path(actionSvcConfig.getActionPlansPath())
            .queryParams(queryParams)
            .build();
    when(restUtility.createUriComponents(any(String.class), any(MultiValueMap.class)))
        .thenReturn(uriComponents);
    when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            eq(httpEntity),
            eq(new ParameterizedTypeReference<List<ActionPlanDTO>>() {})))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    // When
    actionSvcClient.getActionPlans(COLLECTION_EXERCISE_ID, true);

    // Then exception thrown
  }
}
