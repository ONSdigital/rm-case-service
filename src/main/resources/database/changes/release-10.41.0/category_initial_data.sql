DELETE FROM casesvc.category WHERE categorypk = 'INDIVIDUAL_RESPONSE_REQUESTED';
DELETE FROM casesvc.category WHERE categorypk = 'RESPONDENT_ENROLLED';

ALTER TABLE casesvc.category RENAME oldcasesampleunittype TO oldcasesampleunittypes;

UPDATE casesvc.category SET oldcasesampleunittypes = 'B,BI' WHERE categoryPK like 'ACTION%';

ALTER TABLE casesvc.category ALTER COLUMN oldcasesampleunittypes SET NOT NULL;

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('CASE_CREATED', 'Case Created', 'Case Created', NULL, NULL, NULL, NULL,'B,BI',NULL,  NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('RESPONDENT_ACCOUNT_CREATED', 'Account created for respondent', 'Account created for respondent', 'ACCOUNT_CREATED', NULL, NULL, NULL, 'B', NULL,  NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('ACCESS_CODE_AUTHENTICATION_ATTEMPT', 'Access Code authentication attempted', 'Access Code authentication attempted', NULL, NULL, NULL, NULL, 'B',NULL,  NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('COLLECTION_INSTRUMENT_DOWNLOADED', 'Collection Instrument Downloaded', 'Collection Instrument Downloaded', NULL, NULL, NULL, NULL, 'BI', NULL, NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('ACTION_CANCELLATION_COMPLETED', 'Action Cancellation Completed', 'Action Cancellation Completed', NULL, NULL, NULL, NULL, 'B,BI', NULL, NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('ACTION_CANCELLATION_CREATED', 'Action Cancellation Created', 'Action Cancellation Created', NULL, NULL, NULL, NULL,  'B,BI', NULL, NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('UNSUCCESSFUL_RESPONSE_UPLOAD', 'Unsuccessful Response Upload', 'Unsuccessful Response Upload', NULL, NULL, NULL, NULL, 'BI', NULL, NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('SUCCESSFUL_RESPONSE_UPLOAD', 'Successful Response Upload', 'Successful Response Upload', 'DISABLED',  NULL, NULL, NULL, 'BI', NULL, NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('OFFLINE_RESPONSE_PROCESSED', 'Offline Response Processed', 'Offline Response Processed', NULL,  NULL, NULL, NULL, 'BI', NULL, NULL);

INSERT INTO casesvc.category (categorypk, shortdescription, longdescription, eventtype, role, generatedactiontype, "group", oldcasesampleunittypes, newcasesampleunittype, recalccollectioninstrument) VALUES ('RESPONDENT_ENROLED', 'Respondent Enroled', 'Respondent Enroled', 'DEACTIVATED',  NULL, NULL, NULL, 'B', 'BI', NULL);
