package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseTypeRepository;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;

/**
 * A CaseTypeService implementation which encapsulates all business logic
 * operating on the CaseType entity model.
 */
@Named
@Slf4j
public class CaseTypeServiceImpl implements CaseTypeService {

  /**
   * Spring Data Repository for CaseType entities.
   */
  @Inject
  private CaseTypeRepository caseTypeRepo;

  /**
   * Return all CaseTypes.
   *
   * @return List of CaseType entities or empty List
   */
  @Override
  public List<CaseType> findCaseTypes() {
    log.debug("Entering findCaseTypes");
    return caseTypeRepo.findAll();
  }

  /**
   * Find CaseType by unique Id.
   *
   * @param caseTypeId CaseType Id Integer
   * @return CaseType entity or null
   */
  @Override
  public CaseType findCaseTypeByCaseTypeId(final Integer caseTypeId) {
    log.debug("Entering findCaseTypeByCaseTypeId with {}", caseTypeId);
    return caseTypeRepo.findOne(caseTypeId);
  }

}
