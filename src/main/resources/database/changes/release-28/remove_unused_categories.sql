DELETE FROM casesvc.caseevent WHERE category_fk = 'COMMUNAL_ESTABLISHMENT_INSTITUTION';
DELETE FROM casesvc.category WHERE category_pk = 'COMMUNAL_ESTABLISHMENT_INSTITUTION';

DELETE FROM casesvc.caseevent WHERE category_fk = 'DECEASED';
DELETE FROM casesvc.category WHERE category_pk = 'DECEASED';

DELETE FROM casesvc.caseevent WHERE category_fk = 'DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS';
DELETE FROM casesvc.category WHERE category_pk = 'DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS';

DELETE FROM casesvc.caseevent WHERE category_fk = 'FULL_INTERVIEW_REQUEST_DATA_DELETED';
DELETE FROM casesvc.category WHERE category_pk = 'FULL_INTERVIEW_REQUEST_DATA_DELETED';

DELETE FROM casesvc.caseevent WHERE category_fk = 'FULL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT';
DELETE FROM casesvc.category WHERE category_pk = 'FULL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT';

DELETE FROM casesvc.caseevent WHERE category_fk = 'ILL_AT_HOME';
DELETE FROM casesvc.category WHERE category_pk = 'ILL_AT_HOME';

DELETE FROM casesvc.caseevent WHERE category_fk = 'IN_HOSPITAL';
DELETE FROM casesvc.category WHERE category_pk = 'IN_HOSPITAL';

DELETE FROM casesvc.caseevent WHERE category_fk = 'LEGITIMACY_CONCERNS';
DELETE FROM casesvc.category WHERE category_pk = 'LEGITIMACY_CONCERNS';

DELETE FROM casesvc.caseevent WHERE category_fk = 'NO_PERSON_IN_ELIGIBLE_AGE_RANGE';
DELETE FROM casesvc.category WHERE category_pk = 'NO_PERSON_IN_ELIGIBLE_AGE_RANGE';

DELETE FROM casesvc.caseevent WHERE category_fk = 'NO_TRACE_OF_ADDRESS';
DELETE FROM casesvc.category WHERE category_pk = 'NO_TRACE_OF_ADDRESS';

DELETE FROM casesvc.caseevent WHERE category_fk = 'OTHER_CIRCUMSTANTIAL_REFUSAL';
DELETE FROM casesvc.category WHERE category_pk = 'OTHER_CIRCUMSTANTIAL_REFUSAL';

DELETE FROM casesvc.caseevent WHERE category_fk = 'OTHER_OUTRIGHT_REFUSAL';
DELETE FROM casesvc.category WHERE category_pk = 'OTHER_OUTRIGHT_REFUSAL';

DELETE FROM casesvc.caseevent WHERE category_fk = 'PARTIAL_INTERVIEW_REQUEST_DATA_DELETED';
DELETE FROM casesvc.category WHERE category_pk = 'PARTIAL_INTERVIEW_REQUEST_DATA_DELETED';

DELETE FROM casesvc.caseevent WHERE category_fk = 'PARTIAL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT';
DELETE FROM casesvc.category WHERE category_pk = 'PARTIAL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT';

DELETE FROM casesvc.caseevent WHERE category_fk = 'REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT';
DELETE FROM casesvc.category WHERE category_pk = 'REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT';

DELETE FROM casesvc.caseevent WHERE category_fk = 'VACANT_OR_EMPTY';
DELETE FROM casesvc.category WHERE category_pk = 'VACANT_OR_EMPTY';

DELETE FROM casesvc.caseevent WHERE category_fk = 'WRONG_ADDRESS';
DELETE FROM casesvc.category WHERE category_pk = 'WRONG_ADDRESS';