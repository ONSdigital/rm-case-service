package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.Contact;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;

/**
 * The REST endpoint controller for CaseSvc Cases
 */
@Path("/cases")
@Produces({ "application/json" })
@Slf4j
public final class CaseEndpoint implements CTPEndpoint {

	public static final String ERRORMSG_CASENOTFOUND = "Case not found for";
	public static final String ERRORMSG_CASEGROUPNOTFOUND = "CaseGroup not found for";
	public static final String EVENT_REQUIRES_NEW_CASE = "Event requested for case %s requires additional data - new Case details";

	@Inject
	private CaseGroupService caseGroupService;

	@Inject
	private CategoryService categoryService;

	@Inject
	private CaseService caseService;

	@Inject
	private MapperFacade mapperFacade;

	/**
	 * the GET endpoint to find a Case by id
	 *
	 * @param caseId
	 *            to find by
	 * @return the case found
	 * @throws CTPException
	 *             something went wrong
	 */
	@GET
	@Path("/{caseId}")
	public CaseDTO findCaseByCaseId(@PathParam("caseId") final Integer caseId) throws CTPException {
		log.info("Entering findCaseByCaseId with {}", caseId);
		Case caseObj = caseService.findCaseByCaseId(caseId);
		if (caseObj == null) {
			throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
					String.format("%s case id %s", ERRORMSG_CASENOTFOUND, caseId));
		}

		return mapperFacade.map(caseObj, CaseDTO.class);
	}

	/**
	 * the GET endpoint to find a Case by IAC
	 *
	 * @param IAC
	 *            to find by
	 * @return the case found
	 * @throws CTPException
	 *             something went wrong
	 */
	@GET
	@Path("/iac/{iac}")
	public CaseDTO findCaseByIac(@PathParam("iac") final String iac) throws CTPException {
		log.info("Entering findCaseByIac with {}", iac);
		Case caseObj = caseService.findCaseByIac(iac);

		if (caseObj == null) {
			throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
					String.format("%s iac id %s", ERRORMSG_CASENOTFOUND, iac));
		}

		createNewEventForIACAuthenticated(caseObj);

		return mapperFacade.map(caseObj, CaseDTO.class);
	}

	private void createNewEventForIACAuthenticated(Case caseObj) {

		Category cat = categoryService.findCategory(CategoryDTO.CategoryType.IAC_AUTHENTICATED);
		CaseEvent caseEvent = new CaseEvent();
		caseEvent.setCaseId(caseObj.getCaseId());
		caseEvent.setCategory(CategoryDTO.CategoryType.IAC_AUTHENTICATED);
		caseEvent.setCreatedBy(Constants.SYSTEM);
		caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
		caseEvent.setDescription(cat.getShortDescription());

		caseService.createCaseEvent(caseEvent, caseObj);
	}

	/**
	 * the GET endpoint to find case events by case id
	 *
	 * @param caseId
	 *            to find by
	 * @return the case events found
	 * @throws CTPException
	 *             something went wrong
	 */
	@GET
	@Path("/casegroup/{caseGroupId}")
	public List<CaseDTO> findCasesInCaseGroup(@PathParam("caseGroupId") final Integer caseGroupId) throws CTPException {
		log.info("Entering findCasesInCaseGroup with {}", caseGroupId);
		CaseGroup caseGroup = caseGroupService.findCaseGroupByCaseGroupId(caseGroupId);
		if (caseGroup == null) {
			throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
					String.format("%s casegroup id %s", ERRORMSG_CASEGROUPNOTFOUND, caseGroupId));
		}
		List<Case> cases = caseService.findCasesByCaseGroupId(caseGroupId);
		List<CaseDTO> caseDTOs = mapperFacade.mapAsList(cases, CaseDTO.class);
		return CollectionUtils.isEmpty(caseDTOs) ? null : caseDTOs;
	}

	/**
	 * the GET endpoint to find case events by case id
	 *
	 * @param caseId
	 *            to find by
	 * @return the case events found
	 * @throws CTPException
	 *             something went wrong
	 */
	@GET
	@Path("/{caseId}/events")
	public List<CaseEventDTO> findCaseEventsByCaseId(@PathParam("caseId") final Integer caseId) throws CTPException {
		log.info("Entering findCaseEventsByCaseId with {}", caseId);
		Case caseObj = caseService.findCaseByCaseId(caseId);
		if (caseObj == null) {
			throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
					String.format("%s case id %s", ERRORMSG_CASENOTFOUND, caseId));
		}
		List<CaseEvent> caseEvents = caseService.findCaseEventsByCaseId(caseId);
		List<CaseEventDTO> caseEventDTOs = mapperFacade.mapAsList(caseEvents, CaseEventDTO.class);
		return CollectionUtils.isEmpty(caseEventDTOs) ? null : caseEventDTOs;
	}

	/**
	 * To create a case event being given a parent case and json to describe the
	 * case event to be created
	 *
	 * @param caseId
	 *            the parent case
	 * @param caseEventCreationRequestDTO
	 *            the CaseEventDTO describing the case event to be created
	 * @return the created CaseEventDTO
	 * @throws CTPException
	 *             on failure to create CaseEvent
	 */
	@POST
	@Path("/{caseId}/events")
	public CaseEventDTO createCaseEvent(@PathParam("caseId") final Integer caseId,
			@Valid final CaseEventCreationRequestDTO caseEventCreationRequestDTO) throws CTPException {
		log.info("Entering createCaseEvent with caseId {} and requestObject {}", caseId, caseEventCreationRequestDTO);
		caseEventCreationRequestDTO.setCaseId(caseId);

		CaseEvent caseEvent = mapperFacade.map(caseEventCreationRequestDTO, CaseEvent.class);
		Case caze = mapperFacade.map(caseEventCreationRequestDTO.getCaseCreationRequest(), Case.class);
		if (caze != null) {
			Contact contact = mapperFacade.map(caseEventCreationRequestDTO.getCaseCreationRequest(), Contact.class);
			caze.setContact(contact);
		}

		Category category = categoryService.findCategory(caseEvent.getCategory());
		if (category.getNewCaseRespondentType() != null && caze == null) {
			throw new CTPException(CTPException.Fault.VALIDATION_FAILED,
					String.format(EVENT_REQUIRES_NEW_CASE, caseId));
		}

		CaseEvent createdCaseEvent = caseService.createCaseEvent(caseEvent, caze);
		if (createdCaseEvent == null) {
			throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
					String.format("%s case id %s", ERRORMSG_CASENOTFOUND, caseId));
		}
		return mapperFacade.map(createdCaseEvent, CaseEventDTO.class);
	}

}
