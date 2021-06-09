ALTER TABLE casesvc.category RENAME COLUMN categorypk TO category_pk;
ALTER TABLE casesvc.category RENAME COLUMN shortdescription TO short_description;
ALTER TABLE casesvc.category RENAME COLUMN longdescription TO long_description;
ALTER TABLE casesvc.category RENAME COLUMN eventtype TO event_type;
ALTER TABLE casesvc.category RENAME COLUMN generatedactiontype TO generated_action_type;
ALTER TABLE casesvc.category RENAME COLUMN oldcasesampleunittypes TO old_case_sample_unit_types;
ALTER TABLE casesvc.category RENAME COLUMN newcasesampleunittype TO new_case_sample_unit_type;
ALTER TABLE casesvc.category RENAME COLUMN recalccollectioninstrument TO recalc_collection_instrument;

ALTER TABLE casesvc.caseevent RENAME COLUMN caseeventpk TO case_event_pk;
ALTER TABLE casesvc.caseevent RENAME COLUMN casefk TO case_fk;
ALTER TABLE casesvc.caseevent RENAME COLUMN createdby TO created_by;
ALTER TABLE casesvc.caseevent RENAME COLUMN createddatetime TO created_date_time;
ALTER TABLE casesvc.caseevent RENAME COLUMN categoryfk TO category_fk;

ALTER TABLE casesvc.response RENAME COLUMN responsepk TO response_pk;
ALTER TABLE casesvc.response RENAME COLUMN casefk TO case_fk;
ALTER TABLE casesvc.response RENAME COLUMN inboundchannel TO inbound_channel;
ALTER TABLE casesvc.response RENAME COLUMN responsedatetime TO response_date_time;

ALTER TABLE casesvc.case RENAME COLUMN casepk TO case_pk;
ALTER TABLE casesvc.case RENAME COLUMN caseref TO case_ref;
ALTER TABLE casesvc.case RENAME COLUMN casegroupfk TO case_group_fk;
ALTER TABLE casesvc.case RENAME COLUMN casegroupid TO case_group_id;
ALTER TABLE casesvc.case RENAME COLUMN partyid TO party_id;
ALTER TABLE casesvc.case RENAME COLUMN sampleunittype TO sample_unit_type;
ALTER TABLE casesvc.case RENAME COLUMN collectioninstrumentid TO collection_instrument_id;
ALTER TABLE casesvc.case RENAME COLUMN statefk TO state_fk;
ALTER TABLE casesvc.case RENAME COLUMN actionplanid TO action_plan_id;
ALTER TABLE casesvc.case RENAME COLUMN createddatetime TO created_date_time;
ALTER TABLE casesvc.case RENAME COLUMN createdby TO created_by;
ALTER TABLE casesvc.case RENAME COLUMN sourcecase TO source_case;
ALTER TABLE casesvc.case RENAME COLUMN optlockversion TO opt_lock_version;

ALTER TABLE casesvc.casestate RENAME COLUMN statepk TO state_pk;

ALTER TABLE casesvc.casegroup RENAME COLUMN casegrouppk TO case_group_pk;
ALTER TABLE casesvc.casegroup RENAME COLUMN partyid TO party_id;
ALTER TABLE casesvc.casegroup RENAME COLUMN collectionexerciseid TO collection_exercise_id;
ALTER TABLE casesvc.casegroup RENAME COLUMN sampleunitref TO sample_unit_ref;
ALTER TABLE casesvc.casegroup RENAME COLUMN sampleunittype TO sample_unit_type;
ALTER TABLE casesvc.casegroup RENAME COLUMN surveyid TO survey_id;

ALTER TABLE casesvc.caseiacaudit RENAME COLUMN caseiacauditpk TO case_iac_audit_pk;
ALTER TABLE casesvc.caseiacaudit RENAME COLUMN casefk TO case_fk;
ALTER TABLE casesvc.caseiacaudit RENAME COLUMN createddatetime TO created_date_time;

ALTER TABLE casesvc.casegroupstatusaudit RENAME COLUMN casegroupstatusauditpk TO case_group_status_audit_pk;
ALTER TABLE casesvc.casegroupstatusaudit RENAME COLUMN casegroupfk TO case_group_fk;
ALTER TABLE casesvc.casegroupstatusaudit RENAME COLUMN partyid TO party_id;
ALTER TABLE casesvc.casegroupstatusaudit RENAME COLUMN createddatetime TO created_date_time;