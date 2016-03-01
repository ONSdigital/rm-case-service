

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
END;
$BODY$
  LANGUAGE plpgsql IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION caseframe.pseudo_encrypt(bigint)
  OWNER TO postgres;




CREATE OR REPLACE FUNCTION caseframe.iac_generator(OUT iac character)
  RETURNS character AS
$BODY$
BEGIN
    iac := SUBSTRING(lpad((ltrim(to_char((abs((select pseudo_encrypt from caseframe.pseudo_encrypt(currval('caseframe.qid_seq'::regclass))))),'99999999999999999999'))),20,'0'),11,10);           
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.iac_generator()
  OWNER TO postgres;




CREATE OR REPLACE FUNCTION caseframe.create_questionnaires()
  RETURNS boolean AS
$BODY$
DECLARE
r_case record;
BEGIN

   -- For each case in the case table call the funtion to create a questionnaire
   FOR r_case IN SELECT c.caseid 
                  FROM caseframe.case c
                  WHERE NOT EXISTS
                        (SELECT q.caseid 
                         FROM caseframe.questionnaire q 
                         WHERE q.caseid = c.caseid) LOOP
	EXECUTE 'SELECT * FROM caseframe.create_questionnaire(' || r_case.caseid || ')';
   END LOOP;

RETURN true;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.create_questionnaires()
  OWNER TO postgres;



CREATE OR REPLACE FUNCTION caseframe.create_questionnaire(p_caseid integer)
  RETURNS boolean AS
$BODY$
DECLARE
v_questionset character varying(10);


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

  RETURN true;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.create_questionnaire(integer)
  OWNER TO postgres;
