--generate-cases


--create_questionnaire

DROP FUNCTION caseframe.create_questionnaire(integer);

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
--,questionnaire_status
--,dispatch_datetime
--,response_datetime
--,receipt_datetime
,questionset
,iac 
) 
(SELECT
 NEXTVAL('caseframe.qidseq'::regclass)
,p_caseid
--,questionnaire_status
--,dispatch_datetime
--,response_datetime
--,receipt_datetime
,v_questionset
,CURRVAL('caseframe.qidseq'::regclass) || caseframe.iac_generator()
);

  RETURN TRUE;

EXCEPTION

 WHEN OTHERS THEN
    PERFORM caseframe.logmessage(p_messagetext := 'CREATE QUESTIONNAIE EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
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

