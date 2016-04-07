set schema 'caseframe';
UPDATE category SET generatedactiontype = 'ESC_GENERAL' WHERE name = 'General Enquiry - Escalated';
UPDATE category SET generatedactiontype = 'ESC_COMPLAINT' WHERE name = 'Complaint - Escalated';
UPDATE category SET generatedactiontype = 'ESC_SURVEY' WHERE name = 'Survey Enquiry - Escalated';

ALTER TABLE category ALTER COLUMN role TYPE character varying(50);
UPDATE category SET role = 'collect-cso, collect-admin' WHERE manual = true;
INSERT INTO category VALUES ('Pending', 'Pending', NULL, true, 'collect-escalate, collect-admin', NULL);
INSERT INTO category VALUES ('Closed', 'Closed', NULL, true, 'collect-escalate, collect-admin', NULL);
