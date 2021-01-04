ALTER TABLE casesvc.case
DROP COLUMN active_enrolment;

ALTER TABLE casesvc.case
add column active_enrolment boolean DEFAULT FALSE;
