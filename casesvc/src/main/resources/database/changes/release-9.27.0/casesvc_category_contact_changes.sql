--change description to long description and add column for short description

ALTER TABLE casesvc.category ADD COLUMN shortdescription character varying(50);

ALTER TABLE casesvc.category RENAME description  TO longdescription;

ALTER TABLE casesvc.category
   ALTER COLUMN role TYPE character varying(100);


--drop constraint on caseevent referencing category

ALTER TABLE casesvc.caseevent
  DROP CONSTRAINT category_fkey;

-- truncate table

truncate casesvc.category;
--insert data

set schema 'casesvc';

INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('ACTION_CANCELLATION_COMPLETED', 'Action Cancellation Completed', NULL, false, NULL, NULL, NULL, 'Action Cancellation Completed');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('ACTION_CANCELLATION_CREATED', 'Action Cancellation Created', NULL, false, NULL, NULL, NULL, 'Action Cancellation Created');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('ACTION_COMPLETED', 'Action Completed', NULL, false, NULL, NULL, NULL, 'Action Completed');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('ACTION_CREATED', 'Action Created', NULL, false, NULL, NULL, NULL, 'Action Created');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('ACTION_UPDATED', 'Action Updated', NULL, false, NULL, NULL, NULL, 'Action Updated');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('ADDRESS_DETAILS_INCORRECT', 'Address Details Incorrect', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Address Details Incorrect');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('CASE_CREATED', 'Case Created', NULL, false, NULL, NULL, NULL, 'Case Created');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('CLASSIFICATION_INCORRECT', 'Classification Incorrect', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general', 'Classification Incorrect');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('GENERAL_COMPLAINT', 'General Complaint', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'General Complaint');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('GENERAL_ENQUIRY', 'General Enquiry', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'General Enquiry');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('MISCELLANEOUS', 'Miscellaneous', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Miscellaneous');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TECHNICAL_QUERY', 'Technical Query', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Technical Query');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('ACCESSIBILITY_MATERIALS', 'Accessibility Materials', NULL, true, 'collect-csos, collect-admins', NULL, 'general', 'Accessibility Materials');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('PAPER_QUESTIONNAIRE_RESPONSE', 'Paper Questionnaire Response', 'DEACTIVATED', false, NULL, NULL, NULL, 'Paper Questionnaire Response');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('ONLINE_QUESTIONNAIRE_RESPONSE', 'Online Questionnaire Response', 'DISABLED', false, NULL, NULL, NULL, 'Online Questionnaire Response');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('UNDELIVERABLE', 'Undeliverable', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, 'general', 'Undeliverable');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('INDIVIDUAL_RESPONSE_REQUESTED', 'Individual Response Requested', NULL, true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Response Requested');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('INDIVIDUAL_REPLACEMENT_IAC_REQUESTED', 'Individual Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Replacement IAC Requested');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('INDIVIDUAL_PAPER_REQUESTED', 'Individual Paper Requested', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, NULL, 'Individual Paper Requested');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_SOMALI', 'Translation Somali', NULL, true, 'collect-csos, collect-admins', 'QGSOM', 'translation', 'Somali');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_BENGALI', 'Translation Bengali', NULL, true, 'collect-csos, collect-admins', 'QGBEN', 'translation', 'Bengali');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_SPANISH', 'Translation Spanish', NULL, true, 'collect-csos, collect-admins', 'QGSPA', 'translation', 'Spanish');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_POLISH', 'Translation Polish', NULL, true, 'collect-csos, collect-admins', 'QGPOL', 'translation', 'Polish');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_CANTONESE', 'Translation Cantonese', NULL, true, 'collect-csos, collect-admins', 'QGCAN', 'translation', 'Cantonese');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_MANDARIN', 'Translation Mandarin', NULL, true, 'collect-csos, collect-admins', 'QGMAN', 'translation', 'Mandarin');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_PUNJABI_SHAHMUKI', 'Translation Punjabi – Shahmuki', NULL, true, 'collect-csos, collect-admins', 'QGSHA', 'translation', 'Punjabi – Shahmuki');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_LITHUANIAN', 'Translation Lithuanian', NULL, true, 'collect-csos, collect-admins', 'QGLIT', 'translation', 'Lithuanian');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('FIELD_COMPLAINT_ESCALATED', 'Field Complaint Escalated', NULL, true, 'collect-field-escalate, collect-csos, collect-admins', 'FC_ESCALATION', 'general', 'Field Complaint Escalated');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('FIELD_EMERGENCY_ESCALATED', 'Field Emergency Escalated', NULL, true, 'collect-field-escalate, collect-csos, collect-admins', 'FE_ESCALATION', 'general', 'Field Emergency Escalated');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('GENERAL_COMPLAINT_ESCALATED', 'General Complaint - Escalated', NULL, true, 'collect-general-escalate, collect-csos, collect-admins', 'GC_ESCALATION', 'general', 'General Complaint - Escalated');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('GENERAL_ENQUIRY_ESCALATED', 'General Enquiry - Escalated', NULL, true, 'collect-general-escalate, collect-csos, collect-admins', 'GE_ESCALATION', 'general', 'General Enquiry - Escalated');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('INCORRECT_ESCALATION', 'Incorrect Escalation', NULL, true, 'collect-field-escalate, collect-general-escalate, collect_admins', NULL, 'general', 'Incorrect Escalation');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('PENDING', 'Pending', NULL, true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Pending');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('REFUSAL', 'Refusal', 'DEACTIVATED', true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Refusal');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('HOUSEHOLD_REPLACEMENT_IAC_REQUESTED', 'Household Replacement IAC Requested', 'DISABLED', true, 'collect-csos, collect-admins', NULL, NULL, 'Household Replacement IAC Requested');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('HOUSEHOLD_PAPER_REQUESTED', 'Household Paper Requested', 'DEACTIVATED', true, 'collect-csos, collect-admins', NULL, NULL, 'Household Paper Requested');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_PUNJABI_GURMUKHI', 'Translation Punjabi – Gurmukhi', NULL, true, 'collect-csos, collect-admins', 'QGGUR', 'translation', 'Punjabi – Gurmukhi');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_TURKISH', 'Translation Turkish', NULL, true, 'collect-csos, collect-admins', 'QGTUR', 'translation', 'Turkish');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_ARABIC', 'Translation Arabic', NULL, true, 'collect-csos, collect-admins', 'QGARA', 'translation', 'Arabic');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_PORTUGUESE', 'Translation Portuguese', NULL, true, 'collect-csos, collect-admins', 'QGPOR', 'translation', 'Portuguese');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_URDU', 'Translation Urdu', NULL, true, 'collect-csos, collect-admins', 'QGURD', 'translation', 'Urdu');
INSERT INTO category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription) VALUES ('TRANSLATION_GUJARATI', 'Translation Gujarati', NULL, true, 'collect-csos, collect-admins', 'QGGUJ', 'translation', 'Gujarati');


--add constraint on caseevent referencing category

ALTER TABLE ONLY casesvc.caseevent
    ADD CONSTRAINT category_fkey FOREIGN KEY (category) REFERENCES casesvc.category(name);

--add title column to contact table

ALTER TABLE casesvc.contact
   ADD COLUMN title character varying(20);

--create sequence for contact it
CREATE SEQUENCE contactidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE casesvc.contactidseq OWNER TO postgres;

--change contactid on contact to automatically use sequence

ALTER TABLE casesvc.contact
   ALTER COLUMN contactid SET DEFAULT nextval('casesvc.contactidseq'::regclass);

