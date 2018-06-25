package uk.gov.ons.ctp.response.casesvc.domain.model;

import net.sourceforge.cobertura.CoverageIgnore;

/** Domain model object */
@CoverageIgnore
public interface CaseProjection {

  /**
   * Returns case Id
   *
   * @return caseid
   */
  Integer getCaseId();
}
