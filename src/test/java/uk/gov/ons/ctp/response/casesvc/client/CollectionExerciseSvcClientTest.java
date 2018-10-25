package uk.gov.ons.ctp.response.casesvc.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.CollectionExerciseSvc;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

public class CollectionExerciseSvcClientTest {

  private static final String CREATE_COLLEX_PATH = "/collectionexercises";
  private static final UUID EXISTING_SURVEY_ID =
      UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");
  private static final String EXERCISE_REF = "201808";
  private static final String USER_DESCRIPTION = "August 2018";

  @InjectMocks private CollectionExerciseSvcClient collectionExerciseSvcClient;

  @Mock private AppConfig appConfig;

  @Mock private RestTemplate restTemplate;

  @Mock private RestUtility restUtility;

  private static CollectionExerciseDTO collectionExercise;
  private static List<CollectionExerciseDTO> collectionExercises;

  @Before
  public void setUp() throws Exception {
    collectionExercises = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    collectionExercise = collectionExercises.get(0);

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGetCollectionExercise() {
    // Given
    CollectionExerciseSvc collectionExerciseSvcConfig = new CollectionExerciseSvc();
    collectionExerciseSvcConfig.setCollectionExercisePath("test:path");
    when(appConfig.getCollectionExerciseSvc()).thenReturn(collectionExerciseSvcConfig);
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .path(
                collectionExerciseSvcConfig.getCollectionExercisePath()
                    + "/"
                    + collectionExercise.getId())
            .queryParams(null)
            .build();
    when(restUtility.createUriComponents(
            appConfig.getCollectionExerciseSvc().getCollectionExercisePath(),
            null,
            collectionExercise.getId()))
        .thenReturn(uriComponents);
    HttpEntity httpEntity = new HttpEntity(null, null);
    when(restUtility.createHttpEntity(null)).thenReturn(httpEntity);
    ResponseEntity<CollectionExerciseDTO> responseEntity =
        new ResponseEntity(collectionExercise, HttpStatus.OK);
    when(restTemplate.exchange(
            any(URI.class), eq(HttpMethod.GET), eq(httpEntity), eq(CollectionExerciseDTO.class)))
        .thenReturn(responseEntity);

    // When
    CollectionExerciseDTO responseCollectionExercise =
        collectionExerciseSvcClient.getCollectionExercise(collectionExercise.getId());

    // Then
    verify(restTemplate, times(1))
        .exchange(
            URI.create(String.format("test:path/%s", collectionExercise.getId())),
            HttpMethod.GET,
            httpEntity,
            CollectionExerciseDTO.class);
    assertEquals(responseCollectionExercise.getId(), collectionExercise.getId());
  }

  @Test
  public void testGetCollectionExercisesBySurvey() {
    // Given
    CollectionExerciseSvc collectionExerciseSvcConfig = new CollectionExerciseSvc();
    collectionExerciseSvcConfig.setCollectionExerciseSurveyPath("test:path");
    when(appConfig.getCollectionExerciseSvc()).thenReturn(collectionExerciseSvcConfig);
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .path(
                collectionExerciseSvcConfig.getCollectionExerciseSurveyPath()
                    + "/"
                    + collectionExercise.getSurveyId())
            .queryParams(null)
            .build();
    when(restUtility.createUriComponents(
            appConfig.getCollectionExerciseSvc().getCollectionExerciseSurveyPath(),
            null,
            collectionExercise.getSurveyId()))
        .thenReturn(uriComponents);
    HttpEntity httpEntity = new HttpEntity(null, null);
    when(restUtility.createHttpEntity(null)).thenReturn(httpEntity);
    ResponseEntity<List<CollectionExerciseDTO>> responseEntity =
        new ResponseEntity(collectionExercises, HttpStatus.OK);
    when(restTemplate.exchange(
            any(URI.class),
            eq(HttpMethod.GET),
            eq(httpEntity),
            eq(new ParameterizedTypeReference<List<CollectionExerciseDTO>>() {})))
        .thenReturn(responseEntity);

    // When
    List<CollectionExerciseDTO> responseCollectionExercises =
        collectionExerciseSvcClient.getCollectionExercises(collectionExercise.getSurveyId());

    // Then
    verify(restTemplate, times(1))
        .exchange(
            URI.create(String.format("test:path/%s", collectionExercise.getSurveyId())),
            HttpMethod.GET,
            httpEntity,
            new ParameterizedTypeReference<List<CollectionExerciseDTO>>() {});
    assertEquals(
        responseCollectionExercises.get(0).getSurveyId(), collectionExercise.getSurveyId());
    assertEquals(
        responseCollectionExercises.get(1).getSurveyId(), collectionExercise.getSurveyId());
  }

  @Test
  public void testCreateCollectionExercise() {
    CollectionExerciseSvc collectionExerciseSvcConfig = new CollectionExerciseSvc();
    collectionExerciseSvcConfig.setCollectionExercisePath(CREATE_COLLEX_PATH);
    when(appConfig.getCollectionExerciseSvc()).thenReturn(collectionExerciseSvcConfig);

    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .path(collectionExerciseSvcConfig.getCollectionExercisesPath())
            .queryParams(null)
            .build();
    when(restUtility.createUriComponents(any(String.class), any(MultiValueMap.class)))
        .thenReturn(uriComponents);

    CollectionExerciseDTO collex = new CollectionExerciseDTO();
    collex.setSurveyId(EXISTING_SURVEY_ID.toString());
    collex.setExerciseRef(EXERCISE_REF);
    collex.setUserDescription(USER_DESCRIPTION);
    HttpEntity httpEntity = new HttpEntity<>(collex, null);
    when(restUtility.createHttpEntity(any(CollectionExerciseDTO.class))).thenReturn(httpEntity);

    collectionExerciseSvcClient.createCollectionExercise(
        EXISTING_SURVEY_ID, EXERCISE_REF, USER_DESCRIPTION);

    verify(restUtility, times(1))
        .createUriComponents(collectionExerciseSvcConfig.getCollectionExercisesPath(), null);
    verify(restUtility, times(1)).createHttpEntity(eq(collex));
    verify(restTemplate, times(1))
        .exchange(
            eq(uriComponents.toUri()),
            eq(HttpMethod.POST),
            eq(httpEntity),
            eq(CollectionExerciseDTO.class));
  }
}
