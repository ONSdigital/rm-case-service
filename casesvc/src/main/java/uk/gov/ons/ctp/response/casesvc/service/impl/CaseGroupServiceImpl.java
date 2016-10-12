package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;

/**
 * A CaseGroupService implementation which encapsulates all business logic
 * operating on the CaseGroup entity model.
 */
@Named
@Slf4j
public class CaseGroupServiceImpl implements CaseGroupService {

  /**
   * Spring Data Repository for CaseGroup entities.
   */
  @Inject
  private CaseGroupRepository caseGroupRepo;

  @Override
  public CaseGroup findCaseGroupByCaseGroupId(final Integer caseGroupId) {
    log.debug("Entering findCaseGroupByCaseGroupId with {}", caseGroupId);
    return caseGroupRepo.findOne(caseGroupId);
  }
  
  @Override
  public List<CaseGroup> findCaseGroupsByUprn(final Long uprn) {
    log.debug("Entering findCaseGroupsByUprn with {}", uprn);
    return caseGroupRepo.findByUprn(uprn);
  }

}
