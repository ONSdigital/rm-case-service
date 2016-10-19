--change description to long description and add column for short description

ALTER TABLE casesvc.category ADD COLUMN newcaserespondenttype character varying(10);


--drop constraint on caseevent referencing category

ALTER TABLE casesvc.caseevent
  DROP CONSTRAINT category_fkey;

-- truncate table

truncate casesvc.category;
--insert data

set schema 'casesvc';

INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('ACTION_CANCELLATION_COMPLETED', 'Action Cancellation Completed', NULL, false, NULL, NULL, NULL, 'Action Cancellation Completed', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('ACTION_CANCELLATION_CREATED', 'Action Cancellation Created', NULL, false, NULL, NULL, NULL, 'Action Cancellation Created', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('ACTION_COMPLETED', 'Action Completed', NULL, false, NULL, NULL, NULL, 'Action Completed', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('ACTION_CREATED', 'Action Created', NULL, false, NULL, NULL, NULL, 'Action Created', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('ACTION_UPDATED', 'Action Updated', NULL, false, NULL, NULL, NULL, 'Action Updated', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('ADDRESS_DETAILS_INCORRECT', 'Address Details Incorrect', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Address Details Incorrect', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('CASE_CREATED', 'Case Created', NULL, false, NULL, NULL, NULL, 'Case Created', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('CLASSIFICATION_INCORRECT', 'Classification Incorrect', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general', 'Classification Incorrect', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('GENERAL_COMPLAINT', 'General Complaint', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'General Complaint', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('GENERAL_ENQUIRY', 'General Enquiry', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'General Enquiry', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('MISCELLANEOUS', 'Miscellaneous', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Miscellaneous', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TECHNICAL_QUERY', 'Technical Query', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Technical Query', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('ACCESSIBILITY_MATERIALS', 'Accessibility Materials', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Accessibility Materials', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('PAPER_QUESTIONNAIRE_RESPONSE', 'Paper Questionnaire Response', 'DEACTIVATED', false, NULL, NULL, NULL, 'Paper Questionnaire Response', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('ONLINE_QUESTIONNAIRE_RESPONSE', 'Online Questionnaire Response', 'DISABLED', false, NULL, NULL, NULL, 'Online Questionnaire Response', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('UNDELIVERABLE', 'Undeliverable', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general', 'Undeliverable', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('INDIVIDUAL_RESPONSE_REQUESTED', 'Individual Response Requested', NULL, true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Response Requested', 'I');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('INDIVIDUAL_REPLACEMENT_IAC_REQUESTED', 'Individual Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Replacement IAC Requested', 'I');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('INDIVIDUAL_PAPER_REQUESTED', 'Individual Paper Requested', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Paper Requested', 'I');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_SOMALI', 'Somali Translation', NULL, true, 'collect-csos, collect-admins', 'QGSOM', 'translation', 'Somali', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_BENGALI', 'Bengali Translation', NULL, true, 'collect-csos, collect-admins', 'QGBEN', 'translation', 'Bengali', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_SPANISH', 'Spanish Translation', NULL, true, 'collect-csos, collect-admins', 'QGSPA', 'translation', 'Spanish', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_POLISH', 'Polish Translation', NULL, true, 'collect-csos, collect-admins', 'QGPOL', 'translation', 'Polish', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_CANTONESE', 'Cantonese Translation', NULL, true, 'collect-csos, collect-admins', 'QGCAN', 'translation', 'Cantonese', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_MANDARIN', 'Mandarin Translation', NULL, true, 'collect-csos, collect-admins', 'QGMAN', 'translation', 'Mandarin', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_PUNJABI_SHAHMUKI', 'Punjabi (Shahmuki) Translation', NULL, true, 'collect-csos, collect-admins', 'QGSHA', 'translation', 'Punjabi (Shahmuki)', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_LITHUANIAN', 'Lithuanian Translation', NULL, true, 'collect-csos, collect-admins', 'QGLIT', 'translation', 'Lithuanian', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('FIELD_COMPLAINT_ESCALATED', 'Field Complaint Escalated', NULL, true, 'collect-field-escalate, collect-csos, collect-admins', 'FC_ESCALATION', 'general', 'Field Complaint Escalated', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('FIELD_EMERGENCY_ESCALATED', 'Field Emergency Escalated', NULL, true, 'collect-field-escalate, collect-csos, collect-admins', 'FE_ESCALATION', 'general', 'Field Emergency Escalated', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('GENERAL_COMPLAINT_ESCALATED', 'General Complaint - Escalated', NULL, true, 'collect-general-escalate, collect-csos, collect-admins', 'GC_ESCALATION', 'general', 'General Complaint - Escalated', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('GENERAL_ENQUIRY_ESCALATED', 'General Enquiry - Escalated', NULL, true, 'collect-general-escalate, collect-csos, collect-admins', 'GE_ESCALATION', 'general', 'General Enquiry - Escalated', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('INCORRECT_ESCALATION', 'Incorrect Escalation', NULL, true, 'collect-field-escalate, collect-general-escalate, collect_admins', NULL, 'general', 'Incorrect Escalation', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('PENDING', 'Pending', NULL, true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Pending', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('REFUSAL', 'Refusal', 'DEACTIVATED', true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Refusal', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('HOUSEHOLD_REPLACEMENT_IAC_REQUESTED', 'Household Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, NULL, 'Household Replacement IAC Requested', 'H');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('HOUSEHOLD_PAPER_REQUESTED', 'Household Paper Requested', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, NULL, 'Household Paper Requested', 'H');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_PUNJABI_GURMUKHI', 'Punjabi (Gurmukhi) Translation', NULL, true, 'collect-csos, collect-admins', 'QGGUR', 'translation', 'Punjabi (Gurmukhi)', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_TURKISH', 'Turkish Translation', NULL, true, 'collect-csos, collect-admins', 'QGTUR', 'translation', 'Turkish', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_ARABIC', 'Arabic Translation', NULL, true, 'collect-csos, collect-admins', 'QGARA', 'translation', 'Arabic', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_PORTUGUESE', 'Portuguese Translation', NULL, true, 'collect-csos, collect-admins', 'QGPOR', 'translation', 'Portuguese', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_URDU', 'Urdu Translation', NULL, true, 'collect-csos, collect-admins', 'QGURD', 'translation', 'Urdu', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('TRANSLATION_GUJARATI', 'Gujarati Translation', NULL, true, 'collect-csos, collect-admins', 'QGGUJ', 'translation', 'Gujarati', NULL);
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype) VALUES ('CLOSE_ESCALATION', 'Close Escalation', NULL, true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Close Escalation', NULL);

--add constraint on category referencing respondenttype
ALTER TABLE ONLY casesvc.category
    ADD CONSTRAINT newcaserespondenttype_fkey FOREIGN KEY (newcaserespondenttype) REFERENCES casesvc.respondenttype(respondenttype);

--add constraint on caseevent referencing category
ALTER TABLE ONLY casesvc.caseevent
    ADD CONSTRAINT category_fkey FOREIGN KEY (category) REFERENCES casesvc.category(name);




