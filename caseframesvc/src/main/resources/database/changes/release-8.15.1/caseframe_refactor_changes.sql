--caseframe schema changes  

--case table changes
  ALTER TABLE caseframe.case RENAME COLUMN case_status TO status;
  ALTER TABLE caseframe.case RENAME COLUMN created_datetime TO createddatetime;
  ALTER TABLE caseframe.case RENAME COLUMN created_by TO createdby;
  ALTER TABLE caseframe.case 
  ALTER COLUMN caseid TYPE bigint,
  ALTER COLUMN createdby TYPE character varying(50);


--caseevent table changes

  ALTER TABLE caseframe.caseevent RENAME COLUMN created_datetime TO createddatetime;
  ALTER TABLE caseframe.caseevent RENAME COLUMN created_by TO createdby;
  ALTER TABLE caseframe.caseevent 
  ALTER COLUMN caseid TYPE bigint,
  ALTER COLUMN description TYPE character varying(350),
  ALTER COLUMN createdby TYPE character varying(50);

--casetype table changes

  ALTER TABLE caseframe.casetype RENAME COLUMN casetype_name TO name;

--category table changes

  ALTER TABLE caseframe.category ALTER COLUMN generatedactiontype TYPE character varying(100);

--sample table changes

  ALTER TABLE caseframe.sample RENAME COLUMN sample_name TO name;
  ALTER TABLE caseframe.sample RENAME COLUMN address_criteria TO addresscriteria;


--survey table changes

  ALTER TABLE caseframe.survey RENAME COLUMN survey_name TO name;

--questionnaire table changes
 ALTER TABLE caseframe.questionnaire RENAME COLUMN questionnaire_status TO status;
 ALTER TABLE caseframe.questionnaire RENAME COLUMN dispatch_datetime TO dispatchdatetime;
 ALTER TABLE caseframe.questionnaire RENAME COLUMN response_datetime TO responsedatetime;
 ALTER TABLE caseframe.questionnaire RENAME COLUMN receipt_datetime TO receiptdatetime;
 ALTER TABLE caseframe.questionnaire ALTER COLUMN questionnaireid TYPE bigint;
 ALTER TABLE caseframe.questionnaire  ALTER COLUMN caseid TYPE bigint;

--renaming of sequences

 ALTER SEQUENCE caseframe.caseid_seq RENAME TO caseidseq;
 ALTER SEQUENCE caseframe.qid_seq RENAME TO qidseq;