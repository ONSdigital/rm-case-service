--initial data to insert into caseframe
-- tables are casestate, casetype, category, questionset, sample, survey
--this script populates with the data used for 2016 Census Test.

SET SCHEMA 'caseframe';

INSERT INTO casestate (state, description) VALUES ('INIT', 'Initial creation of case');
INSERT INTO casestate (state, description) VALUES ('CLOSED', 'Case Closed ');
INSERT INTO casestate (state, description) VALUES ('RESPONDED', '2016 Hotels only response received');


--
-- Data for Name: questionset; Type: TABLE DATA; Schema: caseframe; Owner: role_connect
--

INSERT INTO questionset (questionset, description) VALUES ('HH', 'Households');
INSERT INTO questionset (questionset, description) VALUES ('CE', 'Communal Establishments');


--
-- Data for Name: casetype; Type: TABLE DATA; Schema: caseframe; Owner: role_connect
--

INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (1, 'HH', 'Household', 1, 'HH');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (2, 'HGH', 'Hotel Guest House Bed and Breakfast', 3, 'CE');
INSERT INTO casetype (casetypeid, name, description, actionplanid, questionset) VALUES (3, 'CH', 'Care Home', 2, 'CE');


--
-- Data for Name: category; Type: TABLE DATA; Schema: caseframe; Owner: role_connect
--

INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('CaseCreated', 'Initial Creation Of Case', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('CaseClosed', 'Closure of Case', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('ActionUpdated', 'Action updated', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('ActionCompleted', 'Action completed', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('ActionCreated', 'Action created', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('General Enquiry', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Complaint', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Survey Enquiry', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Address Details Incorrect', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Classification Incorrect', NULL, true, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Request for Fulfilment', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Technical Query', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Miscellaneous', NULL, NULL, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Pending', 'Pending', NULL, true, 'collect-escalate, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Complaint - Escalated', NULL, NULL, true, 'collect-csos, collect-admins', 'ComplaintEscalation');
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('General Enquiry - Escalated', NULL, NULL, true, 'collect-csos, collect-admins', 'GeneralEscalation');
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Survey Enquiry - Escalated', NULL, NULL, true, 'collect-csos, collect-admins', 'SurveyEscalation');
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Undeliverable', NULL, true, true, 'collect-csos, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('QuestionnaireResponse', 'Questionnaire Response logged', true, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('ActionCancellationCompleted', 'Action cancellation completed', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('ActionCancellationCreated', 'Action cancellation created', NULL, false, NULL, NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Closed', 'Closed', NULL, true, 'collect-escalate, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('Refusal', NULL, true, true, 'collect-csos, collect-escalate, collect-admins', NULL);
INSERT INTO category (name, description, closecase, manual, role, generatedactiontype) VALUES ('IncorrectEscalation', 'Incorrect Escalation', NULL, true, 'collect-escalate, collect-admins', NULL);


--
-- Data for Name: survey; Type: TABLE DATA; Schema: caseframe; Owner: role_connect
--

INSERT INTO survey (surveyid, name, description) VALUES (1, '2016 Test', '2016 Census Test');


--
-- Data for Name: sample; Type: TABLE DATA; Schema: caseframe; Owner: role_connect
--

INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (1, 'Residential', 'Households', 'addresstype = ''HH''', 1, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (2, 'Hotels Guest Houses', 'Hotels Guest Houses Bed and Breakfasts', 'addresstype = ''CE'' and category = ''HOTEL''', 2, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (3, 'Care Homes', 'Care Homes', 'addresstype = ''CE'' and category = ''CARE HOME''', 3, 1);



