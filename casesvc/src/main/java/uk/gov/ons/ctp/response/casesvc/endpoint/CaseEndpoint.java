package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;

import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseGroupEndpoint.ERRORMSG_CASEGROUPNOTFOUND;

/**
 * The REST endpoint controller for CaseSvc Cases
 */
@RestController
@RequestMapping(value = "/cases", produces = "application/json")
@Slf4j
public final class CaseEndpoint implements CTPEndpoint {

  public static final String ERRORMSG_CASENOTFOUND = "Case not found for";
  public static final String EVENT_REQUIRES_NEW_CASE = "Event requested for case %s requires additional data - new Case details";

  @Autowired
  private CaseGroupService caseGroupService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private CaseService caseService;

  @Qualifier("caseSvcBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to find a Case by UUID
   *
   * @param id to find by
   * @return the case found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> findCaseById(@PathVariable("id") final String id,
                                            @RequestParam(value = "caseevents", defaultValue = "false") boolean caseevents)
          throws CTPException {
    log.info("Entering findCaseById with {}", id);
    Case caseObj = caseService.findCaseById(id);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format("%s case id %s", ERRORMSG_CASENOTFOUND, id));
    }

    // TODO find the CaseGroup info
    if (caseevents) {
//      List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseId(id);
    }
    // TODO Build the full DTO

    return ResponseEntity.ok(mapperFacade.map(caseObj, CaseDTO.class));
  }

  /**
   * the GET endpoint to find a Case by IAC
   *
   * @param iac to find by
   * @return the case found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/iac/{iac}", method = RequestMethod.GET)
  public ResponseEntity<?> findCaseByIac(@PathVariable("iac") final String iac) throws CTPException {
    log.info("Entering findCaseByIac with {}", iac);
    Case caseObj = caseService.findCaseByIac(iac);


    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s iac id %s", ERRORMSG_CASENOTFOUND, iac));
    }
    
    createNewEventForIACAuthenticated(caseObj);
    
    return ResponseEntity.ok(mapperFacade.map(caseObj, CaseDTO.class));
  }

  private void createNewEventForIACAuthenticated(Case caseObj) {
	Category cat = categoryService.findCategory(CategoryDTO.CategoryType.IAC_AUTHENTICATED);
    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseId(caseObj.getCasePK());
    caseEvent.setCategory(CategoryDTO.CategoryType.IAC_AUTHENTICATED);
    caseEvent.setCreatedBy(Constants.SYSTEM);
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    caseEvent.setDescription(cat.getShortDescription());
    
    caseService.createCaseEvent(caseEvent,caseObj);
  }

  /**
   * the GET endpoint to find cases by case group UUID
   *
   * @param id UUID to find by
   * @return the case events found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/casegroupid/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> findCasesInCaseGroup(@PathVariable("id") final String id)
          throws CTPException {
    log.info("Entering findCasesInCaseGroup with {}", id);

    CaseGroup caseGroup = caseGroupService.findCaseGroupById(id);
    if (caseGroup == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s casegroup id %s", ERRORMSG_CASEGROUPNOTFOUND, id));
    }

    List<Case> cases = caseService.findCasesByCaseGroupFK(caseGroup.getCaseGroupPK());
    List<CaseDTO> caseDTOs = mapperFacade.mapAsList(cases, CaseDTO.class);
    return CollectionUtils.isEmpty(caseDTOs) ?
            ResponseEntity.noContent().build() : ResponseEntity.ok(caseDTOs);
  }

  /**
   * the GET endpoint to find case events by case id
   *
   * @param caseId to find by
   * @return the case events found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{caseId}/events", method = RequestMethod.GET)
  public ResponseEntity<?> findCaseEventsByCaseId(@PathVariable("caseId") final Integer caseId) throws CTPException {
    log.info("Entering findCaseEventsByCaseId with {}", caseId);
    Case caseObj = caseService.findCaseByCasePK(caseId);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s case id %s", ERRORMSG_CASENOTFOUND, caseId));
    }
    List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseId(caseId);
    List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
    return CollectionUtils.isEmpty(caseEventDTOs) ?
            ResponseEntity.noContent().build() : ResponseEntity.ok(caseEventDTOs);
  }

  /**
   * To create a case event being given a parent case and json to describe the
   * case event to be created
   *
   * @param caseId the parent case
   * @param caseEventCreationRequestDTO the CaseEventDTO describing the case
   *          event to be created
   * @return the created CaseEventDTO
   * @throws CTPException on failure to create CaseEvent
   */
  @RequestMapping(value = "/{caseId}/events", method = RequestMethod.POST)
  public ResponseEntity<?> createCaseEvent(@PathVariable("caseId") final Integer caseId,
                                      @RequestBody @Valid final CaseEventCreationRequestDTO caseEventCreationRequestDTO,
                                      BindingResult bindingResult) throws CTPException {
    log.info("Entering createCaseEvent with caseId {} and requestObject {}", caseId, caseEventCreationRequestDTO);

    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for case event creation: ", bindingResult);
    }

    caseEventCreationRequestDTO.setCaseId(caseId);

    CaseEvent caseEvent = mapperFacade.map(caseEventCreationRequestDTO, CaseEvent.class);
    Case caze = mapperFacade.map(caseEventCreationRequestDTO.getCaseCreationRequest(), Case.class);
//    if (caze != null) {
// BRES new case id to be passed in?
//    }

    Category category = categoryService.findCategory(caseEvent.getCategory());
    if (category.getNewCaseSampleUnitType() != null && caze == null) {
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED,
          String.format(EVENT_REQUIRES_NEW_CASE, caseId));
    }

    CaseEvent createdCaseEvent = caseService.createCaseEvent(caseEvent, caze);
    if (createdCaseEvent == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s case id %s", ERRORMSG_CASENOTFOUND, caseId));
    }
    // TODO Define URI
    return ResponseEntity.created(URI.create("TODO")).body(mapperFacade.map(createdCaseEvent, CaseEventDTO.class));
  }

}
