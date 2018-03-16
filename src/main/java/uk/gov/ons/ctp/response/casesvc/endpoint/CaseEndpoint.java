package uk.gov.ons.ctp.response.casesvc.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitBase;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CreatedCaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The REST endpoint controller for CaseSvc Cases
 */
@RestController
@RequestMapping(value = "/cases", produces = "application/json")
@Slf4j
public final class CaseEndpoint implements CTPEndpoint {

  public static final String CATEGORY_ACCESS_CODE_AUTHENTICATION_ATTEMPT_NOT_FOUND =
      "Category ACCESS_CODE_AUTHENTICATION_ATTEMPT does not exist";
  public static final String ERRORMSG_CASENOTFOUND = "Case not found for";
  public static final String EVENT_REQUIRES_NEW_CASE = "Event requested for "
          + "case %s requires additional data - new Case details";

  private static final String CASE_ID = "%s case id %s";

  @Autowired
  private CaseGroupService caseGroupService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private CaseService caseService;

  @Autowired
  private InternetAccessCodeSvcClientService internetAccessCodeSvcClientService;

  @Qualifier("caseSvcBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to find a Case by UUID
   *
   * @param caseId to find by
   * @param caseevents flag used to return or not CaseEvents
   * @param iac flag used to return or not the iac
   * @return the case found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
  public ResponseEntity<CaseDetailsDTO> findCaseById(
          @PathVariable("caseId") final UUID caseId,
          @RequestParam(value = "caseevents", required = false) boolean
                  caseevents,
          @RequestParam(value = "iac", required = false) boolean iac)
          throws CTPException {
    log.info("Entering findCaseById with {}", caseId);
    Case caseObj = caseService.findCaseById(caseId);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(CASE_ID, ERRORMSG_CASENOTFOUND, caseId));
    }

