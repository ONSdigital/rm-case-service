-- Function: casesvc.generate_initial_cases(integer, character varying, character varying)

-- DROP FUNCTION casesvc.generate_initial_cases(integer, character varying, character varying);

CREATE OR REPLACE FUNCTION casesvc.generate_initial_cases(
    p_sampleid integer,
    p_geog_area_type character varying,
    p_geog_area_code character varying)
  RETURNS boolean AS
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
    v_sql_text character varying;
    v_caseeventid bigint;
    v_casegroupid bigint;
    v_actionplanmappingid integer;
    v_cases_already_generated integer;
    v_number_of_cases integer;
    v_survey character varying(20);
    v_sample character varying(20);
   

BEGIN

--check that have a valid area type passed in

IF p_geog_area_type NOT IN ('MSOA','LA','REGION')
THEN
 RAISE SQLSTATE 'Z0001' using message = p_geog_area_type ||' is not a valid geographic area type. Must be of type MSOA , LA or REGION' ;
END IF;

--When the address criteria is fincalised for 2017 then this should be tidied up

 --SELECT addresscriteria FROM casesvc.sample WHERE sampleid = p_sampleid INTO v_address_criteria;

 SELECT name FROM casesvc.sample WHERE sampleid = p_sampleid INTO v_sample;
 
           SELECT CASE p_geog_area_type
           WHEN 'OA' THEN   'oa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LSOA' THEN  'lsoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'MSOA' THEN 'msoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LA' THEN 'lad12cd = ''' || p_geog_area_code ||''''
           WHEN 'REGION' THEN 'region11cd = ''' || p_geog_area_code || ''''
          ELSE '0=1' --not a valid area type
       END INTO v_geog_select_text ;


--assign into variables for insert statement


v_sampleid := p_sampleid;

SELECT survey FROM casesvc.sample WHERE sampleid = v_sampleid INTO v_survey;

SELECT name FROM casesvc.sample WHERE sampleid = v_sampleid INTO v_sample;
select casetypeid
from casesvc.samplecasetypeselector
where sampleid = v_sampleid
and respondenttype = 'H' into v_casetypeid;

select actionplanmappingid
from casesvc.actionplanmapping
where casetypeid = v_casetypeid
and isdefault = TRUE
into v_actionplanmappingid;


v_sql_text := 'SELECT uprn FROM casesvc.address where ' || v_geog_select_text || ' and sample = ''' || v_sample || '''';

 FOR v_address_loop IN  EXECUTE v_sql_text LOOP
     v_caseeventid := nextval('casesvc.caseeventidseq') ;
     v_casegroupid := nextval('casesvc.casegroupidseq') ;
     

--insert intitial record into casegroup  and case table
   
     INSERT INTO casesvc.casegroup(casegroupid, uprn, sampleid)
     Values (
     v_casegroupid 
    ,v_address_loop.uprn
    ,v_sampleid);


    INSERT INTO casesvc.case(casegroupid, state, casetypeid, actionplanmappingid, createddatetime, createdby)
     Values ( 
     v_casegroupid
    ,'SAMPLED_INIT'
    ,v_casetypeid
    ,v_actionplanmappingid
    ,CURRENT_TIMESTAMP
    ,'SYSTEM');

 --    v_number_of_cases := v_number_of_cases + 1;

  INSERT INTO casesvc.caseevent(
     caseeventid, caseid, description, createdby, createddatetime, category)
     VALUES(v_caseeventid
    ,currval('casesvc.caseidseq')   
    ,'Initial Creation Of Case'
    ,'SYSTEM'
    ,CURRENT_TIMESTAMP
    ,'CASE_CREATED');

        
END LOOP;

    PERFORM casesvc.logmessage(p_messagetext := v_number_of_cases || ' cases generated for sampleid ' || v_sampleid || ' : Area Type ' || p_geog_area_type || ' : Area Code ' || p_geog_area_code
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.generate_cases');

RETURN TRUE;

EXCEPTION

  WHEN sqlstate  'Z0001' THEN
       PERFORM casesvc.logmessage(p_messagetext := 'EXCEPTION TRIGGERED ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                               ,p_jobid := 0   
                               ,p_messagelevel := 'WARNING'
                               ,p_functionname := 'casesvc.generate_cases'); 
RETURN FALSE;
                               
  WHEN OTHERS THEN
    PERFORM casesvc.logmessage(p_messagetext := 'GENERATE CASES EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'casesvc.generate_cases');

                             
RETURN FALSE;
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION casesvc.generate_initial_cases(integer, character varying, character varying)
  OWNER TO postgres;

