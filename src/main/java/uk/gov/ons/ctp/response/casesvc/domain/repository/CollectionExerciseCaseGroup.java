package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.UUID;

public interface CollectionExerciseCaseGroup {

  UUID getCollectionExerciseId();

  UUID getSurveyId();

  String getStatus();
}
