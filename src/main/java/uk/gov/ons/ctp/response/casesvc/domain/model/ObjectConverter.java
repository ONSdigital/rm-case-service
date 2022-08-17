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

  public static CaseDetailsDTO caseDetailsDTO(Case caze) {
    CaseDetailsDTO caseDetailsDTO = new CaseDetailsDTO();

    caseDetailsDTO.setActionPlanId(caze.getActionPlanId());
    caseDetailsDTO.setActiveEnrolment(caze.isActiveEnrolment());
    caseDetailsDTO.setCaseRef(caze.getCaseRef());
    caseDetailsDTO.setCollectionInstrumentId(caze.getCollectionInstrumentId());
    caseDetailsDTO.setCreatedBy(caze.getCreatedBy());
    caseDetailsDTO.setCreatedDateTime(caze.getCreatedDateTime());
    caseDetailsDTO.setIac(caze.getIac());
    caseDetailsDTO.setId(caze.getId());
    caseDetailsDTO.setPartyId(caze.getPartyId());
    caseDetailsDTO.setSampleUnitId(caze.getSampleUnitId());
    caseDetailsDTO.setSampleUnitType(caze.getSampleUnitType().name());
    caseDetailsDTO.setState(caze.getState());

    return caseDetailsDTO;
  }

  public static CaseEvent caseEvent(CaseEventCreationRequestDTO caseEventCreationRequestDTO) {
    CaseEvent caseEvent = new CaseEvent();

    caseEvent.setCategory(caseEventCreationRequestDTO.getCategory());
    caseEvent.setCreatedBy(caseEventCreationRequestDTO.getCreatedBy());
    caseEvent.setDescription(caseEventCreationRequestDTO.getDescription());
    caseEvent.setMetadata(caseEventCreationRequestDTO.getMetadata());
    caseEvent.setSubCategory(caseEventCreationRequestDTO.getSubCategory());

    return caseEvent;
  }

  public static List<CaseEventDTO> caseEventDTOList(List<CaseEvent> caseEvents) {
    List<CaseEventDTO> mappedCaseEventDTO = new ArrayList<>();
    for (int i = 1; i <= caseEvents.size(); i++) {
      CaseEvent caseEvent = caseEvents.get(i - 1);
      CaseEventDTO caseEventDTO = new CaseEventDTO();

      caseEventDTO.setCategory(caseEvent.getCategory());
      caseEventDTO.setCreatedBy(caseEvent.getCreatedBy());
      caseEventDTO.setCreatedDateTime(caseEvent.getCreatedDateTime());
      caseEventDTO.setDescription(caseEvent.getDescription());
      caseEventDTO.setMetadata(caseEvent.getMetadata());
      caseEventDTO.setSubCategory(caseEvent.getSubCategory());

      mappedCaseEventDTO.add(caseEventDTO);
    }
    return mappedCaseEventDTO;
  }

  public static CreatedCaseEventDTO createdCaseEventDTO(CaseEvent caseEvent) {
    CreatedCaseEventDTO createdCaseEventDTO = new CreatedCaseEventDTO();

    createdCaseEventDTO.setCategory(caseEvent.getCategory());
    createdCaseEventDTO.setCreatedBy(caseEvent.getCreatedBy());
    createdCaseEventDTO.setCreatedDateTime(caseEvent.getCreatedDateTime());
    createdCaseEventDTO.setDescription(caseEvent.getDescription());
    createdCaseEventDTO.setMetadata(caseEvent.getMetadata());
    createdCaseEventDTO.setSubCategory(caseEvent.getSubCategory());

    return createdCaseEventDTO;
  }

  public static CaseGroupDTO caseGroupDTO(CaseGroup caseGroup) {
    CaseGroupDTO caseGroupDTO = new CaseGroupDTO();

    if (caseGroup != null) {
      caseGroupDTO.setCaseGroupStatus(caseGroup.getStatus());
      caseGroupDTO.setCollectionExerciseId(caseGroup.getCollectionExerciseId());
      caseGroupDTO.setId(caseGroup.getId());
      caseGroupDTO.setPartyId(caseGroup.getPartyId());
      caseGroupDTO.setSampleUnitRef(caseGroup.getSampleUnitRef());
      caseGroupDTO.setSampleUnitType(caseGroup.getSampleUnitType());
      caseGroupDTO.setSurveyId(caseGroup.getSurveyId());
    }
    return caseGroupDTO;
  }

  public static List<CategoryDTO> categoryDTO(List<Category> categories) {
    List<CategoryDTO> mappedCategoryDTO = new ArrayList<>();
    for (int i = 1; i <= categories.size(); i++) {
      Category category = categories.get(i - 1);
      CategoryDTO categoryDTO = new CategoryDTO();

      categoryDTO.setGroup(category.getGroup());
      categoryDTO.setLongDescription(category.getLongDescription());
      categoryDTO.setName(category.getCategoryName());
      categoryDTO.setRole(category.getRole());
      categoryDTO.setShortDescription(category.getShortDescription());

      mappedCategoryDTO.add(categoryDTO);
    }
    return mappedCategoryDTO;
  }
}
