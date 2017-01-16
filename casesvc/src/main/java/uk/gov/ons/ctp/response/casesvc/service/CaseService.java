package uk.gov.ons.ctp.response.casesvc.service;

import java.sql.Timestamp;
import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;

/**
 * The Case Service interface defines all business behaviours for operations on
 * the Case entity model.
 */
public interface CaseService extends CTPService {

  /**
   * Find the cases in a casegroup
   * @param caseGroupId the group
   * @return the cases in the group
   */
  List<Case> findCasesByCaseGroupId(final Integer caseGroupId);

  /**
   * Find Case entity by unique Id.
   *
   * @param caseId Unique Case Id
   * @return Case object or null
   */
  Case findCaseByCaseId(Integer caseId);

  /**
   * Find Case entity by unique caseRef.
   *
   * @param caseRef Unique caseRef
   * @return Case object or null
   */
  Case findCaseByCaseRef(String caseRef);

  /**
   * Find Case entity by IAC
   *
   * @param iac The IAC 
   * @return Case object or null
   */
  Case findCaseByIac(String iac);
  
  /**
   * Find CaseEvent entities associated with a Case.
   *
   * @param caseId Case Id
   * @return List of CaseEvent entities or empty List
   */
  List<CaseEvent> findCaseEventsByCaseId(Integer caseId);

  /**
   * Create a CaseEvent from the details provided in the passed CaseEvent. Some events will also as a side effect
   * create a new case - if so the details must be provided in the newCase argument, otherwise it may remain null.
   * If the newCase is passed it must also contain the contact details for the new case.
   *
   * @param caseEvent CaseEvent to be created
   * @param newCase optional case object containing partial details of the case to be created by this event.
   * @return the created CaseEvent
   */
  CaseEvent createCaseEvent(CaseEvent caseEvent, Case newCase);

  /**
   * Create a CaseEvent for the specific scenario of an incoming CaseReceipt (sent by the SDX Gateway and containing the
   * responseDateTime of the online/paper response)
   *
   * @param caseEvent CaseEvent to be created
   * @param newCase optional case object containing partial details of the case to be created by this event.
   * @param timestamp timestamp equals to the incoming CaseReceipt's responseDateTime
   * @return the created CaseEvent
   */
  CaseEvent createCaseEvent(CaseEvent caseEvent, Case newCase, Timestamp timestamp);

  /**
   * Not sure this is the best place for this method, but .. several parts of case svc need to build a 
   * CaseNotification for a Case and need the services of the ActionPlanMappingService to get the actionPlanId
   * This method just creates a CaseNotification
   * @param caze The Case
   * @param transitionEvent the event to inform the recipient of
   * @return the newly created notification object
   */
  CaseNotification prepareCaseNotification(Case caze, CaseDTO.CaseEvent transitionEvent);
}
