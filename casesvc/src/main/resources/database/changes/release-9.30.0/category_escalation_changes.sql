--category changes for escalated alls and refusals 

UPDATE CASESVC.CATEGORY
SET role = 'collect-csos, collect-admins'
WHERE NAME IN ('FIELD_COMPLAINT_ESCALATED','FIELD_EMERGENCY_ESCALATED','GENERAL_COMPLAINT_ESCALATED','GENERAL_ENQUIRY_ESCALATED');

UPDATE CASESVC.CATEGORY
SET role = 'collect-csos, collect-admins'
WHERE NAME = 'REFUSAL';

INSERT INTO casesvc.category (name, longdescription, eventtype, manual, role, generatedactiontype, "group", shortdescription, newcaserespondenttype, oldcaserespondenttype) VALUES ('ESCALATED_REFUSAL', 'Escalated Refusal', 'DEACTIVATED', true, 'collect-field-escalate, collect-general-escalate, collect-admins', NULL, 'general', 'Escalated Refusal', NULL, NULL);

