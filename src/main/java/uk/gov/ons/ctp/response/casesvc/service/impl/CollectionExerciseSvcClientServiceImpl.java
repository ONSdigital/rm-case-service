package uk.gov.ons.ctp.response.casesvc.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import uk.gov.ons.ctp.response.casesvc.service.CollectionExerciseSvcClientService;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * The service to retrieve a CollectionExercise
 */
@Slf4j
@CoverageIgnore
@Service
public class CollectionExerciseSvcClientServiceImpl implements CollectionExerciseSvcClientService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Qualifier("collectionExerciseRestUtility")
    @Autowired
    private RestUtility restUtility;

    @Autowired
    private ObjectMapper objectMapper;

    @Retryable(value = {RestClientException.class}, maxAttemptsExpression = "#{${retries.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
    @Override
    public CollectionExerciseDTO getCollectionExercise(final UUID collectionExerciseId) {
        UriComponents uriComponents = restUtility.createUriComponents(
                appConfig.getCollectionExerciseSvc().getCollectionExercisePath(), null, collectionExerciseId);

        HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

        log.debug("about to get to the CollectionExercise SVC with {}", collectionExerciseId);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity,
                String.class);

        CollectionExerciseDTO result = null;
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            try {
                result = objectMapper.readValue(responseBody, CollectionExerciseDTO.class);
            } catch (IOException e) {
                log.error(String.format("cause = %s - message = %s", e.getCause(), e.getMessage()));
            }
        }
        return result;
    }

    @Retryable(value = {RestClientException.class}, maxAttemptsExpression = "#{${retries.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
    @Override
    public List<CollectionExerciseDTO> getCollectionExercises(final String surveyId) {
        UriComponents uriComponents = restUtility.createUriComponents(
                appConfig.getCollectionExerciseSvc().getCollectionExerciseSurveyPath(), null, surveyId);
        HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uriComponents.toUri(), HttpMethod.GET, httpEntity, String.class);
        List<CollectionExerciseDTO> result = null;
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            try {
                result = objectMapper.readValue(responseBody, new TypeReference<List<CollectionExerciseDTO>>() {
                });
            } catch (IOException e) {
                log.error(String.format("cause = %s - message = %s", e.getCause(), e.getMessage()));
            }
        }
        return result;
    }
}
