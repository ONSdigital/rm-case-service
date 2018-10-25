package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;

/** JPA Data Repository. */
@Repository
public interface CaseGroupRepository extends JpaRepository<CaseGroup, Integer> {
  /**
   * To find CaseGroup by UUID
   *
   * @param id the UUID of the CaseGroup
   * @return the matching CaseGroup
   */
  CaseGroup findById(UUID id);

  /**
   * To find CaseGroup by Survey ID
   *
   * @param surveyId the UUID of the survey
   * @return the matching CaseGroups
   */
  List<CaseGroup> findBySurveyId(UUID surveyId);

  /**
   * To find CaseGroup by party UUID
   *
   * @param partyId the UUID of the Party
   * @return the matching CaseGroups
   */
  List<CaseGroup> findByPartyId(UUID partyId);

  /**
   * To find CaseGroup by party UUID where Collection Exercise in list supplied
   *
   * @param partyId the UUID of the Party
   * @param collExs the list of Collection exercises for a survey
   * @return the matching CaseGroups
   */
  @Query(
      "SELECT c FROM CaseGroup c WHERE c.partyId = :partyId AND c.collectionExerciseId IN :collExs")
  List<CaseGroup> retrieveByPartyIdInListOfCollEx(
      @Param("partyId") UUID partyId, @Param("collExs") List<UUID> collExs);

  CaseGroup findCaseGroupByCollectionExerciseIdAndSampleUnitRef(
      UUID collectionExerciseId, String ruRef);
}
