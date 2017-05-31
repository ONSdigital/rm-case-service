package uk.gov.ons.ctp.response.casesvc.service;

import java.util.UUID;

import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

public interface CollectionExerciseSvcClientService {
  
  CollectionExerciseDTO getCollectionExercise(final UUID collectionExerciseId);

}