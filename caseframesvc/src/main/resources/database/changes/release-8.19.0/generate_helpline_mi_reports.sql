-- Function: caseframe.generate_helpline_mi_reports()

-- DROP FUNCTION caseframe.generate_helpline_mi_reports();

CREATE OR REPLACE FUNCTION caseframe.generate_helpline_mi_reports()
  RETURNS boolean AS
$BODY$
DECLARE
v_sql_text text;
v_view_query text;
v_filename text;
v_directory text;
v_filedatestamp text;

BEGIN

 refresh materialized view caseframe.helpline_mi;

 v_directory := '/var/helpline-mi/' ;

 v_filedatestamp := to_char(current_timestamp, 'DDMMYYYY' );

--hourly

  v_view_query := '(SELECT to_char(helpline_mi.createddatetime,''DD-MM-YYYY HH24:00'' ) as Hour,
                   count(1) as Call_Total
                   FROM caseframe.helpline_mi 
                   WHERE role like ''%cso%''
                   AND subcategory is null
                   AND UPPER(category) <> ''REFUSAL''
                   GROUP BY hour)';

   
 v_filename := v_directory || 'hl_calls_hour_' || v_filedatestamp || '.csv' ;

 v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

 EXECUTE v_sql_text;

--daily

 v_view_query := '(SELECT to_char(helpline_mi.createddatetime, ''DD-MM-YYYY'') as Day,
		   count(1) as Call_Total
		   FROM caseframe.helpline_mi
	           WHERE role like ''%cso%''
	           AND subcategory is null
	           AND UPPER(category) <> ''REFUSAL''
                   GROUP BY day)';
   
 v_filename := v_directory ||'hl_calls_day_'|| v_filedatestamp || '.csv' ;

 v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

 EXECUTE v_sql_text;

--weekly

 v_view_query := '(SELECT to_char(date_trunc(''week''::text, helpline_mi.createddatetime),''DD-MM-YYYY'') as Week,
                   count(1) as Call_Total
                   FROM caseframe.helpline_mi
                   WHERE role like ''%cso%''
                   AND subcategory is null
                   AND UPPER(category) <> ''REFUSAL''
                   GROUP BY week)';
   
 v_filename := v_directory || 'hl_calls_week_' || v_filedatestamp || '.csv' ;

 v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

 EXECUTE v_sql_text;


--Calls resolved without escalation

 v_view_query := '(SELECT category, count(*) as Call_Total 
                   FROM caseframe.helpline_mi
                   WHERE role like ''%cso%''
                   AND subcategory is null
                   AND UPPER(category) not like ''%ESCALATED%''
                   AND UPPER(category) not like ''%REFUSAL%''
                   GROUP BY category
                   ORDER BY 1)';

  v_filename := v_directory || 'hl_calls_not_escalated_' || v_filedatestamp || '.csv' ;

  v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

  EXECUTE v_sql_text;

--escalated calls breakdown

  v_view_query := '(SELECT category, count(*) as Call_Total FROM caseframe.helpline_mi
                    WHERE role like ''%cso%''
                    AND subcategory is null
                    AND UPPER(category) like ''%ESCALATED%''
                    GROUP BY category
                    ORDER BY 1)';

  v_filename := v_directory || 'hl_calls_escalated_' || v_filedatestamp || '.csv' ;

  v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

  EXECUTE v_sql_text;

--escalated calls resolution

  v_view_query := '(SELECT category, count(*) as Call_Total FROM caseframe.helpline_mi 
                    WHERE role like ''%esc%''
                    AND UPPER(category) <> ''REFUSAL''
                    GROUP BY category
                    ORDER BY 1)';

  v_filename := v_directory || 'hl_escalated_outcomes_' || v_filedatestamp || '.csv' ;

  v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

  EXECUTE v_sql_text;

--refused calls details

  v_view_query := '(SELECT DESCRIPTION, CREATEDBY, TO_CHAR(CREATEDDATETIME, ''DD-MM-YYYY HH24:MM:SS'') as time_logged 
	            FROM CASEFRAME.HELPLINE_MI 
	            WHERE UPPER(CATEGORY) = ''REFUSAL''
		    ORDER BY time_logged)';

  v_filename := v_directory || 'hl_refusal_details_' || v_filedatestamp || '.csv' ;

  v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

  EXECUTE v_sql_text;
  
  RETURN true;

  EXCEPTION
  WHEN OTHERS THEN
   PERFORM caseframe.logmessage(p_messagetext := 'generate_helpline_mi_reports' || v_sql_text
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'caseframe.generate_helpline_mi_reports');
  return false;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
ALTER FUNCTION caseframe.generate_helpline_mi_reports()
  OWNER TO postgres;
