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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.ObjectConverter;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CreatedCaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;
import uk.gov.ons.ctp.response.lib.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.error.InvalidRequestException;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;

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
  private CaseEventRepository caseEventRepository;

  /** Contructor for CaseEndpoint */
  @Autowired
  public CaseEndpoint(
      final CaseService caseService,
      final CaseGroupService caseGroupService,
      final CategoryService categoryService,
      final CaseEventRepository caseEventRepository) {
    this.caseService = caseService;
    this.caseGroupService = caseGroupService;
    this.categoryService = categoryService;
    this.caseEventRepository = caseEventRepository;
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
   * @param maxCasesPerSurvey the maximum number of cases to return per survey
   * @return the cases found
   */
  @RequestMapping(value = "/partyid/{partyId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseDetailsDTO>> findCasesByPartyId(
      @PathVariable("partyId") final UUID partyId,
      @RequestParam(value = "caseevents", required = false) final boolean caseevents,
      @RequestParam(value = "iac", required = false) final boolean iac,
      @RequestParam(value = "max_cases_per_survey", required = false)
          final Integer maxCasesPerSurvey) {

    List<Case> casesList;
    if (maxCasesPerSurvey != null) {
      log.with("party_id", partyId)
          .with("max_cases_per_survey", maxCasesPerSurvey)
          .info("Retrieving cases by party");
      casesList = caseService.findCasesByPartyIdLimitedPerSurvey(partyId, iac, maxCasesPerSurvey);

    } else {
      casesList = caseService.findCasesByPartyId(partyId, iac);
    }

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
      caseEventDTOs = ObjectConverter.caseEventDTOList(caseEvents);
    } else {
      List<CaseEvent> caseEvents =
          caseService.findCaseEventsByCaseFKAndCategory(caze.getCasePK(), categories);
      caseEventDTOs = ObjectConverter.caseEventDTOList(caseEvents);
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

    CaseEvent caseEvent = ObjectConverter.caseEvent(caseEventCreationRequestDTO);

    // Find target case and add to event
    Case targetCase = caseService.findCaseById(caseId);
    if (targetCase == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(CASE_ID, ERRORMSG_CASENOTFOUND, caseId));
    }
    caseEvent.setCaseFK(targetCase.getCasePK());

    CaseEvent createdCaseEvent = caseService.createCaseEvent(caseEvent, targetCase);

    CreatedCaseEventDTO mappedCaseEvent = ObjectConverter.createdCaseEventDTO(createdCaseEvent);
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
    CaseDetailsDTO caseDetailsDTO = ObjectConverter.caseDetailsDTO(caze);

    CaseGroup parentCaseGroup = caseGroupService.findCaseGroupByCaseGroupPK(caze.getCaseGroupFK());
    caseDetailsDTO.setCaseGroup(ObjectConverter.caseGroupDTO(parentCaseGroup));

    if (caseevents) {
      List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseFK(caze.getCasePK());
      List<CaseEventDTO> caseEventDTOs = ObjectConverter.caseEventDTOList(caseEvents);
      caseDetailsDTO.setCaseEvents(caseEventDTOs);
    }

    if (!iac) {
      caseDetailsDTO.setIac(null);
    }

    return caseDetailsDTO;
  }

  /**
   * Deletes all case, caseevent and casegroup data for a particular collection exercise
   *
   * @param collectionExerciseId The Collection Exercise UUID to delete for
   * @return An appropriate HTTP repsonse code
   */
  @DeleteMapping("{collectionExerciseId}")
  public ResponseEntity<String> deleteCaseDataByCollectionExercise(
      @PathVariable UUID collectionExerciseId) throws CTPException {

    log.with("collection_exercise_id", collectionExerciseId).info("Deleting cases");

    List<CaseGroup> caseGroupList =
        caseGroupService.findCaseGroupsForCollectionExercise(collectionExerciseId);
    List<Case> caseList = caseService.findCasesByGroupFK(caseGroupList);
    List<Integer> listOfCasePKs = caseList.stream().map(Case::getCasePK).toList();
    List<CaseEvent> caseEventsList = caseEventRepository.findByCaseFKIn(listOfCasePKs);

    caseEventRepository.deleteAll(caseEventsList);
    caseService.deleteCasesInList(caseList);
    caseGroupService.deleteCaseGroups(caseGroupList);

    log.with("collection_exercise_id", collectionExerciseId).info("Delete successful");

    return ResponseEntity.ok("Deleted Successfully");
  }
}
