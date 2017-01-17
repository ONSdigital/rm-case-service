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

--call function to insert into report repository table

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
ALTER FUNCTION casesvc.insert_helpline_report_into_reportrepository()
  OWNER TO casesvc;
