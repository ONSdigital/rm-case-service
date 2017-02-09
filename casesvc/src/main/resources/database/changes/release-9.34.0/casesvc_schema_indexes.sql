CREATE INDEX case_actionplanmappingid_index ON casesvc.case (actionplanmappingid);
CREATE INDEX case_casegroupid_index ON casesvc.case (casegroupid);
CREATE INDEX case_casetypeid_index ON casesvc.case (casetypeid);
CREATE INDEX case_state_index ON casesvc.case (state);
CREATE INDEX caseevent_caseid_index on casesvc.caseevent(caseid);

