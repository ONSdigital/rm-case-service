--insert category table data

set schema 'caseframe';

INSERT INTO category VALUES ('General Enquiry', NULL, NULL, true, NULL, NULL);
INSERT INTO category VALUES ('General Enquiry - Escalated', NULL, NULL, true, NULL, 'Escalated');
INSERT INTO category VALUES ('Complaint', NULL, NULL, true, NULL, NULL);
INSERT INTO category VALUES ('Complaint - Escalated', NULL, NULL, true, NULL, 'Escalated');
INSERT INTO category VALUES ('Survey Enquiry', NULL, NULL, true, NULL, NULL);
INSERT INTO category VALUES ('Survey Enquiry - Escalated', NULL, NULL, true, NULL, 'Escalated');
INSERT INTO category VALUES ('Address Details Incorrect', NULL, NULL, true, NULL, NULL);
INSERT INTO category VALUES ('Classification Incorrect', NULL, true, true, NULL, NULL);
INSERT INTO category VALUES ('Refusal', NULL, true, true, NULL, NULL);
INSERT INTO category VALUES ('Request for Fulfilment', NULL, NULL, true, NULL, NULL);
INSERT INTO category VALUES ('Technical Query', NULL, NULL, true, NULL, NULL);
INSERT INTO category VALUES ('Miscellaneous', NULL, NULL, true, NULL, NULL);
INSERT INTO category VALUES ('CaseCreated', 'Initial Creation Of Case', NULL, false, NULL, NULL);
INSERT INTO category VALUES ('CaseClosed', 'Closure of Case', NULL, false, NULL, NULL);
INSERT INTO category VALUES ('ActionUpdated', 'Action updated', NULL, false, NULL, NULL);
INSERT INTO category VALUES ('ActionCompleted', 'Action completed', NULL, false, NULL, NULL);
INSERT INTO category VALUES ('ActionCreated', 'Action created', NULL, false, NULL, NULL);
INSERT INTO category VALUES ('QuestionnareResponse', 'Questionnaire Response logged', NULL, false, NULL, NULL);
