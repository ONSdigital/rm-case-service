-- Function: casesvc.generate_operational_mi_reports()

-- DROP FUNCTION casesvc.generate_operational_mi_reports();


CREATE OR REPLACE FUNCTION casesvc.generate_operational_mi_reports()
  RETURNS boolean AS
$BODY$
DECLARE
v_directory     text;
v_filename      text[];
v_loop          integer;
v_total_files   integer;
v_refresh_view  text;

BEGIN

    v_directory := '/var/operational-mi/' ;
    --v_directory   := 'd:/sarah/';
    v_total_files := 7;
 
    v_filename[1]  := 'casesvc.mi_base_data'; -- base data must be refreshed first as other views created from this view
    v_filename[2]  := 'casesvc.mi_hh_by_lad';
    v_filename[3]  := 'casesvc.mi_hh_by_sample';
    v_filename[4]  := 'casesvc.mi_hh_noreturn_metrics';
    v_filename[5]  := 'casesvc.mi_hh_return_metrics';
    v_filename[6]  := 'casesvc.mi_ce_return_metrics_hotel';
    v_filename[7]  := 'casesvc.mi_ce_return_metrics_uni';

    FOR v_loop in 1 ..v_total_files LOOP
       v_refresh_view := 'refresh materialized view ' || v_filename[v_loop];  
       EXECUTE v_refresh_view; -- Refresh the views
       PERFORM casesvc.copy_csv(p_sql := 'SELECT * FROM ' || v_filename[v_loop]
                               ,p_directory := v_directory
                               ,p_filename  := v_filename[v_loop] || '_' ||to_char(current_timestamp, 'DDMMYYYY')); -- copy data to CSV
    END LOOP;

  RETURN true;

  EXCEPTION
  WHEN OTHERS THEN
   
     PERFORM casesvc.logmessage(p_messagetext := 'GENERATE OPERATIONAL MI EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                               ,p_jobid := 0
                               ,p_messagelevel := 'FATAL'
                               ,p_functionname := 'casesvc.generate_operational_mi_reports');
                               
  RETURN FALSE;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;