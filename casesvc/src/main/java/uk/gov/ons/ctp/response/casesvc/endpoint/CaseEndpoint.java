package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseGroupEndpoint.ERRORMSG_CASEGROUPNOTFOUND;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.*;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;

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
   * @param caseId to find by
   * @return the case found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
  public ResponseEntity<?> findCaseById(@PathVariable("caseId") final UUID caseId,
                                        @RequestParam(value = "caseevents", required = false) boolean caseevents,
                                        @RequestParam(value = "iac", required = false) boolean iac)
          throws CTPException {
    log.info("Entering findCaseById with {}", caseId);
    Case caseObj = caseService.findCaseById(caseId);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s case id %s", ERRORMSG_CASENOTFOUND, caseId));
    }

    CaseDetailsDTO caseDetailsDTO = mapperFacade.map(caseObj, CaseDetailsDTO.class);

    CaseGroup parentCaseGroup = caseGroupService.findCaseGroupByCaseGroupPK(caseObj.getCaseGroupFK());
    caseDetailsDTO.setCaseGroup(mapperFacade.map(parentCaseGroup, CaseGroupDTO.class));

    if (caseevents) {
      List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseFK(caseObj.getCasePK());
      List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
      caseDetailsDTO.setCaseEvents(caseEventDTOs);
    }

    if (!iac) {
      caseDetailsDTO.setIac(null);
    }

    return ResponseEntity.ok(caseDetailsDTO);
  }

  /**
   * the GET endpoint to find Cases by partyid UUID
   *
   * @param partyId to find by
   * @return the case found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/partyid/{partyId}", method = RequestMethod.GET)
  public ResponseEntity<?> findCasesByPartyId(@PathVariable("partyId") final UUID partyId,
                                        @RequestParam(value = "caseevents", required = false) boolean caseevents,
                                        @RequestParam(value = "iac", required = false) boolean iac)
          throws CTPException {
    log.info("Entering findCasesByPartyId with {}", partyId);
    List<Case> casesList = caseService.findCasesByPartyId(partyId);

    if (CollectionUtils.isEmpty(casesList)) {
      return ResponseEntity.noContent().build();
    } else {
      List<CaseDetailsDTO> casesDetailedListDTO = mapperFacade.mapAsList(casesList, CaseDetailsDTO.class);
      for (CaseDetailsDTO caze: casesDetailedListDTO) {
        if (!iac) {
          caze.setIac(null);
        }

        CaseGroup parentCaseGroup = caseGroupService.findCaseGroupById(caze.getCaseGroupId());
        caze.setCaseGroup(mapperFacade.map(parentCaseGroup, CaseGroupDTO.class));

//        if (caseevents) {
//          List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseFK(caze.get);
//          List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
//          caseDetailsDTO.setCaseEvents(caseEventDTOs);
//        }
      }

      // TODO Add the case events
      return ResponseEntity.ok(casesDetailedListDTO);
    }
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
    caseEvent.setCaseFK(caseObj.getCasePK());
    caseEvent.setCategory(CategoryDTO.CategoryType.IAC_AUTHENTICATED);
    caseEvent.setCreatedBy(Constants.SYSTEM);
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    caseEvent.setDescription(cat.getShortDescription());
    
    caseService.createCaseEvent(caseEvent,caseObj);
  }

  /**
   * the GET endpoint to find cases by case group UUID
   *
   * @param caseId UUID to find by
   * @return the case events found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/casegroupid/{caseId}", method = RequestMethod.GET)
  public ResponseEntity<?> findCasesInCaseGroup(@PathVariable("caseId") final UUID caseId,
                                                @RequestParam(name = "caseevents", defaultValue = "false") final boolean caseevents) 
                                                throws CTPException {
    log.info("Entering findCasesInCaseGroup with {}", caseId);

    CaseGroup caseGroup = caseGroupService.findCaseGroupById(caseId);
    if (caseGroup == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s casegroup id %s", ERRORMSG_CASEGROUPNOTFOUND, caseId));
    }

    // TODO return the case group details
    // TODO play with caseevents
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
  public ResponseEntity<?> findCaseEventsByCaseId(@PathVariable("caseId") final UUID caseId) throws CTPException {
    
    log.info("Entering findCaseByCaseId with {}", caseId);
    Case caze = caseService.findCaseById(caseId);
	
    if (caze == null) {
        throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
            String.format("%s case id %s", ERRORMSG_CASENOTFOUND, caseId.toString()));
      }
	
    Integer casePK = caze.getCasePK();
	
    log.info("Entering findCaseEventsByCaseId with {}", casePK);
 
    List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseFK(casePK);
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
  public ResponseEntity<?> createCaseEvent(@PathVariable("caseId") final UUID caseId,
                                      @RequestBody @Valid final CaseEventCreationRequestDTO caseEventCreationRequestDTO,
                                      BindingResult bindingResult) throws CTPException {
    log.info("Entering createCaseEvent with caseId {} and requestObject {}", caseId, caseEventCreationRequestDTO);

    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for case event creation: ", bindingResult);
    }

    caseEventCreationRequestDTO.setCaseId(caseId);

    CaseEvent caseEvent = mapperFacade.map(caseEventCreationRequestDTO, CaseEvent.class);
    Case caseFound = caseService.findCaseById(caseId);
    if (caseFound == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s case id %s", ERRORMSG_CASENOTFOUND, caseId));
    }
    caseEvent.setCaseFK(caseFound.getCasePK());
    
    Case caze = null;
    if (caseEventCreationRequestDTO.getPartyId() != null){
      caze = new Case();
      caze.setPartyId(caseEventCreationRequestDTO.getPartyId());
    }

    Category category = categoryService.findCategory(caseEvent.getCategory());
    if (category.getNewCaseSampleUnitType() != null && caze == null) {
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED,
          String.format(EVENT_REQUIRES_NEW_CASE, caseId));
    }

    CaseEvent createdCaseEvent = caseService.createCaseEvent(caseEvent, caze);
    
    CaseEventDTO mappedCaseEvent = mapperFacade.map(createdCaseEvent, CaseEventDTO.class);
    mappedCaseEvent.setCaseId(caseId);
    mappedCaseEvent.setPartyId(caseEventCreationRequestDTO.getPartyId());
    
    // TODO Define URI
    return ResponseEntity.created(URI.create("TODO")).body(mappedCaseEvent);
  }


}
