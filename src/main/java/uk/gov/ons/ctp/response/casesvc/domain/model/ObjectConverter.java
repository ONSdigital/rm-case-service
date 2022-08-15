package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.util.ArrayList;
import java.util.List;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CreatedCaseEventDTO;

public class ObjectConverter {

  private ObjectConverter() {};

  public static List<CaseEventDTO> caseEventDTOList(List<CaseEvent> caseEvents) {
    List<CaseEventDTO> mappedCaseEventDTO = new ArrayList<>();
    for (int i = 1; i <= caseEvents.size(); i++) {
      CaseEvent caseEvent = caseEvents.get(i);
      mappedCaseEventDTO.add(caseEventDTO(caseEvent));
    }
    return mappedCaseEventDTO;
  }

  public static CaseEventDTO caseEventDTO(CaseEvent caseEvent) {
    CaseEventDTO caseEventDTO = new CaseEventDTO();
    caseEventDTO.setCategory(caseEvent.getCategory());
    caseEventDTO.setCreatedBy(caseEvent.getCreatedBy());
    caseEventDTO.setDescription(caseEventDTO.getDescription());
    caseEventDTO.setMetadata(caseEvent.getMetadata());
    caseEventDTO.setSubCategory(caseEvent.getSubCategory());
    caseEventDTO.setCreatedDateTime(caseEvent.getCreatedDateTime());

    return caseEventDTO;
  }

  public static CreatedCaseEventDTO createdCaseEventDTO(CaseEvent caseEvent) {
    CreatedCaseEventDTO createdCaseEventDTO = new CreatedCaseEventDTO();

    createdCaseEventDTO.setCreatedBy(caseEvent.getCreatedBy());
    createdCaseEventDTO.setCreatedDateTime(createdCaseEventDTO.getCreatedDateTime());
    createdCaseEventDTO.setDescription(caseEvent.getDescription());
    createdCaseEventDTO.setCategory(caseEvent.getCategory());
    createdCaseEventDTO.setMetadata(caseEvent.getMetadata());
    createdCaseEventDTO.setSubCategory(caseEvent.getSubCategory());

    return createdCaseEventDTO;
  }

  public static CaseDetailsDTO caseDetailsDTO(Case caze) {
    CaseDetailsDTO caseDetailsDTO = new CaseDetailsDTO();

    caseDetailsDTO.setId(caze.getId());
    caseDetailsDTO.setState(caze.getState());
    caseDetailsDTO.setCaseRef(caze.getCaseRef());
    caseDetailsDTO.setIac(caze.getIac());
    caseDetailsDTO.setCreatedBy(caze.getCreatedBy());
    caseDetailsDTO.setActionPlanId(caze.getActionPlanId());
    caseDetailsDTO.setActiveEnrolment(caze.isActiveEnrolment());
    caseDetailsDTO.setCreatedDateTime(caze.getCreatedDateTime());
    caseDetailsDTO.setCollectionInstrumentId(caze.getCollectionInstrumentId());
    caseDetailsDTO.setPartyId(caze.getPartyId());
    caseDetailsDTO.setSampleUnitId(caze.getSampleUnitId());
    caseDetailsDTO.setSampleUnitType(caze.getSampleUnitType().name());

    return caseDetailsDTO;
  }

  public static CaseGroupDTO caseGroupDTO(CaseGroup caseGroup) {
    CaseGroupDTO caseGroupDTO = new CaseGroupDTO();

    caseGroupDTO.setCaseGroupStatus(caseGroup.getStatus());
    caseGroupDTO.setCollectionExerciseId(caseGroup.getCollectionExerciseId());
    caseGroupDTO.setId(caseGroup.getId());
    caseGroupDTO.setPartyId(caseGroup.getPartyId());
    caseGroupDTO.setSampleUnitRef(caseGroup.getSampleUnitRef());
    caseGroupDTO.setSampleUnitType(caseGroup.getSampleUnitType());
    caseGroupDTO.setSurveyId(caseGroup.getSurveyId());

    return caseGroupDTO;
  }

  public static CaseEvent caseEvent(CaseEventCreationRequestDTO caseEventCreationRequestDTO) {
    CaseEvent caseEvent = new CaseEvent();

    caseEvent.setDescription(caseEventCreationRequestDTO.getDescription());
    caseEvent.setCategory(caseEventCreationRequestDTO.getCategory());
    caseEvent.setCreatedBy(caseEventCreationRequestDTO.getCreatedBy());
    caseEvent.setSubCategory(caseEventCreationRequestDTO.getSubCategory());
    caseEvent.setMetadata(caseEventCreationRequestDTO.getMetadata());

    return caseEvent;
  }

  public static List<CategoryDTO> categoryDTO(List<Category> categories) {
    List<CategoryDTO> mappedCategoryDTO = new ArrayList<>();
    for (int i = 1; i <= categories.size(); i++) {
      Category category = categories.get(i);
      CategoryDTO categoryDTO = new CategoryDTO();

      categoryDTO.setShortDescription(category.getShortDescription());
      categoryDTO.setGroup(category.getGroup());
      categoryDTO.setName(category.getCategoryName());
      categoryDTO.setLongDescription(category.getLongDescription());
      categoryDTO.setRole(category.getRole());

      mappedCategoryDTO.add(categoryDTO);
    }
    return mappedCategoryDTO;
  }
}
