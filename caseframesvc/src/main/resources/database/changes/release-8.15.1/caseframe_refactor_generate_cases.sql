--generate-cases

-- Function: caseframe.generate_cases(integer, character varying, character varying)

-- DROP FUNCTION caseframe.generate_cases(integer, character varying, character varying);

CREATE OR REPLACE FUNCTION caseframe.generate_cases(p_sampleid integer, p_geog_area_type character varying, p_geog_area_code character varying)
  RETURNS boolean AS
$BODY$
DECLARE
v_addresscriteria character varying(100);
v_sql_text text;
v_geog_select_text text;
v_rowcount integer;

BEGIN
           SELECT CASE p_geog_area_type
           WHEN 'OA' THEN   'oa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LSOA' THEN  'lsoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'MSOA' THEN 'msoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LA' THEN 'lad12cd = ''' || p_geog_area_code ||''''
           WHEN 'REGION' THEN 'region11cd = ''' || p_geog_area_code || ''''
          ELSE '0=1' --not a valid area type
       END INTO v_geog_select_text ;


 select addresscriteria from caseframe.sample where sampleid = p_sampleid into v_addresscriteria;

IF v_geog_select_text = '0=1' THEN
   PERFORM caseframe.logmessage(p_messagetext := '*** 0 CASES CREATED - INVALID Geography Area Type: ' || p_geog_area_type || ' *** SQL NOT created for Generate Cases for Sampleid: ' || p_sampleid || ', Geography Area Type: ' || p_geog_area_type 
                               || ', Geography Area Code: ' || p_geog_area_code || ', Address Criteria: ' || v_addresscriteria
                               ,p_jobid := 0   
                               ,p_messagelevel := 'ERROR'
                               ,p_functionname := 'caseframe.generate_cases');

ELSE


   v_sql_text := 'INSERT INTO caseframe.case(caseid, uprn, status, casetypeid, createddatetime, createdby, sampleid, actionplanid, surveyid, questionset)
   select nextval(''caseframe.caseidseq'')
   ,a.uprn
   ,''INIT''
   ,s.casetypeid
   ,CURRENT_TIMESTAMP
   ,''SYSTEM''
   ,s.sampleid
   ,ct.actionplanid
   ,s.surveyid
   ,ct.questionset
   from caseframe.sample s
  ,caseframe.casetype ct
  ,caseframe.address a where ' || v_geog_select_text  ||' and s.sampleid = ' || p_sampleid || ' and s.casetypeid = ct.casetypeid and ' || v_addresscriteria
   || 'and a.uprn NOT IN (SELECT uprn from caseframe.case )';


   PERFORM caseframe.logmessage(p_messagetext := 'SQL created for Generate Cases for Sampleid: ' || p_sampleid || ', Geography Area Type: ' || p_geog_area_type 
                               || ', Geography Area Code: ' || p_geog_area_code || ', Address Criteria: ' || v_addresscriteria
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'caseframe.generate_cases');

   EXECUTE v_sql_text;

   GET DIAGNOSTICS v_rowcount = ROW_COUNT;  

   PERFORM caseframe.logmessage(p_messagetext := v_rowcount  || ' NEW CASES generated for Sampleid: ' || p_sampleid || ', Geography Area Type: ' || p_geog_area_type 
                                || ', Geography Area Code: ' || p_geog_area_code || ', Address Criteria: ' || v_addresscriteria
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'caseframe.generate_cases');

   --create questionnaires for each case
  PERFORM caseframe.create_questionnaires();
END IF;


  RETURN TRUE;

  EXCEPTION

 WHEN OTHERS THEN
    PERFORM caseframe.logmessage(p_messagetext := 'GENERATE CASES EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.generate_cases');
  RETURN FALSE;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.generate_cases(integer, character varying, character varying)
  OWNER TO postgres;
