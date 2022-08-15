package uk.gov.ons.ctp.response.casesvc.domain;

import uk.gov.ons.ctp.response.casesvc.domain.model.Case;

public class ObjectTestConverter {

  private ObjectTestConverter() {};

  public static Case caze(Case caze) {
    Case testCaze = new Case();

    testCaze.setIac(caze.getIac());
    testCaze.setState(caze.getState());
    testCaze.setPartyId(caze.getPartyId());
    testCaze.setId(caze.getId());
    testCaze.setCreatedBy(caze.getCreatedBy());
    testCaze.setCasePK(caze.getCasePK());
    testCaze.setActiveEnrolment(caze.isActiveEnrolment());
    testCaze.setCaseGroupFK(caze.getCaseGroupFK());
    testCaze.setCaseGroupId(caze.getCaseGroupId());
    testCaze.setSampleUnitType(caze.getSampleUnitType());
    testCaze.setSampleUnitId(caze.getSampleUnitId());
    testCaze.setOptLockVersion(caze.getOptLockVersion());
    testCaze.setCaseGroupFK(caze.getCaseGroupFK());
    testCaze.setSourceCaseId(caze.getSourceCaseId());
    testCaze.setCaseRef(caze.getCaseRef());
    testCaze.setCollectionInstrumentId(caze.getCollectionInstrumentId());
    testCaze.setActionPlanId(caze.getActionPlanId());
    testCaze.setCreatedDateTime(caze.getCreatedDateTime());
    testCaze.setIacAudits(caze.getIacAudits());

    return testCaze;
  }
}
