package uk.gov.ons.ctp.response.casesvc.service;

import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

import java.util.List;
import java.util.UUID;

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

  /**
   * Returns all CollectionExercises for a given survey ID
   * @param surveyId the survey ID to search by
   * @return the list of Collection Exercises
   */
  List<CollectionExerciseDTO> getCollectionExercises(String surveyId);
}
