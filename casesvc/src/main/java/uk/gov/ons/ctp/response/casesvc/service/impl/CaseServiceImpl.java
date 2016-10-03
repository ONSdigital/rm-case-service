package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.domain.model.Response;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public class CaseServiceImpl implements CaseService {

  private static final int TRANSACTION_TIMEOUT = 30;
  private static final String CASE_RESPONSE_RECEIVED_CREATEDBY = "SYSTEM";
  private static final String CASE_RESPONSE_RECEIVED_DESCRIPTION = "Questionnaire response received";

  /**
   * Spring Data Repository for Case entities.
   */
  @Inject
  private CaseRepository caseRepo;

  /**
   * Spring Data Repository for CaseEvent Entities.
   */
  @Inject
  private CaseEventRepository caseEventRepo;

  /**
   * Spring Data Repository for Category Entities.
   */
  @Inject
  private CategoryRepository categoryRepo;

  /**
   * ActionSVC client service
   */
  @Inject
  private ActionSvcClientService actionSvcClientService;

  @Inject
  private MapperFacade mapperFacade;
  
  /**
   * Notification publishing service for Case life cycle events
   */
  @Inject
  private CaseNotificationPublisher notificationPublisher;

  @Override
  public Case findCaseByCaseId(final Integer caseId) {
    log.debug("Entering findCaseByCaseId");
    return caseRepo.findOne(caseId);
  }

  @Override
  public List<Case> findCasesByCaseGroupId(final Integer caseGroupId) {
    log.debug("Entering findCasesByCaseGroupId");
    return caseRepo.findByCaseGroupId(caseGroupId);
  }

  @Override
  public List<CaseEvent> findCaseEventsByCaseId(final Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId");
    return caseEventRepo.findByCaseIdOrderByCreatedDateTimeDesc(caseId);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public CaseEvent createCaseEvent(final CaseEvent caseEvent) {
    log.debug("Entering createCaseEvent");
    CaseEvent createdCaseEvent = null;

    Integer caseId = caseEvent.getCaseId();
    Case existingCase = caseRepo.findOne(caseId);

    if (existingCase != null) {
      caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
      createdCaseEvent = caseEventRepo.save(caseEvent);

      Category category = categoryRepo.findOne(caseEvent.getCategoryId());
      Boolean closeCase = category.getCloseCase();

      CategoryDTO.CategoryName reasonForClosure = CategoryDTO.CategoryName.getEnumByLabel(category.getName());

      if (Boolean.TRUE.equals(closeCase)) {
        closeCase(existingCase);
      }

      if (reasonForClosure == CategoryDTO.CategoryName.QUESTIONNAIRE_RESPONSE) {
        markQuestionnairesAsResponded(caseId);
      }

      String actionType = category.getGeneratedActionType();
      if (!StringUtils.isEmpty(actionType)) {
        actionSvcClientService.createAndPostAction(actionType, caseId, caseEvent.getCreatedBy());
      }
    }
    return createdCaseEvent;
  }

  private void closeCase(Case caze) {
    caseRepo.setState(caze.getCaseId(), CaseDTO.CaseState.RESPONDED.name());
    actionSvcClientService.cancelActions(caze.getCaseId());
    //XXX
//    notificationPublisher
//        .sendNotifications(Arrays.asList(new CaseNotification(caze.getCaseId(), caze.getActionPlanId(), RESPONDED)));

  }

  /**
   * mark all case related questionnaires as responded
   *
   * @param caseId Integer case ID
   */
  private void markQuestionnairesAsResponded(int caseId) {
    Timestamp currentTime = DateTimeUtil.nowUTC();
    //XXX
//    List<Questionnaire> associatedQuestionnaires = questionnaireRepo.findByCaseId(caseId);
//    for (Questionnaire questionnaire : associatedQuestionnaires) {
//      questionnaireRepo.setResponseDatetimeFor(currentTime, questionnaire.getQuestionnaireId());
//    }
  }
  
  /**
   * Update a Questionnaire to record a response has been received in the Survey
   * Data Exchange. Process a CaseEvent object for this event.
   *
   * @param questionnaireId Integer Unique Id of questionnaire
   * @return Updated Questionnaire object or null
   */
  public Case recordResponse(String caseRef) {
    Case caze = caseRepo.findByCaseRef(caseRef);
    if (caze != null) {
      // create a Response obj and associate it with this case
      Response response = Response.builder()
          .inboundChannel(InboundChannel.PAPER.name())
          .dateTime(DateTimeUtil.nowUTC()).build();

      //XXX is the response saved?
      caze.getResponses().add(response);
      caseRepo.save(caze); 
      
      // create a CaseEvent for cancelling Actions and closing a Case
      CaseEventDTO caseEventDTO = new CaseEventDTO();
      caseEventDTO.setCaseId(caze.getCaseId());
      CaseEvent caseEvent = mapperFacade.map(caseEventDTO, CaseEvent.class);
      String categoryName = CategoryDTO.CategoryName.QUESTIONNAIRE_RESPONSE.getLabel();
      Category category = categoryRepo.findByName(categoryName);
      caseEvent.setCategoryId(category.getCategoryId());
      caseEvent.setCreatedBy(CASE_RESPONSE_RECEIVED_CREATEDBY);
      caseEvent.setDescription(CASE_RESPONSE_RECEIVED_DESCRIPTION);
      createCaseEvent(caseEvent);
    }
    return caze;
  }
}
