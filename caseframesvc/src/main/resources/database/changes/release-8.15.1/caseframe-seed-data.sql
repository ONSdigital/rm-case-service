--changes to caseframe.category table for new actiontype names
--changes so that column name is part of insert

set schema 'caseframe';

TRUNCATE category CASCADE;
TRUNCATE sample CASCADE;
TRUNCATE casetype CASCADE;
TRUNCATE questionset CASCADE;
TRUNCATE survey CASCADE;

--survey

INSERT INTO survey (surveyid, name, description) VALUES (1, '2016 Test', '2016 Census Test');

--questionset

INSERT INTO questionset (questionset, description) VALUES ('HH', 'Households');
INSERT INTO questionset (questionset, description) VALUES ('CE', 'Communal Establishments');

--casetype

INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (1, 'HH', 'Household', 1, 'HH');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (2, 'HGH', 'Hotel Guest House Bed and Breakfast', 2, 'CE');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (3, 'CH', 'Care Home', 3, 'CE');

--sample

INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (1, 'Residential', 'Households', 'addresstype = ''HH''', 1, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (2, 'Hotels Guest Houses', 'Hotels Guest Houses Bed and Breakfasts', 'addresstype = ''CE'' and estabtype in (''CH'')', 2, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (3, 'Care Homes', 'Care Homes', 'addresstype = ''CE'' and estabtype in (''RI01'')', 3, 1);

--category

INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('CaseCreated', 'Initial Creation Of Case', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('CaseClosed', 'Closure of Case', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('ActionUpdated', 'Action updated', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('ActionCompleted', 'Action completed', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('ActionCreated', 'Action created', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('QuestionnareResponse', 'Questionnaire Response logged', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('General Enquiry', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Complaint', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Survey Enquiry', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Address Details Incorrect', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Classification Incorrect', NULL, true, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Refusal', NULL, true, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Request for Fulfilment', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Technical Query', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Miscellaneous', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Pending', 'Pending', NULL, true, 'collect-escalate, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Closed', 'Closed', NULL, true, 'collect-escalate, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Complaint - Escalated', NULL, NULL, true, 'collect-csos, collect-admins', 'ComplaintEscalation');
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('General Enquiry - Escalated', NULL, NULL, true, 'collect-csos, collect-admins', 'GeneralEscalation');
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Survey Enquiry - Escalated', NULL, NULL, true, 'collect-csos, collect-admins', 'SurveyEscalation');
