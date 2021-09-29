package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseAction;

public interface CaseActionRepository extends JpaRepository<CaseAction, Integer> {

  /**
   * find cases for action
   *
   * @param collectionExerciseId the collection exercise id
   * @return the list of case actions
   */
  List<CaseAction> findByCollectionExerciseId(UUID collectionExerciseId);

  /**
   * find case for action
   *
   * @param caseId case id
   * @return case action
   */
  CaseAction findByCaseId(UUID caseId);

  /**
   * find cases for action
   *
   * @param collectionExerciseId the collection exercise id
   * @param activeEnrolment active enrolment true or false
   * @return the list of case actions
   */
  List<CaseAction> findByCollectionExerciseIdAndActiveEnrolment(
      UUID collectionExerciseId, boolean activeEnrolment);

  /**
   * find cases for action
   *
   * @param caseId list of cases
   * @return the list of case actions
   */
  List<CaseAction> findByCaseIdIn(List<UUID> caseId);
}
