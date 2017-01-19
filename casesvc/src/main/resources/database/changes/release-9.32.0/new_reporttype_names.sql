-- Function: casesvc.insert_shousing_report_into_reportrepository()

-- DROP FUNCTION casesvc.insert_shousing_report_into_reportrepository();

CREATE OR REPLACE FUNCTION casesvc.insert_shousing_report_into_reportrepository()
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

PERFORM casesvc.logmessage(p_messagetext := 'insert_shousing_report_into_reportrepository started'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.insert_household_report_into_reportrepository');

v_reporttype := 'SH_OUTSTANDING_CASES';
v_createddatetime := current_timestamp;
v_record_count := 0;

--build up header
v_contents_text := 'caseref,organisation_name,address_line1,address_line2,locality,town_name,postcode,questionset' || chr(10);

--select records to be used

FOR r IN SELECT  c.caseref, a.organisation_name, a.address_line1, a.address_line2, a.locality, a.town_name, a.postcode, 
CASE WHEN a.sample = 'SHOUSING' THEN 'H1' else  '' end questionset
FROM casesvc.case c
,casesvc.casegroup cg
,casesvc.address a
WHERE c.state = 'ACTIONABLE'
AND c.casegroupid = cg.casegroupid
AND cg.uprn = a.uprn
AND a.sample = 'SHOUSING'
ORDER BY a.postcode
--build up text field for contents
LOOP
v_current_record_text := COALESCE(r.caseref,'') || ',' ||  COALESCE(r.organisation_name,'') || ',' 
                   ||  COALESCE(r.address_line1,'') || ',' ||  COALESCE(r.address_line2,'') || ','  
                   ||  COALESCE(r.locality,'') || ',' ||  COALESCE(r.town_name,'') || ',' 
                   ||  COALESCE(r.postcode,'') || ',' || COALESCE(r.questionset,'')  ||chr(10);
v_contents_text := v_contents_text || v_current_record_text;
v_record_count := v_record_count + 1;

END LOOP;

--insert into reportrepository

INSERT INTO casesvc.reportrepository (reporttype,  contents, createddatetime ) VALUES(v_reporttype,  v_contents_text, v_createddatetime );


PERFORM casesvc.logmessage(p_messagetext := 'insert_shousing_report_into_reportrepository completed:' || v_record_count || ' sheltered housing address records generated'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.insert_shousing_report_into_reportrepository');

RETURN TRUE;

EXCEPTION

  WHEN OTHERS THEN
    PERFORM casesvc.logmessage(p_messagetext := 'insert_shousing_report_into_reportrepository EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'casesvc.insert_shousing_report_into_reportrepository');
RETURN FALSE;
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: casesvc.insert_university_report_into_reportrepository()

-- DROP FUNCTION casesvc.insert_university_report_into_reportrepository();

CREATE OR REPLACE FUNCTION casesvc.insert_university_report_into_reportrepository()
  RETURNS boolean AS
$BODY$
DECLARE
v_contents_text text;
v_current_record_text text;
r record;
v_reporttype character varying;
v_createddate date;
v_createddatetime timestamp with time zone;
v_record_count integer;

BEGIN

PERFORM casesvc.logmessage(p_messagetext := 'insert_university_report_into_reportrepository started'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.insert_university_report_into_reportrepository');

v_reporttype := 'CE_OUTSTANDING_CASES';
v_createddatetime := current_timestamp;
v_record_count := 0;
--build up header
v_contents_text := 'caseref,organisation_name,address_line1,address_line2,locality,town_name,postcode' || chr(10);

--select records to be used
FOR r IN SELECT  c.caseref, a.organisation_name, a.address_line1, a.address_line2, a.locality, a.town_name, a.postcode
FROM casesvc.case c
,casesvc.casegroup cg
,casesvc.address a
WHERE c.state = 'ACTIONABLE'
AND a.sample = 'UNIVERSITY'
AND c.casegroupid = cg.casegroupid
AND cg.uprn = a.uprn
ORDER BY a.postcode
--build up text field for contents
LOOP
v_current_record_text := COALESCE(r.caseref,'') || ',' ||  COALESCE(r.organisation_name,'') || ',' 
                   ||  COALESCE(r.address_line1,'') || ',' ||  COALESCE(r.address_line2,'') || ','  
                   ||  COALESCE(r.locality,'') || ',' ||  COALESCE(r.town_name,'') || ',' 
                   ||  COALESCE(r.postcode,'') || chr(10);
v_contents_text := v_contents_text || v_current_record_text;
v_record_count := v_record_count + 1;

END LOOP;

--insert into reportrepository


INSERT INTO casesvc.reportrepository (reporttype,  contents, createddatetime ) VALUES(v_reporttype,  v_contents_text, v_createddatetime );


PERFORM casesvc.logmessage(p_messagetext := 'insert_university_report_into_reportrepository completed.'|| v_record_count ||' university addresses generated'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.insert_university_report_into_reportrepository');

RETURN TRUE;

EXCEPTION

  WHEN OTHERS THEN
    PERFORM casesvc.logmessage(p_messagetext := 'insert_university_report_into_reportrepository EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'casesvc.insert_university_report_into_reportrepository');
RETURN FALSE;
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: casesvc.insert_household_report_into_reportrepository()

-- DROP FUNCTION casesvc.insert_household_report_into_reportrepository();

CREATE OR REPLACE FUNCTION casesvc.insert_household_report_into_reportrepository()
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

PERFORM casesvc.logmessage(p_messagetext := 'insert_household_report_into_reportrepository started'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.insert_household_report_into_reportrepository');

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

--insert into reportrepository

INSERT INTO casesvc.reportrepository (reporttype,  contents, createddatetime ) VALUES(v_reporttype,  v_contents_text, v_createddatetime );


PERFORM casesvc.logmessage(p_messagetext := 'insert_household_report_into_reportrepository completed:' || v_record_count || ' household address records generated'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.insert_household_report_into_reportrepository');

RETURN TRUE;

EXCEPTION

  WHEN OTHERS THEN
    PERFORM casesvc.logmessage(p_messagetext := 'insert_household_report_into_reportrepository EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'casesvc.insert_household_report_into_reportrepository');
RETURN FALSE;
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: casesvc.insert_helpline_report_into_reportrepository()

-- DROP FUNCTION casesvc.insert_helpline_report_into_reportrepository();

CREATE OR REPLACE FUNCTION casesvc.insert_helpline_report_into_reportrepository()
  RETURNS boolean AS
$BODY$
DECLARE

v_contents_text text;
v_current_record_text text;
r record;
v_reporttype character varying;
v_createddate date;
v_createddatetime timestamp with time zone;

BEGIN

PERFORM casesvc.logmessage(p_messagetext := 'insert_helpline_report_into_reportrepository started '
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'casesvc.insert_helpline_report_into_reportrepository');
                               


v_reporttype := 'HL_METRICS';
v_createddate := current_date;
v_createddatetime := current_timestamp;

REFRESH MATERIALIZED VIEW casesvc.helpline_mi_daily_calls;

--build up header

v_contents_text := 'Count_type, Count ' || chr(10);

--calculate total calls
FOR r IN SELECT 'Total Calls Received' col1, count(*) col2 FROM casesvc.helpline_mi_daily_calls
WHERE UPPER(role) LIKE '%COLLECT-CSOS%'
--build up text field for contents
LOOP
v_current_record_text := r.col1::text || ',' || r.col2::text ||chr(10);
v_contents_text := v_contents_text || v_current_record_text;

END LOOP;

--select records to be used for hourly calls
FOR r IN WITH hourly_calls AS
(SELECT * FROM generate_series((date_trunc('day',current_timestamp)) + interval '9 hours',
                              (date_trunc('day',current_timestamp)) + interval '20 hours' , '1 hours') hour_time)                              
                              SELECT 'Total calls received ' || to_char(hour_time, 'HH24') || '-' || to_char(hour_time + interval '1 hour', 'HH24'   ) col1  , count(to_char(a.createddatetime, 'HH24')) col2
                              FROM hourly_calls hc                         
                              LEFT OUTER JOIN casesvc.helpline_mi_daily_calls a
                              on to_char(hour_time, 'HH24') = to_char(a.createddatetime, 'HH24')
                              AND UPPER(a.role) LIKE '%COLLECT-CSOS%'
                              GROUP BY hc.hour_time 
                              ORDER BY 1
--build up text field for contents
LOOP
v_current_record_text := r.col1::text || ',' || r.col2::text ||chr(10);
v_contents_text := v_contents_text || v_current_record_text;

END LOOP;

--select records to be used for calls by category
FOR r IN WITH valid_categories AS
(SELECT name category_name,
longdescription
FROM casesvc.category
WHERE manual = TRUE
AND name <> 'UNDELIVERABLE')
SELECT 'Total ' || vc.longdescription || ' Calls' col1, count(a.category) col2
FROM valid_categories vc
LEFT OUTER JOIN  casesvc.helpline_mi_daily_calls a
ON vc.category_name = a.category
GROUP BY vc.category_name, vc.longdescription
ORDER BY vc.category_name
--build up text field for contents
LOOP
v_current_record_text := r.col1::text || ',' || r.col2::text ||chr(10);
v_contents_text := v_contents_text || v_current_record_text;

END LOOP;

--insert into reportrepository

INSERT INTO casesvc.reportrepository (reporttype,  contents, createddatetime ) VALUES(v_reporttype,  v_contents_text, v_createddatetime );


   PERFORM casesvc.logmessage(p_messagetext := 'insert_helpline_report_into_reportrepository completed '
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'casesvc.insert_helpline_report_into_reportrepository');
  RETURN TRUE;

  EXCEPTION
  WHEN OTHERS THEN
   PERFORM casesvc.logmessage(p_messagetext := 'insert_helpline_report_into_reportrepository' || SQLERRM
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'casesvc.insert_helpline_report_into_reportrepository');
  RETURN FALSE;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;



