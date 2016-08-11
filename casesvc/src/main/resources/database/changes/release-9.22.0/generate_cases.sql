
DROP FUNCTION casesvc.generate_cases(integer, character varying, character varying);

-- Function: casesvc.generate_cases(integer, character varying, character varying)

-- DROP FUNCTION casesvc.generate_cases(integer, character varying, character varying);

CREATE OR REPLACE FUNCTION casesvc.generate_cases(
    IN p_sampleid integer,
    IN p_geog_area_type character varying,
    IN p_geog_area_code character varying)
  RETURNS TABLE(p_caseid_out bigint, p_actionplanid_out integer) AS
$BODY$
DECLARE
    v_address_loop RECORD;
    v_sampleid integer;
    v_geog_select_text character varying;
    v_address_criteria character varying;
    v_casetypeid integer;
    v_actionplanid integer;
    v_questionset character varying;
    v_surveyid integer;
    v_caseid bigint;
    v_sql_text character varying;
    v_caseeventid bigint;
    v_cases_already_generated integer;
    v_number_of_cases integer;
   

BEGIN

--check that have a valid area type passed in

IF p_geog_area_type NOT IN ('MSOA','LA','REGION')
THEN
 RAISE SQLSTATE 'Z0001' using message = p_geog_area_type ||' is not a valid geographic area type. Must be of type MSOA , LA or REGION' ;
END IF;

--When the address criteria is fincalised for 2017 then this should be tidied up

 SELECT addresscriteria FROM casesvc.sample WHERE sampleid = p_sampleid INTO v_address_criteria;
 
           SELECT CASE p_geog_area_type
           WHEN 'OA' THEN   'oa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LSOA' THEN  'lsoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'MSOA' THEN 'msoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LA' THEN 'lad12cd = ''' || p_geog_area_code ||''''
           WHEN 'REGION' THEN 'region11cd = ''' || p_geog_area_code || ''''
          ELSE '0=1' --not a valid area type
       END INTO v_geog_select_text ;

--check to see if any of the uprn's already have a case created. If so then
-- log message and do not run sample generation.


EXECUTE 'select count(a.uprn) 
from casesvc.address a
inner join casesvc.case c on (a.uprn = c.uprn)
where ' || v_geog_select_text || ' and ' || v_address_criteria INTO v_cases_already_generated;

IF v_cases_already_generated > 0
THEN
RAISE SQLSTATE 'Z0001' using message = 'Cases not created. There are already ' || v_cases_already_generated || ' cases for this sample' ;
END IF;

--assign into variables for insert statement


v_sampleid := p_sampleid;
SELECT surveyid FROM casesvc.sample WHERE sampleid = v_sampleid INTO v_surveyid;
SELECT casetypeid FROM casesvc.sample WHERE sampleid = v_sampleid INTO v_casetypeid;
SELECT actionplanid FROM casesvc.casetype WHERE casetypeid = v_casetypeid INTO v_actionplanid;
SELECT questionset FROM casesvc.casetype WHERE casetypeid = v_casetypeid INTO v_questionset;
v_number_of_cases := 0;
v_sql_text := 'SELECT uprn FROM casesvc.address where ' || v_geog_select_text || ' and ' || v_address_criteria;


 FOR v_address_loop IN  EXECUTE v_sql_text LOOP

     v_caseid := nextval('casesvc.caseidseq') ;
     v_caseeventid := nextval('casesvc.caseeventidseq') ;

     p_caseid_out := v_caseid;
     p_actionplanid_out := v_actionplanid;

     RETURN NEXT;

--insert intitial record into case table
   
     INSERT INTO casesvc.case(caseid, uprn, state, casetypeid, createddatetime, createdby, sampleid, actionplanid, surveyid, questionset)
     Values ( --nextval('casesvc.caseidseq') 
     v_caseid 
    ,v_address_loop.uprn
    ,'INIT'
    ,v_casetypeid
    ,CURRENT_TIMESTAMP
    ,'SYSTEM'
    ,v_sampleid
    ,v_actionplanid
    ,v_surveyid
    ,v_questionset);

-- insert record into caseevent to say initial creation of case

     INSERT INTO casesvc.caseevent(
     caseeventid, caseid, description, createdby, createddatetime, 
     category)
     VALUES(v_caseeventid
    ,v_caseid      
    ,'Initial Creation Of Case'
    ,'SYSTEM'
    ,CURRENT_TIMESTAMP
    ,'CaseCreated');
   
-- insert into questionnaire table

     EXECUTE 'select casesvc.create_questionnaire(' || v_caseid  ||',''' || v_questionset || ''')';

     v_number_of_cases := v_number_of_cases + 1;
        
END LOOP;

    PERFORM casesvc.logmessage(p_messagetext := v_number_of_cases || ' cases generated for sampleid ' || v_sampleid || ' : Area Type ' || p_geog_area_type || ' : Area Code ' || p_geog_area_code
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.generate_cases');

RETURN;

EXCEPTION

  WHEN sqlstate  'Z0001' THEN
       PERFORM casesvc.logmessage(p_messagetext := 'EXCEPTION TRIGGERED ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                               ,p_jobid := 0   
                               ,p_messagelevel := 'WARNING'
                               ,p_functionname := 'casesvc.generate_cases'); 
  WHEN OTHERS THEN
    PERFORM casesvc.logmessage(p_messagetext := 'GENERATE CASES EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'casesvc.generate_cases');

   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION casesvc.generate_cases(integer, character varying, character varying)
  OWNER TO postgres;


