

-- Function: caseframe.logmessage(text, numeric, text, text)

-- DROP FUNCTION caseframe.logmessage(text, numeric, text, text);

CREATE OR REPLACE FUNCTION caseframe.logmessage(p_messagetext text DEFAULT NULL::text, p_jobid numeric DEFAULT NULL::numeric, p_messagelevel text DEFAULT NULL::text, 

p_functionname text DEFAULT NULL::text)
  RETURNS boolean AS
$BODY$
DECLARE
v_text TEXT ;
v_function TEXT;
BEGIN
INSERT INTO caseframe.messagelog
(messagetext, jobid, messagelevel, functionname, createddatetime )
values (p_messagetext, p_jobid, p_messagelevel, p_functionname, current_timestamp);
  RETURN TRUE;
EXCEPTION
WHEN OTHERS THEN
RETURN FALSE;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.logmessage(text, numeric, text, text)
  OWNER TO postgres;





-- Function: caseframe.pseudo_encrypt(bigint)
-- DROP FUNCTION caseframe.pseudo_encrypt(bigint);

CREATE OR REPLACE FUNCTION caseframe.pseudo_encrypt(value bigint)
  RETURNS integer AS
$BODY$
DECLARE
l1 int;
l2 int;
r1 int;
r2 int;
i int:=0;
BEGIN
 l1:= (VALUE >> 16) & 65535;
 r1:= VALUE & 65535;
 WHILE i < 3 LOOP
   l2 := r1;
   r2 := l1 # ((((1366 * r1 + 150889) % 714025) / 714025.0) * 32767)::int;
   l1 := l2;
   r1 := r2;
   i := i + 1;
 END LOOP;
 RETURN ((r1 << 16) + l1);

EXCEPTION

 WHEN OTHERS THEN
    PERFORM caseframe.logmessage(p_messagetext := 'CREATE ENCRYPTED BIGINT EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.pseudo_encrypt');
  RETURN FALSE;    

END;
$BODY$
  LANGUAGE plpgsql IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION caseframe.pseudo_encrypt(bigint)
  OWNER TO postgres;



-- Function: caseframe.iac_generator()
-- DROP FUNCTION caseframe.iac_generator();

CREATE OR REPLACE FUNCTION caseframe.iac_generator(OUT iac character)
  RETURNS character AS
$BODY$
DECLARE
v_errmess text;
BEGIN
    iac := SUBSTRING(lpad((ltrim(to_char((abs((select pseudo_encrypt from caseframe.pseudo_encrypt(currval

('caseframe.qid_seq'::regclass))))),'99999999999999999999'))),20,'0'),11,10);  


EXCEPTION

 WHEN OTHERS THEN
    PERFORM caseframe.logmessage(p_messagetext := 'CREATE AN IAC CODE EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.iac_generator');         
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.iac_generator()
  OWNER TO postgres;



-- Function: caseframe.create_questionnaires()

-- DROP FUNCTION caseframe.create_questionnaires();

CREATE OR REPLACE FUNCTION caseframe.create_questionnaires()
  RETURNS boolean AS
$BODY$
DECLARE
r_case record;
v_case_count integer;
BEGIN
   -- Initialse case count
   v_case_count := 0;

   -- For each case in the case table call the funtion to create a questionnaire
   FOR r_case IN SELECT c.caseid 
                  FROM caseframe.case c
                  WHERE NOT EXISTS
                        (SELECT q.caseid 
                         FROM caseframe.questionnaire q 
                         WHERE q.caseid = c.caseid) LOOP
        
	EXECUTE 'SELECT * FROM caseframe.create_questionnaire(' || r_case.caseid || ')';
        v_case_count := v_case_count + 1;
   END LOOP;

   PERFORM caseframe.logmessage(p_messagetext := v_case_count || ' NEW QUESTIONNAIRE(S) CREATED FROM CASE TABLE'
                            ,p_jobid := 0   
                            ,p_messagelevel := 'INFO'
                            ,p_functionname := 'caseframe.create_questionnaires');


RETURN TRUE;

EXCEPTION

 WHEN OTHERS THEN
    PERFORM action.logmessage(p_messagetext := 'CREATE QUESTIONNAIES EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.create_questionnaires');
  RETURN FALSE;    

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.create_questionnaires()
  OWNER TO postgres;


-- Function: caseframe.create_questionnaire(integer)

-- DROP FUNCTION caseframe.create_questionnaire(integer);

CREATE OR REPLACE FUNCTION caseframe.create_questionnaire(p_caseid integer)
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
 NEXTVAL('caseframe.qid_seq'::regclass)
,p_caseid
--,questionnaire_status
--,dispatch_datetime
--,response_datetime
--,receipt_datetime
,v_questionset
,CURRVAL('caseframe.qid_seq'::regclass) || caseframe.iac_generator()
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
ALTER FUNCTION caseframe.create_questionnaire(integer)
  OWNER TO postgres;