    return ResponseEntity.ok(buildDetailedCaseDTO(caseObj, caseevents, iac));
  }

  /**
   * the GET endpoint to find Cases by partyid UUID
   *
   * @param partyId to find by
   * @param caseevents flag used to return or not CaseEvents
   * @param iac flag used to return or not the iac
   * @return the cases found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/partyid/{partyId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDetailsDTO>> findCasesByPartyId(
          @PathVariable("partyId") final UUID partyId,
          @RequestParam(value = "caseevents", required = false)
                  boolean caseevents,
          @RequestParam(value = "iac", required = false) boolean iac)
          throws CTPException {
    log.info("Entering findCasesByPartyId with {}", partyId);
    List<Case> casesList = caseService.findCasesByPartyId(partyId);

    if (CollectionUtils.isEmpty(casesList)) {
      return ResponseEntity.noContent().build();
    } else {
      List<CaseDetailsDTO> resultList = new ArrayList<>();
      for (Case caze: casesList) {
        resultList.add(buildDetailedCaseDTO(caze, caseevents, iac));
      }
      return ResponseEntity.ok(resultList);
    }
  }

  /**
   * the GET endpoint to find a Case by IAC
   *
   * @param iac to find by
   * @param caseevents flag used to return or not CaseEvents
   * @param iacFlag flag used to return or not the iac
   * @return the case found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/iac/{iac}", method = RequestMethod.GET)
  public ResponseEntity<CaseDetailsDTO> findCaseByIac(@PathVariable("iac") final String iac,
                                         @RequestParam(value = "caseevents", required = false) final boolean caseevents,
                                         @RequestParam(value = "iac", required = false) final boolean iacFlag)
          throws CTPException {
    log.info("Entering findCaseByIac with {}", iac);
    Case caseObj = caseService.findCaseByIac(iac);
    if (caseObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format("%s iac %s", ERRORMSG_CASENOTFOUND, iac));
    }

    createNewEventForAccessCodeAuthAttempt(caseObj);

    return ResponseEntity.ok(buildDetailedCaseDTO(caseObj, caseevents, iacFlag));
  }

  /**
   * the POST endpoint to generate and return a new iac
   *
   * @param collectionexerciseid to generate iac for
   * @param ruref to generate iac for
   * @return the case created
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/iac/{collectionexerciseid}/{ruref}", method = RequestMethod.POST)
  public ResponseEntity<CaseDetailsDTO> generateNewIac(@PathVariable("collectionexerciseid") final UUID collectionexerciseid,
                                                       @PathVariable("ruref") final String ruref)
          throws CTPException {
    log.info("Entering generateNewIac with collectionexerciseid {} and ruref {}", collectionexerciseid, ruref);

    CaseGroup caseGroup = caseGroupService.findCaseGroupByCollectionExerciseIdAndRuRef(collectionexerciseid, ruref);
    if (caseGroup == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format("CaseGroup not found for collectionexerciseid %s and ruref %s",
                      collectionexerciseid, ruref));
    }
    List<Case> casesList = caseService.findCasesByCaseGroupFK(caseGroup.getCaseGroupPK());
    Case latestCase = casesList.get(casesList.size() - 1);
    List<String> iacs = internetAccessCodeSvcClientService.generateIACs(1);

    SampleUnitBase sampleUnitBase = new SampleUnitBase(caseGroup.getSampleUnitRef(), Character.toString('B'),
            caseGroup.getPartyId().toString(), latestCase.getCollectionInstrumentId().toString());
    Case newCase = caseService.generateNewCase(sampleUnitBase, caseGroup, iacs.get(0), latestCase.getActionPlanId());
    log.info("Successfully created new case {}", newCase.getId());
    return ResponseEntity.ok(buildDetailedCaseDTO(newCase, false, true));
  }

  /**
   * the GET endpoint to find cases by case group UUID
   *
   * @param casegroupId UUID to find by
   * @return the case events found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/casegroupid/{casegroupId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDTO>> findCasesInCaseGroup(@PathVariable("casegroupId") final UUID casegroupId)
          throws CTPException {
    log.info("Entering findCasesInCaseGroup with {}", casegroupId);

    CaseGroup caseGroup = caseGroupService.findCaseGroupById(casegroupId);
    if (caseGroup == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("CaseGroup not found for casegroup id %s", casegroupId));
    }

    List<Case> casesList = caseService.findCasesByCaseGroupFK(caseGroup.getCaseGroupPK());
    if (CollectionUtils.isEmpty(casesList)) {
      return ResponseEntity.noContent().build();
    } else {
      List<CaseDTO> caseDTOs = mapperFacade.mapAsList(casesList, CaseDTO.class);
      return ResponseEntity.ok(caseDTOs);
    }
  }

  /**
   * the GET endpoint to find case events by case id
   *
   * @param caseId to find by
   * @return the case events found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{caseId}/events", method = RequestMethod.GET)
  public ResponseEntity<List<CaseEventDTO>> findCaseEventsByCaseId(@PathVariable("caseId") final UUID caseId)
          throws CTPException {
    log.info("Entering findCaseEventsByCaseId with {}", caseId);
    Case caze = caseService.findCaseById(caseId);
    if (caze == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format(CASE_ID, ERRORMSG_CASENOTFOUND, caseId.toString()));
    }

    List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseFK(caze.getCasePK());
    List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
    return CollectionUtils.isEmpty(caseEventDTOs)
            ? ResponseEntity.noContent().build() : ResponseEntity.ok(caseEventDTOs);
  }

  /**
   * To create a case event being given a parent case and json to describe the
   * case event to be created
   *
   * @param caseId the parent case
   * @param caseEventCreationRequestDTO the CaseEventDTO describing the case event to be created
   * @param bindingResult the bindingResult used to validate requests
   * @return the created CaseEventDTO
   * @throws CTPException on failure to create CaseEvent
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(value = "/{caseId}/events", method = RequestMethod.POST)
  public ResponseEntity<CreatedCaseEventDTO> createCaseEvent(@PathVariable("caseId") final UUID caseId,
                                      @RequestBody @Valid final CaseEventCreationRequestDTO caseEventCreationRequestDTO,
                                      BindingResult bindingResult) throws CTPException, InvalidRequestException {
    log.debug("Entering createCaseEvent with caseId {} and requestObject {}", caseId, caseEventCreationRequestDTO);

    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for case event creation: ", bindingResult);
    }

    CaseEvent caseEvent = mapperFacade.map(caseEventCreationRequestDTO, CaseEvent.class);
    Case caseFound = caseService.findCaseById(caseId);
    log.debug("caseFound is {}", caseFound);
    if (caseFound == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(CASE_ID, ERRORMSG_CASENOTFOUND, caseId));
    }
    caseEvent.setCaseFK(caseFound.getCasePK());

    Case caze = null;
    if (caseEventCreationRequestDTO.getPartyId() != null) {
      caze = new Case();
      caze.setPartyId(caseEventCreationRequestDTO.getPartyId());
    }

    Category category = categoryService.findCategory(caseEvent.getCategory());
    log.debug("category is {}", category);
    if (category.getNewCaseSampleUnitType() != null && caze == null) {
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED,
          String.format(EVENT_REQUIRES_NEW_CASE, caseId));
    }

    CaseEvent createdCaseEvent = caseService.createCaseEvent(caseEvent, caze);

    CreatedCaseEventDTO mappedCaseEvent = mapperFacade.map(createdCaseEvent, CreatedCaseEventDTO.class);
    mappedCaseEvent.setCaseId(caseId);
    mappedCaseEvent.setPartyId(caseEventCreationRequestDTO.getPartyId());

    String newResourceUrl = ServletUriComponentsBuilder
        .fromCurrentRequest().buildAndExpand(mappedCaseEvent.getCaseId()).toUri().toString();

    return ResponseEntity.created(URI.create(newResourceUrl)).body(mappedCaseEvent);
  }

  /**
   * Creates a new event for the Access Code Authorisation Attempt
   * @param caze Case Object to be used in CaseDTO
   * @param caseevents If caseevents exist
   * @param iac If IAC exists
   * @return CaseDetailsDTO caseDetails object
   */
  private CaseDetailsDTO buildDetailedCaseDTO(Case caze, boolean caseevents, boolean iac) {
    CaseDetailsDTO caseDetailsDTO = mapperFacade.map(caze, CaseDetailsDTO.class);

    CaseGroup parentCaseGroup = caseGroupService.findCaseGroupByCaseGroupPK(caze.getCaseGroupFK());
    caseDetailsDTO.setCaseGroup(mapperFacade.map(parentCaseGroup, CaseGroupDTO.class));

    if (caseevents) {
      List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseFK(caze.getCasePK());
      List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
      caseDetailsDTO.setCaseEvents(caseEventDTOs);
    }

    if (!iac) {
      caseDetailsDTO.setIac(null);
    }

    return caseDetailsDTO;
  }

  /**
   * Creates a new event for the Access Code Authorisation Attempt
   * @param caseObj Case Object for event to be created
   * @throws CTPException if IAC not found
   */
  private void createNewEventForAccessCodeAuthAttempt(Case caseObj) throws CTPException {
    Category cat = categoryService.findCategory(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
    if (cat == null) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, CATEGORY_ACCESS_CODE_AUTHENTICATION_ATTEMPT_NOT_FOUND);
    }

    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseFK(caseObj.getCasePK());
    caseEvent.setCategory(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
    caseEvent.setCreatedBy(Constants.SYSTEM);
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    caseEvent.setDescription(cat.getShortDescription());
    caseService.createCaseEvent(caseEvent, caseObj);
  }
}
