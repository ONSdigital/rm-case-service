-- Function: casesvc.generate_helpline_mi_daily_report()

-- DROP FUNCTION casesvc.generate_helpline_mi_daily_report();

CREATE OR REPLACE FUNCTION casesvc.generate_helpline_mi_daily_report()
  RETURNS boolean AS
$BODY$
DECLARE
v_directory text;
v_report_query text;
v_filename text;

BEGIN

PERFORM casesvc.logmessage(p_messagetext := 'generate_helpline_mi_daily_report started '
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'casesvc.generate_helpline_mi_daily_report');
                               
REFRESH MATERIALIZED VIEW casesvc.helpline_mi_daily_calls;

v_directory := '/var/helpline-mi/' ;
 
v_filename := 'hl_daily_report_' ||  to_char(current_timestamp, 'DDMMYYYY' );

v_report_query :='(SELECT ''Total Calls Received'', count(*) FROM casesvc.helpline_mi_daily_calls
WHERE UPPER(role) LIKE ''%COLLECT-CSOS%'')
UNION ALL
(WITH hourly_calls AS
(SELECT * FROM generate_series((date_trunc(''day'',current_timestamp)) + interval ''9 hours'',
                              (date_trunc(''day'',current_timestamp)) + interval ''20 hours'' , ''1 hours'') hour_time)                              
                              SELECT ''Total calls received '' || to_char(hour_time, ''HH24'') || ''-'' || to_char(hour_time + interval ''1 hour'', ''HH24''   )   , count(to_char(a.createddatetime, ''HH24''))
                              FROM hourly_calls hc                         
                              LEFT OUTER JOIN casesvc.helpline_mi_daily_calls a
                              on to_char(hour_time, ''HH24'') = to_char(a.createddatetime, ''HH24'')
                              AND UPPER(a.role) LIKE ''%COLLECT-CSOS%''
                              GROUP BY hc.hour_time 
                              ORDER BY 1)
UNION ALL
(WITH valid_categories AS
(SELECT name category_name,
longdescription
FROM casesvc.category
WHERE manual = TRUE
AND name <> ''UNDELIVERABLE'')
SELECT ''Total '' || vc.longdescription || '' Calls'', count(a.category)
FROM valid_categories vc
LEFT OUTER JOIN  casesvc.helpline_mi_daily_calls a
ON vc.category_name = a.category
GROUP BY vc.category_name, vc.longdescription
ORDER BY vc.category_name)';

PERFORM casesvc.copy_csv(p_sql := v_report_query
			,p_directory := v_directory
			,p_filename := v_filename
		        ,p_header := '');

   PERFORM casesvc.logmessage(p_messagetext := 'generate_helpline_mi_daily_report completed '
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'casesvc.generate_helpline_mi_daily_report');
  RETURN TRUE;

  EXCEPTION
  WHEN OTHERS THEN
   PERFORM casesvc.logmessage(p_messagetext := 'generate_helpline_mi_daily_report' || SQLERRM
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'casesvc.generate_helpline_mi_daily_report');
  RETURN FALSE;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
