-- Function: casesvc.insert_household_report_into_report()

-- DROP FUNCTION casesvc.insert_household_report_into_report();

CREATE OR REPLACE FUNCTION casesvc.insert_household_report_into_report()
  RETURNS boolean AS
$BODY$
DECLARE
v_contents_text text;
v_current_record_text text;
r record;
v_reporttype character varying;
v_createddatetime timestamp with time zone;
v_record_count integer;

BEGIN

PERFORM casesvc.logmessage(p_messagetext := 'insert_household_report_into_report started'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.insert_household_report_into_report');

v_reporttype := 'HH_OUTSTANDING_CASES';
v_createddatetime := current_timestamp;
v_record_count := 0;

--build up header
v_contents_text := 'caseref,organisation_name,address_line1,address_line2,locality,town_name,postcode,questionset' || chr(10);

--select records to be used
FOR r IN SELECT  c.caseref, a.organisation_name, a.address_line1, a.address_line2, a.locality, a.town_name, a.postcode, 
CASE when sample like '%S%E%' then 'H1S' 
WHEN a.sample like '%E' THEN 'H1'
WHEN a.sample like '%S%W' THEN 'H2S'
WHEN a.sample like '%W' then 'H2'  end questionset
FROM casesvc.case c
,casesvc.casegroup cg
,casesvc.address a
WHERE c.state = 'ACTIONABLE'
AND a.addresstype = 'HH'
AND c.casegroupid = cg.casegroupid
AND cg.uprn = a.uprn
AND a.sample like 'C1%'
ORDER BY a.postcode
--build up text field for contents
LOOP
v_current_record_text := COALESCE(r.caseref,'') || ',' ||  COALESCE(r.organisation_name,'') || ',' 
                   ||  COALESCE(r.address_line1,'') || ',' ||  COALESCE(r.address_line2,'') || ','  
                   ||  COALESCE(r.locality,'') || ',' ||  COALESCE(r.town_name,'') || ',' 
                   ||  COALESCE(r.postcode,'') || ',' || COALESCE(r.questionset,'') ||chr(10);
v_contents_text := v_contents_text || v_current_record_text;
v_record_count := v_record_count + 1;

END LOOP;

--insert into report

INSERT INTO casesvc.report (reporttype,  contents, createddatetime ) VALUES(v_reporttype,  v_contents_text, v_createddatetime );


PERFORM casesvc.logmessage(p_messagetext := 'insert_household_report_into_report completed:' || v_record_count || ' household address records generated'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.insert_household_report_into_report');

RETURN TRUE;

EXCEPTION

  WHEN OTHERS THEN
    PERFORM casesvc.logmessage(p_messagetext := 'insert_household_report_into_report EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'casesvc.insert_household_report_into_report');
RETURN FALSE;
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION casesvc.insert_household_report_into_report()
  OWNER TO casesvc;

