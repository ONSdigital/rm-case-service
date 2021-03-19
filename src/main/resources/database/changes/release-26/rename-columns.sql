ALTER TABLE action.case RENAME COLUMN casepk TO case_pk;
ALTER TABLE action.case RENAME COLUMN sampleunit_id TO sample_unit_id;
ALTER TABLE action.case RENAME COLUMN casegroupfk TO case_group_fk;
ALTER TABLE action.case RENAME COLUMN casegroupid TO case_group_id;
ALTER TABLE action.case RENAME COLUMN sourcecase TO source_case_id;
ALTER TABLE action.case RENAME COLUMN caseref TO case_ref;
ALTER TABLE action.case RENAME COLUMN statefk TO state_fk;
ALTER TABLE action.case RENAME COLUMN sampleunittype TO sample_unit_type;
ALTER TABLE action.case RENAME COLUMN partyid TO party_id;
ALTER TABLE action.case RENAME COLUMN collectioninstrumentid TO collection_instrument_id;
ALTER TABLE action.case RENAME COLUMN actionplanid TO action_plan_id;
ALTER TABLE action.case RENAME COLUMN createddatetime TO created_date_time;
ALTER TABLE action.case RENAME COLUMN createdby TO created_by;

ALTER TABLE action.caseevent RENAME COLUMN caseeventpk TO case_event_pk;
ALTER TABLE action.caseevent RENAME COLUMN casefk TO case_fk;
ALTER TABLE action.caseevent RENAME COLUMN createdby TO created_by;
ALTER TABLE action.caseevent RENAME COLUMN createddatetime TO created_date_time;
ALTER TABLE action.caseevent RENAME COLUMN categoryfk TO category_fk;

ALTER TABLE action.casegroupstatusaudit RENAME COLUMN casegroupstatusauditpk TO case_group_status_audit_pk;
ALTER TABLE action.casegroupstatusaudit RENAME COLUMN casegroupfk TO case_group_fk;
ALTER TABLE action.casegroupstatusaudit RENAME COLUMN partyid TO party_id;
ALTER TABLE action.casegroupstatusaudit RENAME COLUMN createddatetime TO created_date_time;

ALTER TABLE action.caseiacaudit RENAME COLUMN caseiacauditpk TO case_iac_audit_pk;
ALTER TABLE action.caseiacaudit RENAME COLUMN casefk TO case_fk;
ALTER TABLE action.caseiacaudit RENAME COLUMN createddatetime TO created_date_time;

ALTER TABLE action.category RENAME COLUMN categorypk TO category_pk;
ALTER TABLE action.category RENAME COLUMN long_description TO long_description;
ALTER TABLE action.category RENAME COLUMN shortdescription TO short_description;
ALTER TABLE action.category RENAME COLUMN eventtype TO event_type;
ALTER TABLE action.category RENAME COLUMN oldcasesampleunittypes TO old_case_sample_unit_types;
ALTER TABLE action.category RENAME COLUMN newcasesampleunittype TO new_case_sample_unit_type;
ALTER TABLE action.category RENAME COLUMN generatedactiontype TO generated_action_type;
ALTER TABLE action.category RENAME COLUMN recalccollectioninstrument TO recalc_collection_instrument;

ALTER TABLE action.response RENAME COLUMN responsepk TO response_pk;
ALTER TABLE action.response RENAME COLUMN casefk TO case_fk;
ALTER TABLE action.response RENAME COLUMN inboundchannel TO inbound_channel;
ALTER TABLE action.response RENAME COLUMN responsedatetime TO response_date_time;
