package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.caseframe.representation.CategoryDTO;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;
import uk.gov.ons.ctp.response.caseframe.service.QuestionnaireService;

/**
 * A QuestionnaireService implementation which encapsulates all business logic
 * operating on the Questionnaire entity model.
 */
@Named
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public final class QuestionnaireServiceImpl implements QuestionnaireService {

  /**
   * Text associated with failure of Response data received operation
   */
  public static final String OPERATION_FAILED = "Response operation failed for questionnaireid";
  /**
   * Timeout for response received transaction
   */
  private static final int TRANSACTION_TIMEOUT = 30;
  /**
   * The user that created the CaseEvent for a Questionnaire response received
   */
  private static final String QUESTIONNAIRE_RESPONSE_RECEIVED_CREATEDBY = "SYSTEM";
  /**
   * The description of the CaseEvent for a Questionnaire response received
   */
  private static final String QUESTIONNAIRE_RESPONSE_RECEIVED_DESCRIPTION = "Questionnaire response received";
  
  /**
   * Spring Data Repository for Case service.
   */
  @Inject
  private CaseService caseService;
  
  @Inject
  private MapperFacade mapperFacade;
  
  /**
   * Spring Data Repository for Questionnaire Entities.
   */
  @Inject
  private QuestionnaireRepository questionnaireRepo;

  /**
   * Find Questionnaire entity by Internet Access Code.
   *
   * @param iac Unique Internet Access Code
   * @return Questionnaire object or null
   */
  @Override
  public Questionnaire findQuestionnaireByIac(final String iac) {
    log.debug("Entering findQuestionnaireByIac with {}", iac);
    return questionnaireRepo.findByIac(iac);
  }

  /**
   * Find Questionnaire entities associated with a Case.
   *
   * @param caseId Unique Case Id
   * @return List of Questionnaire entities or empty List
   */
  @Override
  public List<Questionnaire> findQuestionnairesByCaseId(final Integer caseId) {
    log.debug("Entering findQuestionnairesByCaseId with {}", caseId);
    return questionnaireRepo.findByCaseId(caseId);
  }

  /**
   * Update a Questionnaire to record a response has been
   * received in the Survey Data Exchange. 
   * Process a CaseEvent object for this event.
   *
   * @param questionnaireId Integer Unique Id of questionnaire
   * @return Updated Questionnaire object or null
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  public Questionnaire recordResponse(final Integer questionnaireId) {
    Questionnaire questionnaire = questionnaireRepo.findOne(questionnaireId);
    if (questionnaire != null) {      
      // create a CaseEvent for cancelling Actions and closing a Case
      CaseEventDTO caseEventDTO = new CaseEventDTO();
      caseEventDTO.setCaseId(questionnaire.getCaseId());
      CaseEvent caseEvent = mapperFacade.map(caseEventDTO, CaseEvent.class);
      caseEvent.setCategory(CategoryDTO.CategoryName.QUESTIONNAIRE_RESPONSE.getLabel());
      caseEvent.setCreatedBy(QUESTIONNAIRE_RESPONSE_RECEIVED_CREATEDBY);
      caseEvent.setDescription(QUESTIONNAIRE_RESPONSE_RECEIVED_DESCRIPTION);
      caseService.createCaseEvent(caseEvent);  
    } else {
      // Questionnaire does not exist to record response
      return null;
    }
    return questionnaire; 
  }
}
