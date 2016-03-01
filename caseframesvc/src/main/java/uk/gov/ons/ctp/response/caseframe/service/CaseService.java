package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;

/**
 * The interface defining the function of the Case service.
 * The application business logic should reside in it's implementation
 */
public interface CaseService extends CTPService {

    List<Case> findCasesByUprn(Integer uprn);

    Case findCaseByQuestionnaireId(Integer qid);

    Case findCaseByCaseId(Integer caseId);

    List<CaseEvent> findCaseEventsByCaseId(Integer caseId);
}
