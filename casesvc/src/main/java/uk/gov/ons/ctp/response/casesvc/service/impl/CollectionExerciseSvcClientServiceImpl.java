package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.service.CollectionExerciseSvcClientService;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

/**
 * The service to retrieve a CollectionExercise
 */
@Service
public class CollectionExerciseSvcClientServiceImpl implements CollectionExerciseSvcClientService {

  @Autowired
  private AppConfig appConfig;

  @Autowired
  @Qualifier("collectionExerciseSvcClient")
  private RestClient collectionExerciseServiceClient;


  @Override
  public CollectionExerciseDTO getCollectionExercise(UUID collectionExerciseId) {
    return collectionExerciseServiceClient.getResource(appConfig.getCollectionExerciseSvc().getCollectionExercisePath(),
            CollectionExerciseDTO.class, collectionExerciseId);
  }
}
