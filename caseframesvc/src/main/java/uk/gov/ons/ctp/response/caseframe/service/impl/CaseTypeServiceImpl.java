package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseType;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseTypeRepository;
import uk.gov.ons.ctp.response.caseframe.service.CaseTypeService;

/**
 * An implementation of the CaseTypeService using JPA Repository class(es) The
 * business logic for the application should reside here.
 */
@Named
@Slf4j
public final class CaseTypeServiceImpl implements CaseTypeService {

  @Inject
  private CaseTypeRepository caseTypeRepo;

  @Override
  public List<CaseType> findCaseTypes() {
    log.debug("Entering findCaseTypes");
    return caseTypeRepo.findAll();
  }

  @Override
  public CaseType findCaseTypeByCaseTypeId(final Integer caseTypeId) {
    log.debug("Entering findCaseTypeByCaseTypeId with {}", caseTypeId);
    return caseTypeRepo.findOne(caseTypeId);
  }

}
