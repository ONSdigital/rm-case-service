package uk.gov.ons.ctp.response.casesvc.client;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.response.casesvc.CaseSvcApplication;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtility;

/** The service to retrieve a CollectionExercise */
@Service
public class CollectionExerciseSvcClient {
  private static final Logger log = LoggerFactory.getLogger(CollectionExerciseSvcClient.class);

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Qualifier("collectionExerciseRestUtility")
  @Autowired
  private RestUtility restUtility;

  @Qualifier("customObjectMapper")
  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Returns the CollectionExercise for a given UUID
   *
   * @param collectionExerciseId the UUID to search by
   * @return the asscoaited CollectionExercise
   */
  @Cacheable(CaseSvcApplication.COLLEX_CACHE_NAME)
  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  public CollectionExerciseDTO getCollectionExercise(final UUID collectionExerciseId) {
    UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getCollectionExerciseSvc().getCollectionExercisePath(),
            null,
            collectionExerciseId);

    HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

    log.debug("Retrieving collection exercise", kv("collection_exercise_id", collectionExerciseId));
    ResponseEntity<CollectionExerciseDTO> responseEntity =
        restTemplate.exchange(
            uriComponents.toUri(), HttpMethod.GET, httpEntity, CollectionExerciseDTO.class);

    return responseEntity.getBody();
  }

  /**
   * Returns all CollectionExercises for a given survey ID
   *
   * @param surveyId the survey ID to search by
   * @return the list of Collection Exercises
   */
  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  public List<CollectionExerciseDTO> getCollectionExercises(final String surveyId) {
    UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getCollectionExerciseSvc().getCollectionExerciseSurveyPath(), null, surveyId);
    HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);
    ResponseEntity<List<CollectionExerciseDTO>> responseEntity =
        restTemplate.exchange(
            uriComponents.toUri(),
            HttpMethod.GET,
            httpEntity,
            new ParameterizedTypeReference<List<CollectionExerciseDTO>>() {});
    return responseEntity.getBody();
  }

  /**
   * Creates a CollectionExercise
   *
   * @param surveyId the survey ID for the collection exercise
   * @param exerciseRef the Exercise ref for the collection exercise
   * @param userDescription the user description for the collection exercise
   */
  public void createCollectionExercise(
      final UUID surveyId, final String exerciseRef, final String userDescription) {
    log.debug(
        "Creating a collection exercise",
        kv("survey_id", surveyId),
        kv("exercise_ref", exerciseRef),
        kv("user_description", userDescription));
    CollectionExerciseDTO collex = new CollectionExerciseDTO();
    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getCollectionExerciseSvc().getCollectionExercisesPath(), null);
    collex.setSurveyId(surveyId.toString());
    collex.setExerciseRef(exerciseRef);
    collex.setUserDescription(userDescription);
    HttpEntity<?> httpEntity = restUtility.createHttpEntity(collex);
    restTemplate.exchange(
        uriComponents.toUri(), HttpMethod.POST, httpEntity, CollectionExerciseDTO.class);
  }
}
