package uk.gov.ons.ctp.response.casesvc.service;

import java.util.UUID;

import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

/**
 * The service to retrieve a CollectionExercise
 */
public interface CollectionExerciseSvcClientService {
  /**
   * Returns the CollectionExercise for a given UUID
   * @param collectionExerciseId the UUID to search by
   * @return the asscoaited CollectionExercise
   */
  CollectionExerciseDTO getCollectionExercise(UUID collectionExerciseId);
}