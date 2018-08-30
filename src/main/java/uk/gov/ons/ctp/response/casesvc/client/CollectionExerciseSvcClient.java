package uk.gov.ons.ctp.response.casesvc.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

/** The service to retrieve a CollectionExercise */
@CoverageIgnore
@Service
public class CollectionExerciseSvcClient {
  private static final Logger log = LoggerFactory.getLogger(CollectionExerciseSvcClient.class);

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Qualifier("collectionExerciseRestUtility")
  @Autowired
  private RestUtility restUtility;

  @Autowired private ObjectMapper objectMapper;

  /**
   * Returns the CollectionExercise for a given UUID
   *
   * @param collectionExerciseId the UUID to search by
   * @return the asscoaited CollectionExercise
   */
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

    log.with("collection_exercise_id").debug("about to get to the CollectionExercise SVC");
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, String.class);

    CollectionExerciseDTO result = null;
    if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
      String responseBody = responseEntity.getBody();
      try {
        result = objectMapper.readValue(responseBody, CollectionExerciseDTO.class);
      } catch (IOException e) {
        log.error("Could not read value", e);
      }
    }
    return result;
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
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, String.class);
    List<CollectionExerciseDTO> result = null;
    if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
      String responseBody = responseEntity.getBody();
      try {
        result =
            objectMapper.readValue(
                responseBody, new TypeReference<List<CollectionExerciseDTO>>() {});
      } catch (IOException e) {
        log.error("Could not read value", e);
      }
    }
    return result;
  }
}
