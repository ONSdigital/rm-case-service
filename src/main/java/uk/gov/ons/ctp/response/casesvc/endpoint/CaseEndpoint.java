package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
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
import uk.gov.ons.ctp.response.casesvc.utility.Constants;

/** The REST endpoint controller for CaseSvc Cases */
@RestController
@RequestMapping(value = "/cases", produces = "application/json")
@Slf4j
public final class CaseEndpoint implements CTPEndpoint {

  public static final String CATEGORY_ACCESS_CODE_AUTHENTICATION_ATTEMPT_NOT_FOUND =
      "Category ACCESS_CODE_AUTHENTICATION_ATTEMPT does not exist";
  public static final String ERRORMSG_CASENOTFOUND = "Case not found for";
  public static final String EVENT_REQUIRES_NEW_CASE =
      "Event requested for " + "case %s requires additional data - new Case details";

  private static final String CASE_ID = "%s case id %s";

  private CaseGroupService caseGroupService;
  private CategoryService categoryService;
  private CaseService caseService;
  private MapperFacade mapperFacade;

  @Autowired
  public CaseEndpoint(
      CaseGroupService caseGroupService,
      CategoryService categoryService,
      CaseService caseService,
      @Qualifier("caseSvcBeanMapper") MapperFacade mapperFacade) {
    this.caseGroupService = caseGroupService;
    this.categoryService = categoryService;
    this.caseService = caseService;
    this.mapperFacade = mapperFacade;
  }

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
      @RequestParam(value = "caseevents", required = false) boolean caseevents,
      @RequestParam(value = "iac", required = false) boolean iac)
      throws CTPException {
    log.info("Entering findCaseById with {}", caseId);
    Case caseObj = caseService.findCaseById(caseId);
    if (caseObj == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
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
   */
  @RequestMapping(value = "/partyid/{partyId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDetailsDTO>> findCasesByPartyId(
      @PathVariable("partyId") final UUID partyId,
      @RequestParam(value = "caseevents", required = false) boolean caseevents,
      @RequestParam(value = "iac", required = false) boolean iac) {
    log.info("Entering findCasesByPartyId with {}", partyId);
    List<Case> casesList = caseService.findCasesByPartyId(partyId);

    if (CollectionUtils.isEmpty(casesList)) {
      return ResponseEntity.noContent().build();
    } else {
      List<CaseDetailsDTO> resultList = new ArrayList<>();
      for (Case caze : casesList) {
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
  public ResponseEntity<CaseDetailsDTO> findCaseByIac(
      @PathVariable("iac") final String iac,
      @RequestParam(value = "caseevents", required = false) final boolean caseevents,
      @RequestParam(value = "iac", required = false) final boolean iacFlag)
      throws CTPException {
    log.info("Entering findCaseByIac with {}", iac);
    Case targetCase = caseService.findCaseByIac(iac);
    if (targetCase == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s iac %s", ERRORMSG_CASENOTFOUND, iac));
    }

    createNewEventForAccessCodeAuthAttempt(targetCase);

    return ResponseEntity.ok(buildDetailedCaseDTO(targetCase, caseevents, iacFlag));
  }

  /**
   * Creates a new event for the Access Code Authorisation Attempt
   *
   * @param targetCase Case Object for event to be created
   * @throws CTPException if IAC not found
   */
  private void createNewEventForAccessCodeAuthAttempt(Case targetCase) throws CTPException {
    Category cat =
        categoryService.findCategory(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
    if (cat == null) {
      throw new CTPException(
          CTPException.Fault.SYSTEM_ERROR, CATEGORY_ACCESS_CODE_AUTHENTICATION_ATTEMPT_NOT_FOUND);
    }

    CaseEvent caseEvent = new CaseEvent();
    caseEvent.setCaseFK(targetCase.getCasePK());
    caseEvent.setCategory(CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
    caseEvent.setCreatedBy(Constants.SYSTEM);
    caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    caseEvent.setDescription(cat.getShortDescription());
    caseService.createCaseEvent(caseEvent, null, targetCase);
  }

  /**
   * the GET endpoint to find cases by case group UUID
   *
   * @param casegroupId UUID to find by
   * @return the case events found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/casegroupid/{casegroupId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDTO>> findCasesInCaseGroup(
      @PathVariable("casegroupId") final UUID casegroupId) throws CTPException {
    log.info("Entering findCasesInCaseGroup with {}", casegroupId);

    CaseGroup caseGroup = caseGroupService.findCaseGroupById(casegroupId);
    if (caseGroup == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
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
  public ResponseEntity<List<CaseEventDTO>> findCaseEventsByCaseId(
      @PathVariable("caseId") final UUID caseId) throws CTPException {
    log.info("Entering findCaseEventsByCaseId with {}", caseId);
    Case caze = caseService.findCaseById(caseId);
    if (caze == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(CASE_ID, ERRORMSG_CASENOTFOUND, caseId.toString()));
    }

    List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseFK(caze.getCasePK());
    List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
    return CollectionUtils.isEmpty(caseEventDTOs)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(caseEventDTOs);
  }

  /**
   * To create a case event being given a parent case and json to describe the case event to be
   * created
   *
   * @param caseId the parent case
   * @param caseEventCreationRequestDTO the CaseEventDTO describing the case event to be created
   * @param bindingResult the bindingResult used to validate requests
   * @return the created CaseEventDTO
   * @throws CTPException on failure to create CaseEvent
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(value = "/{caseId}/events", method = RequestMethod.POST)
  public ResponseEntity<CreatedCaseEventDTO> createCaseEvent(
      @PathVariable("caseId") final UUID caseId,
      @RequestBody @Valid final CaseEventCreationRequestDTO caseEventCreationRequestDTO,
      BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.info(
        "Entering createCaseEvent with caseId {} and requestObject {}",
        caseId,
        caseEventCreationRequestDTO);
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for case event creation: ", bindingResult);
    }

    CaseEvent caseEvent = mapperFacade.map(caseEventCreationRequestDTO, CaseEvent.class);

    // Find target case and add to event
    Case targetCase = caseService.findCaseById(caseId);
    if (targetCase == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(CASE_ID, ERRORMSG_CASENOTFOUND, caseId));
    }
    caseEvent.setCaseFK(targetCase.getCasePK());

    Category category = categoryService.findCategory(caseEvent.getCategory());
    // If category has a new sample unit type then a party id must be provided in the case event
    if (category.getNewCaseSampleUnitType() != null
        && caseEventCreationRequestDTO.getPartyId() == null) {
      throw new CTPException(
          CTPException.Fault.VALIDATION_FAILED, String.format(EVENT_REQUIRES_NEW_CASE, caseId));
    }

    // Create new case if required
    // NOTE this isn't an ideal point to do this
    // We will not be creating new cases from case events when BI cases are removed so will leave
    // for now
    Case newCase;
    if (category.getNewCaseSampleUnitType() != null) {
      newCase = new Case();
      newCase.setPartyId(caseEventCreationRequestDTO.getPartyId());
    } else {
      newCase = null;
    }

    CaseEvent createdCaseEvent = caseService.createCaseEvent(caseEvent, newCase, targetCase);

    CreatedCaseEventDTO mappedCaseEvent =
        mapperFacade.map(createdCaseEvent, CreatedCaseEventDTO.class);
    mappedCaseEvent.setCaseId(caseId);
    mappedCaseEvent.setPartyId(caseEventCreationRequestDTO.getPartyId());

    String newResourceUrl =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .buildAndExpand(mappedCaseEvent.getCaseId())
            .toUri()
            .toString();

    return ResponseEntity.created(URI.create(newResourceUrl)).body(mappedCaseEvent);
  }

  /**
   * Creates a new event for the Access Code Authorisation Attempt
   *
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
}
