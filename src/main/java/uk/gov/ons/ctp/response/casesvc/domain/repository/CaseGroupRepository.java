package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseAction;

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
   * @param sampleUnitRef
   * @param collectionExerciseId
   * @return
   */
  List<CaseGroup> findBySampleUnitRefAndCollectionExerciseId(
      String sampleUnitRef, UUID collectionExerciseId);

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

  Long countCaseGroupByCollectionExerciseId(UUID collectionExerciseId);

  @Query(
      "SELECT count (*) FROM CaseGroup cg, Case c "
          + "WHERE c.caseGroupFK=cg.caseGroupPK "
          + "AND cg.collectionExerciseId = :collectionExerciseId "
          + "AND (cg.status='NOTSTARTED' OR cg.status='INPROGRESS')")
  Long findCasesAgainstCollectionExerciseID(
      @Param("collectionExerciseId") UUID collectionExerciseId);

  /**
   * find case groups for a collection exercise
   *
   * @param collectionExerciseId the collection exercise id
   * @return the list of case actions
   */
  List<CaseGroup> findCaseGroupByCollectionExerciseId(UUID collectionExerciseId);

  /**
   * find case for action
   *
   * @param caseId case id
   * @return case action
   */
  @Query(
      value =
          "SELECT new uk.gov.ons.ctp.response.casesvc.representation.action.CaseAction"
              + "(cg.collectionExerciseId, c.id AS caseId,cg.partyId, cg.sampleUnitRef, cg.sampleUnitType, "
              + "cg.status, cg.surveyId, c.sampleUnitId AS sampleUnitId, c.collectionInstrumentId, iac.iac, "
              + "c.activeEnrolment) "
              + "FROM CaseGroup cg, Case c "
              + "LEFT JOIN CaseIacAudit iac ON iac.caseFK=c.casePK "
              + "WHERE cg.caseGroupPK=c.caseGroupFK AND c.state='ACTIONABLE' "
              + "AND c.id = :caseId ")
  CaseAction findByCaseId(@Param("caseId") UUID caseId);

  /**
   * find cases for action
   *
   * @param collectionExerciseId the collection exercise id
   * @param activeEnrolment active enrolment true or false
   * @return the list of case actions
   */
  @Query(
      value =
          "SELECT new uk.gov.ons.ctp.response.casesvc.representation.action.CaseAction"
              + "(cg.collectionExerciseId, c.id AS caseId,cg.partyId, cg.sampleUnitRef, cg.sampleUnitType, "
              + "cg.status, cg.surveyId, c.sampleUnitId AS sampleUnitId, c.collectionInstrumentId, iac.iac, "
              + "c.activeEnrolment) "
              + "FROM CaseGroup cg, Case c "
              + "LEFT JOIN CaseIacAudit iac ON iac.caseFK=c.casePK "
              + "AND iac.createdDateTime = (SELECT max(createdDateTime) from "
              + "CaseIacAudit z WHERE z.caseFK=c.casePK)"
              + "WHERE cg.caseGroupPK=c.caseGroupFK AND c.state='ACTIONABLE' "
              + "AND cg.collectionExerciseId = :collectionExerciseId "
              + "AND c.activeEnrolment = :activeEnrolment ORDER BY cg.sampleUnitRef, iac.createdDateTime DESC")
  List<CaseAction> findByCollectionExerciseIdAndActiveEnrolment(
      @Param("collectionExerciseId") UUID collectionExerciseId,
      @Param("activeEnrolment") boolean activeEnrolment);

  /**
   * find cases for action
   *
   * @param caseIds list of cases
   * @return the list of case actions
   */
  @Query(
      value =
          "SELECT new uk.gov.ons.ctp.response.casesvc.representation.action.CaseAction"
              + "(cg.collectionExerciseId, c.id AS caseId,cg.partyId, cg.sampleUnitRef, cg.sampleUnitType, "
              + "cg.status, cg.surveyId, c.sampleUnitId AS sampleUnitId, c.collectionInstrumentId, iac.iac, "
              + "c.activeEnrolment) "
              + "FROM CaseGroup cg, Case c "
              + "LEFT JOIN CaseIacAudit iac ON iac.caseFK=c.casePK "
              + "WHERE cg.caseGroupPK=c.caseGroupFK AND c.state='ACTIONABLE' "
              + "AND c.id IN :caseIds ")
  List<CaseAction> findByCaseIdIn(@Param("caseIds") List<UUID> caseIds);

  /**
   * find cases for action
   *
   * @param collectionExerciseID UUID of the collection exercise to delete casegroup entities
   */
  @Modifying
  @Query("DELETE FROM CaseGroup cg WHERE cg.collectionExerciseId = :collectionExerciseId")
  void deleteCaseGroupsByCollectionExerciseId(
      @Param("collectionExerciseId") UUID collectionExerciseID);
}
