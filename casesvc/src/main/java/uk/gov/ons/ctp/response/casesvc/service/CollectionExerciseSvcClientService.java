package uk.gov.ons.ctp.response.casesvc.service;

import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

public interface CollectionExerciseSvcClientService {
  
  CollectionExerciseDTO getCollectionExercise(final String collectionExerciseId);

}