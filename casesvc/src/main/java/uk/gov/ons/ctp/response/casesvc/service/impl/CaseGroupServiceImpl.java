package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;

/**
 * A CaseGroupService implementation which encapsulates all business logic
 * operating on the CaseGroup entity model.
 */
@Service
@Slf4j
public class CaseGroupServiceImpl implements CaseGroupService {

  /**
   * Spring Data Repository for CaseGroup entities.
   */
  @Autowired
  private CaseGroupRepository caseGroupRepo;

  @Override
  public CaseGroup findCaseGroupByCaseGroupId(final Integer caseGroupId) {
    log.debug("Entering findCaseGroupByCaseGroupId with {}", caseGroupId);
    return caseGroupRepo.findOne(caseGroupId);
  }

}
