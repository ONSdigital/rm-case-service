--change length of the iac field on questionnaire to 10 instead of 20
ALTER TABLE caseframe.questionnaire
   ALTER COLUMN iac TYPE character(10);

--drop functions iac_generatpr and pseudo encrypt as no longer used
DROP FUNCTION caseframe.iac_generator();
DROP FUNCTION caseframe.pseudo_encrypt(bigint);



-- Function: caseframe.create_questionnaire(bigint)

-- DROP FUNCTION caseframe.create_questionnaire(bigint);

CREATE OR REPLACE FUNCTION caseframe.create_questionnaire(p_caseid bigint)
  RETURNS boolean AS
$BODY$
DECLARE
v_questionset character varying(10);
v_errmess text;

BEGIN

-- Called to create a questionnaire for the case id passed in

-- Get the formtype from the case table for the case id passed in
SELECT c.questionset INTO v_questionset
FROM caseframe.case c
WHERE caseid = p_caseid;

-- Insert a record into the questionnaire table

INSERT INTO caseframe.questionnaire
(
 questionnaireid
,caseid
--,state
--,dispatchdatetime
--,responsedatetime
--,receiptdatetime
,questionset
,iac 
) 
(SELECT
 NEXTVAL('caseframe.qidseq'::regclass)
,p_caseid
--,state
--,dispatchdatetime
--,responsedatetime
--,receiptdatetime
,v_questionset
,SUBSTRING(CURRVAL('caseframe.qidseq'::regclass)::text,6,5) || SUBSTRING(CURRVAL('caseframe.qidseq'::regclass)::text,1,5)
);

  RETURN TRUE;

EXCEPTION

 WHEN OTHERS THEN
    PERFORM caseframe.logmessage(p_messagetext := 'CREATE QUESTIONNAIRE EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.create_questionnaire');
  RETURN FALSE; 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.create_questionnaire(bigint)
  OWNER TO postgres;
