package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
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
public final class CaseEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaseEndpoint.class);

  public static final String CATEGORY_ACCESS_CODE_AUTHENTICATION_ATTEMPT_NOT_FOUND =
      "Category ACCESS_CODE_AUTHENTICATION_ATTEMPT does not exist";
  public static final String ERRORMSG_CASENOTFOUND = "Case not found for";
  public static final String EVENT_REQUIRES_NEW_CASE =
      "Event requested for " + "case %s requires additional data - new Case details";

  private static final String CASE_ID = "%s case id %s";

  private CaseService caseService;
  private CaseGroupService caseGroupService;
  private CategoryService categoryService;
  private MapperFacade mapperFacade;
  private CaseRepository caseRepository;

  /** Contructor for CaseEndpoint */
  @Autowired
  public CaseEndpoint(
      final CaseService caseService,
      final CaseGroupService caseGroupService,
      final CategoryService categoryService,
      final CaseRepository caseRepository,
      final @Qualifier("caseSvcBeanMapper") MapperFacade mapperFacade) {
    this.caseService = caseService;
    this.caseGroupService = caseGroupService;
    this.categoryService = categoryService;
    this.caseRepository = caseRepository;
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
    log.with("case_id", caseId).debug("Entering findCaseById");
    Case caseObj = caseService.findCaseById(caseId);
    if (caseObj == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(CASE_ID, ERRORMSG_CASENOTFOUND, caseId));
    }

    return ResponseEntity.ok(buildDetailedCaseDTO(caseObj, caseevents, iac));
  }

  @RequestMapping(value = "/sampleunitids", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDetailsDTO>> findCasesBySampleUnitIds(
      @RequestParam(value = "sampleUnitId") final List<UUID> sampleUnitIds,
      @RequestParam(value = "caseevents", required = false) final boolean caseevents,
      @RequestParam(value = "iac", required = false) final boolean iac) {

    List<CaseDetailsDTO> cases =
        sampleUnitIds
            .stream()
            .map(caze -> caseService.findCaseBySampleUnitId(caze))
            .filter(Objects::nonNull)
            .map(
                caze -> {
                  CaseDetailsDTO cdd = buildDetailedCaseDTO(caze, caseevents, iac);
                  return cdd;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(cases);
  }

  /**
   * the GET endpoint to find Cases by partyid UUID
   *
   * @param partyId to find by
   * @param caseevents flag used to return or not CaseEvents
   * @param iac flag used to return or not the iac
   * @return the cases found
   */
  @Deprecated // See findCases(sampleUnitId, partyId)
  @RequestMapping(value = "/partyid/{partyId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDetailsDTO>> findCasesByPartyId(
      @PathVariable("partyId") final UUID partyId,
      @RequestParam(value = "caseevents", required = false) final boolean caseevents,
      @RequestParam(value = "iac", required = false) final boolean iac) {
    log.with("party_id", partyId).debug("Retrieving cases by party");
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
   * The GET endpoint to find cases by the surveyid UUID.
   *
   * @param surveyId to find by
   * @return the cases found
   */
  @RequestMapping(value = "/surveyid/{surveyId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDetailsDTO>> findCasesBySurveyId(
      @PathVariable("surveyId") final UUID surveyId) {
    log.with("survey_id", surveyId).debug("Retrieving cases by survey");
    List<CaseGroup> caseGroupsList = caseGroupService.findCaseGroupBySurveyId(surveyId);
    List<Case> cases = new ArrayList<>();

    for (CaseGroup caseGroup : caseGroupsList) {
      cases.addAll(caseService.findCasesByCaseGroupFK(caseGroup.getCaseGroupPK()));
    }
    List<CaseDetailsDTO> caseDetailsDTOList =
        cases
            .stream()
            .map(caseDetail -> buildDetailedCaseDTO(caseDetail, false, false))
            .collect(Collectors.toList());

    return ResponseEntity.ok(caseDetailsDTOList);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<CaseDetailsDTO>> findCases(
      @RequestParam(required = false) String sampleUnitId,
      @RequestParam(required = false) String partyId) {
    log.with("sample_unit_id", sampleUnitId).with("party_id", partyId).debug("Finding cases");
    Example<Case> exampleCase = buildExampleCase(sampleUnitId, partyId);
    List<Case> cases = getCases(exampleCase);
    List<CaseDetailsDTO> caseResponses =
        cases
            .stream()
            .map(
                c -> {
                  CaseDetailsDTO caseDetails = mapperFacade.map(c, CaseDetailsDTO.class);
                  CaseGroup parentCaseGroup =
                      caseGroupService.findCaseGroupByCaseGroupPK(c.getCaseGroupFK());
                  caseDetails.setCaseGroup(mapperFacade.map(parentCaseGroup, CaseGroupDTO.class));
                  return caseDetails;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(caseResponses);
  }

  private List<Case> getCases(Example<Case> exampleCase) {
    boolean emptyRequest = exampleCase.getProbe().equals(new Case());
    if (emptyRequest) {
      return caseRepository.findAll();
    } else {
      return caseRepository.findAll(exampleCase);
    }
  }

  private Example<Case> buildExampleCase(String sampleUnitId, String partyId) {
    Case.CaseBuilder caseBuilder = Case.builder();
    if (sampleUnitId != null) {
      caseBuilder.sampleUnitId(UUID.fromString(sampleUnitId));
    }
    if (partyId != null) {
      caseBuilder.partyId(UUID.fromString(partyId));
    }
    return Example.of(caseBuilder.build(), ExampleMatcher.matchingAny());
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
    log.debug("Retrieving case by iac");
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
  private void createNewEventForAccessCodeAuthAttempt(final Case targetCase) throws CTPException {
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
   * @param iacFlag Boolean flag for returning iac's with cases
   * @return the case events found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/casegroupid/{casegroupId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDetailsDTO>> findCasesInCaseGroup(
      @PathVariable("casegroupId") final UUID casegroupId,
      @RequestParam(value = "iac", required = false) final boolean iacFlag)
      throws CTPException {
    log.with("case_group_id", casegroupId).debug("Entering findCasesInCaseGroup");

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
      List<CaseDetailsDTO> caseDetailsDTOList =
          casesList
              .stream()
              .map(c -> buildDetailedCaseDTO(c, false, iacFlag))
              .collect(Collectors.toList());
      return ResponseEntity.ok(caseDetailsDTOList);
    }
  }

  /**
   * the GET endpoint to find case events, with optional timestamps by case id
   *
   * @param caseId to find by
   * @param category type to collect case event timestamp
   * @return the case events found, with optional timestamps (category required for this)
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{caseId}/events", method = RequestMethod.GET)
  public ResponseEntity<List<CaseEventDTO>> findCaseEventsByCaseId(
      @PathVariable("caseId") final UUID caseId,
      @RequestParam(value = "category", required = false) final List<String> categories)
      throws CTPException {
    log.with("case_id", caseId).debug("Entering findCaseEventsByCaseId");
    Case caze = caseService.findCaseById(caseId);
    if (caze == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(CASE_ID, ERRORMSG_CASENOTFOUND, caseId.toString()));
    }

    List<CaseEventDTO> caseEventDTOs;

    if (categories == null || categories.isEmpty()) {
      List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseFK(caze.getCasePK());
      caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
    } else {
      List<CaseEvent> caseEvents =
          caseService.findCaseEventsByCaseFKAndCategory(caze.getCasePK(), categories);
      caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
    }

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
    log.with("case_id", caseId)
        .with("category", caseEventCreationRequestDTO.getCategory())
        .debug("Creating case event");
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

    CaseEvent createdCaseEvent = caseService.createCaseEvent(caseEvent, targetCase);

    CreatedCaseEventDTO mappedCaseEvent =
        mapperFacade.map(createdCaseEvent, CreatedCaseEventDTO.class);
    mappedCaseEvent.setCaseId(caseId);

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
