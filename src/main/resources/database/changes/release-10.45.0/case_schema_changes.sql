
-- casestate table (rename state to statePK)
ALTER TABLE casesvc.casestate  DROP CONSTRAINT state_pkey CASCADE;
ALTER TABLE casesvc.casestate  RENAME COLUMN state TO statePK;
ALTER TABLE casesvc.casestate
  ADD CONSTRAINT state_pkey PRIMARY KEY(statePK);


-- case table (rename state to stateFK)
ALTER TABLE casesvc."case"  RENAME COLUMN state TO stateFK;
ALTER TABLE casesvc."case"  ADD CONSTRAINT state_fkey FOREIGN KEY (statefk) REFERENCES casesvc.casestate(statePK);
