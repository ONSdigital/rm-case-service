package uk.gov.ons.ctp.response.caseframe.service;

import java.math.BigInteger;
import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;

/**
 * The Case Service interface defines all business behaviours for operations on
 * the Case entity model.
 */
public interface CaseService extends CTPService {

  /**
   * Find Case entities associated with an Address.
   *
   * @param uprn UPRN for an address
   * @return List of Case entities or empty List
   */
  List<Case> findCasesByUprn(Integer uprn);

  /**
   * Find Case entity by Questionnaire Id.
   *
   * @param qid Unique Questionnaire Id
   * @return Case object or null
   */
  Case findCaseByQuestionnaireId(Integer qid);

  /**
   * Find Case entity by status and actionplanid
   *
   * @param status the case status to find by
   * @param actionPlanId id of the action plan to find by
   * @return all the matching cases
   */
  List<BigInteger> findCaseIdsByStateAndActionPlanId(String state, Integer actionPlanId);

  /**
   * Find Case entity by unique Id.
   *
   * @param caseId Unique Case Id
   * @return Case object or null
   */
  Case findCaseByCaseId(Integer caseId);

  /**
   * Find CaseEvent entities associated with a Case.
   *
   * @param caseId Case Id
   * @return List of CaseEvent entities or empty List
   */
  List<CaseEvent> findCaseEventsByCaseId(Integer caseId);

  /**
   * Create a CaseEvent being given the parent CaseId and the CaseEvent to create
   * @param caseEvent CaseEvent to be created
   * @return the created CaseEvent
   */
  CaseEvent createCaseEvent(CaseEvent caseEvent);
}
