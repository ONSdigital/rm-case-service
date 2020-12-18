ALTER TABLE casesvc."case"
RENAME activeenrolment TO active_enrolment;

DROP TABLE casesvc.action_template CASCADE;