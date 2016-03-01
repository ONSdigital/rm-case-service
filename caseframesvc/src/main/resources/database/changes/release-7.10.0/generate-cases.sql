CREATE OR REPLACE FUNCTION caseframe.generate_cases(p_sampleid integer, p_geog_area_type character varying, p_geog_area_code character varying)
  RETURNS boolean AS
$BODY$
DECLARE
v_addresscriteria character varying(100);
v_sql_text text;
v_geog_select_text text;

BEGIN

           SELECT CASE p_geog_area_type
           WHEN 'OA' THEN   'oa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LSOA' THEN  'lsoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'MSOA' THEN 'msoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LA' THEN 'lad12cd = ''' || p_geog_area_code ||''''
           WHEN 'REGION' THEN 'region11cd = ''' || p_geog_area_code || ''''
          ELSE '0=1' --not a valid area type
       END INTO v_geog_select_text ;


 select address_criteria from caseframe.sample where sampleid = p_sampleid into v_addresscriteria;

v_sql_text := 'INSERT INTO caseframe.case(caseid, uprn, case_status, casetypeid, created_datetime, created_by, sampleid, actionplanid, surveyid, questionset)
select nextval(''caseframe.caseid_seq'')
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
,caseframe.address a
where
 ' || v_geog_select_text  ||' and s.sampleid = ' || p_sampleid || ' and s.casetypeid = ct.casetypeid
and ' || v_addresscriteria
|| 'and a.uprn NOT IN (SELECT uprn from caseframe.case )';

EXECUTE v_sql_text;

--create questionnaires for each case
PERFORM caseframe.create_questionnaires();

  RETURN TRUE;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.generate_cases(integer, character varying, character varying)
  OWNER TO postgres;
