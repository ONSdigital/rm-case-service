
DROP FUNCTION casesvc.create_questionnaire(bigint);

CREATE OR REPLACE FUNCTION casesvc.create_questionnaire(
    p_caseid bigint,
    p_questionset character varying)
  RETURNS boolean AS
$BODY$
DECLARE
v_questionset character varying(10);
v_errmess text;

BEGIN

v_questionset := p_questionset;

-- Insert a record into the questionnaire table

INSERT INTO casesvc.questionnaire
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
 NEXTVAL('casesvc.qidseq'::regclass)
,p_caseid
--,state
--,dispatchdatetime
--,responsedatetime
--,receiptdatetime
,v_questionset
,SUBSTRING(CURRVAL('casesvc.qidseq'::regclass)::text,6,5) || SUBSTRING(CURRVAL('casesvc.qidseq'::regclass)::text,1,5)
);

  RETURN TRUE;

EXCEPTION

 WHEN OTHERS THEN
    PERFORM casesvc.logmessage(p_messagetext := 'CREATE QUESTIONNAIRE EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'casesvc.create_questionnaire');
  RETURN FALSE; 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION casesvc.create_questionnaire(bigint, character varying)
  OWNER TO postgres;
