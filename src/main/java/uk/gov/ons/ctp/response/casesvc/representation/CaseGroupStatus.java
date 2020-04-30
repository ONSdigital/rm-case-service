package uk.gov.ons.ctp.response.casesvc.representation;

/** Status of the overriding business case, Updated depending on enrolled respondent actions */
public enum CaseGroupStatus {
  NOTSTARTED,
  INPROGRESS,
  COMPLETE,
  COMPLETEDBYPHONE,
  REOPENED,
  NOLONGERREQUIRED,
  REFUSAL,
  OTHERNONRESPONSE,
  UNKNOWNELIGIBILITY,
  NOTELIGIBLE
}
