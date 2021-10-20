-- DROP case_action_template table and case_action_event
DROP TABLE casesvc.case_action_template CASCADE;
DROP TABLE casesvc.case_action_event CASCADE;


-- Create case_action_audit_event table
CREATE TABLE casesvc.case_action_audit_event (
    id SERIAL PRIMARY KEY,
    case_id uuid NOT NULL,
    collection_exercise_id uuid NOT NULL,
    type varchar (40) NOT NULL,
    event_tag varchar(100) NOT NULL,
    handler varchar CHECK (handler = 'EMAIL' OR handler = 'LETTER'),
    status varchar CHECK (status = 'PROCESSED' OR status = 'FAILED'),
    processed_timestamp TIMESTAMP);

-- Adding required indexes to case_action_event
CREATE INDEX idx_case_action_audit_event_case_id
ON casesvc.case_action_audit_event USING btree (case_id);
CREATE INDEX idx_case_action_audit_event_collection_exercise_id
ON casesvc.case_action_audit_event USING btree (collection_exercise_id);
CREATE INDEX idx_case_action_audit_event_tag
ON casesvc.case_action_audit_event USING btree (event_tag);
CREATE INDEX idx_case_action_audit_event_handler
ON casesvc.case_action_audit_event USING btree (handler);
CREATE INDEX idx_case_action_audit_event_status
ON casesvc.case_action_audit_event USING btree (status);


-- Recreate case_action_template table
CREATE TABLE casesvc.case_action_template (
    type varchar (40) UNIQUE NOT NULL,
    description varchar(350) NOT NULL,
    event_tag_mapping varchar(100) NOT NULL,
    handler varchar CHECK (handler = 'EMAIL' OR handler = 'LETTER'),
    prefix varchar (100));

-- Adding required indexes to case_action_template
CREATE INDEX idx_case_action_template_tag
ON casesvc.case_action_template USING btree (event_tag_mapping);

INSERT INTO casesvc.case_action_template
    (type, description, event_tag_mapping, handler, prefix)
    VALUES
    ('BSNL', 'Business Survey Notification Letter', 'mps', 'LETTER', 'BSNOT'),
    ('BSNE', 'Business Survey Notification Email', 'go_live', 'EMAIL', NULL),
    ('BSRL', 'Business Survey Reminder Letter', 'reminder', 'LETTER', 'BSREM'),
    ('BSRE', 'Business Survey Reminder Email', 'reminder', 'EMAIL', NULL),
    ('BSNUE', 'Business Survey Nudge Email', 'nudge', 'EMAIL', NULL);



CREATE TABLE casesvc.case_action_event_request (
    id SERIAL PRIMARY KEY,
    collection_exercise_id uuid NOT NULL,
	event_tag varchar(100) NOT NULL,
	process_event_requested_time TIMESTAMP,
	status varchar CHECK (status = 'INPROGRESS'  OR status = 'COMPLETED' OR status = 'FAILED' OR status = 'RETRY'),
	CONSTRAINT u_constraint UNIQUE (collection_exercise_id, event_tag)
    );

CREATE INDEX idx_case_action_event_request_collex
ON casesvc.case_action_event_request USING btree (collection_exercise_id);
CREATE INDEX idx_case_action_event_request_tag
ON casesvc.case_action_event_request USING btree (event_tag);