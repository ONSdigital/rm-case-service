-- Adding required indexes for case_action view.
Create index idx_casegroup_collection_exercise_id
ON casesvc.casegroup USING btree (collection_exercise_id);
Create index idx_case_active_enrolment
ON casesvc.case USING btree (active_enrolment);

-- Adding case_action_template table

CREATE TABLE casesvc.case_action_template (
	event_tag varchar(100) NOT NULL,
    type varchar (40) NOT NULL,
    description varchar(350) NOT NULL,
    handler varchar CHECK (handler = 'email' OR handler = 'letter'),
    prefix varchar (100));

-- Initial entry in case_action_template table

INSERT INTO casesvc.case_action_template
    (event_tag, type, description, handler, prefix)
    VALUES
    ('mps', 'BSNL', 'Business Survey Notification Letter', 'letter', 'BSNOT'),
    ('go_live', 'BSNE', 'Business Survey Notification Email', 'email', NULL),
    ('reminder', 'BSRL', 'Business Survey Reminder Letter', 'letter', 'BSREM'),
    ('reminder', 'BSRE', 'Business Survey Reminder Email', 'email', NULL),
	('reminder2', 'BSRL', 'Business Survey Reminder Letter', 'letter', 'BSREM'),
    ('reminder2', 'BSRE', 'Business Survey Reminder Email', 'email', NULL),
	('reminder3', 'BSRL', 'Business Survey Reminder Letter', 'letter', 'BSREM'),
    ('reminder3', 'BSRE', 'Business Survey Reminder Email', 'email', NULL),
    ('nudge_email_0', 'BSNUE', 'Business Survey Nudge Email', 'email', NULL),
	('nudge_email_1', 'BSNUE', 'Business Survey Nudge Email', 'email', NULL),
	('nudge_email_2', 'BSNUE', 'Business Survey Nudge Email', 'email', NULL),
	('nudge_email_3', 'BSNUE', 'Business Survey Nudge Email', 'email', NULL),
	('nudge_email_4', 'BSNUE', 'Business Survey Nudge Email', 'email', NULL);

-- Adding case_action_event table
CREATE TABLE casesvc.case_action_event (
    id SERIAL PRIMARY KEY,
    case_id uuid NOT NULL,
    type varchar (40) NOT NULL,
    event_tag varchar(100) NOT NULL,
    handler varchar CHECK (handler = 'email' OR handler = 'letter'),
    status varchar CHECK (status = 'PROCESSED' OR status = 'FAILED'),
    processed_timestamp TIMESTAMP);

-- Adding required indexes to case_action_event
Create index idx_case_action_event_case_id
ON casesvc.case_action_event USING btree (case_id);
Create index idx_case_action_event_event_tag
ON casesvc.case_action_event USING btree (event_tag);
Create index idx_case_action_event_handler
ON casesvc.case_action_event USING btree (handler);
Create index idx_case_action_event_status
ON casesvc.case_action_event USING btree (status);

-- Adding View case_action
CREATE VIEW casesvc.case_action AS
select cg.collection_exercise_id, c.id as case_id,cg.party_id, cg.sample_unit_ref, cg.sample_unit_type,
cg.status, cg.survey_id, c.sampleunit_id as sample_unit_id, c.collection_instrument_id,
iac.iac,
c.active_enrolment
from casesvc.casegroup cg, casesvc.case c
LEFT JOIN casesvc.caseiacaudit iac ON iac.case_fk=c.case_pk
where cg.case_group_pk=c.case_group_fk and c.state_fk='ACTIONABLE'