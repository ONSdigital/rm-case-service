-- Function: casesvc.generate_operational_mi_reports()

-- DROP FUNCTION casesvc.generate_operational_mi_reports();

CREATE OR REPLACE FUNCTION casesvc.generate_operational_mi_reports()
  RETURNS boolean AS
$BODY$
DECLARE
v_viewname      text[];
v_filename      text[];
v_headerline    text[];
v_loop          integer;
v_refresh_view  text;
v_contents      text;
r_dataline      record;
v_rows          integer;
BEGIN
    
    PERFORM casesvc.logmessage(p_messagetext := 'GENERATING OPERATIONAL MI REPORTS'
                              ,p_jobid := 0
                              ,p_messagelevel := 'INFO'
                              ,p_functionname := 'casesvc.generate_operational_mi_reports');

    v_viewname[1]  := 'mi_base_data'; -- Base data must be refreshed first as other views created from this view
    v_viewname[2]  := 'mi_hh_by_lad';
    v_viewname[3]  := 'mi_hh_by_sample';
    v_viewname[4]  := 'mi_hh_noreturn_metrics';
    v_viewname[5]  := 'mi_hh_return_metrics';
    v_viewname[6]  := 'mi_ce_return_metrics_hotel';
    v_viewname[7]  := 'mi_ce_return_metrics_uni';

    -- Report names
    v_filename[2]  := 'LA_RETURN_METRICS';
    v_filename[3]  := 'SAMPLE_RETURN_METRICS';
    v_filename[4]  := 'HH_NORETURN_METRICS';
    v_filename[5]  := 'HH_RETURN_METRICS';
    v_filename[6]  := 'CE_RETURN_METRICS_HOTEL';
    v_filename[7]  := 'CE_RETURN_METRICS_UNI';

    -- Header line
    v_headerline[2]:= 'lad,total,total_er,total_ner,total_returns,total_online_returns,total_paper_returns,total_multi_return';
    v_headerline[3]:= 'sample,total,total_er,total_ner,total_returns,total_online_returns,total_paper_returns,total_multi_return';
    v_headerline[4]:= 'total,total_ner,total_refused,total_incorrect_classification,total_address_not_found,total_demolished,total_derelict,total_duplicate,total_no_access,total_vacant_inc2nd_holidayhome,total_under_construction';

    v_headerline[5]:= 'total,total_er,total_ner,total_returns,total_online_returns,total_paper_returns,total_multi_return';
    v_headerline[6]:= v_headerline[5];
    v_headerline[7]:= v_headerline[5];

    -- Refresh all the Views
    FOR v_loop in 1 ..7 LOOP
       v_refresh_view := 'REFRESH MATERIALIZED VIEW casesvc.' || v_viewname[v_loop];  
       EXECUTE v_refresh_view; -- Refresh the views
    END LOOP;

    -- For each view create the 'contents' field
    FOR v_loop in 2 ..7 LOOP
       v_rows := 0;
       v_contents    := '';
       v_contents    := v_headerline[v_loop]; -- Set header line
       CASE v_loop WHEN 2 THEN 
          FOR r_dataline IN (SELECT * FROM casesvc.mi_hh_by_lad) LOOP
             v_contents := v_contents || chr(10) 
                                      || r_dataline.lad 
                                      || ',' || r_dataline.total  
                                      || ',' || r_dataline.total_er  
                                      || ',' || r_dataline.total_ner 
                                      || ',' || r_dataline.total_returns 
                                      || ',' || r_dataline.total_online_returns 
                                      || ',' || r_dataline.total_paper_returns
                                      || ',' || r_dataline.total_multi_return;                                     
             v_rows := v_rows+1;
          END LOOP;         
       WHEN 3 THEN 
          FOR r_dataline IN (SELECT * FROM casesvc.mi_hh_by_sample) LOOP
             v_contents := v_contents || chr(10) 
                                      || r_dataline.sample 
                                      || ',' || r_dataline.total
                                      || ',' || r_dataline.total_er 
                                      || ',' || r_dataline.total_ner 
                                      || ',' || r_dataline.total_returns 
                                      || ',' || r_dataline.total_online_returns 
                                      || ',' || r_dataline.total_paper_returns 
                                      || ',' || r_dataline.total_multi_return;
             v_rows := v_rows+1;
          END LOOP;          
       WHEN 4 THEN 
          FOR r_dataline IN (SELECT * FROM casesvc.mi_hh_noreturn_metrics) LOOP
             v_contents := v_contents || chr(10)
                                      || r_dataline.total
                                      || ',' || r_dataline.total_ner 
                                      || ',' || r_dataline.total_refused 
                                      || ',' || r_dataline.total_incorrect_classification  
                                      || ',' || r_dataline.total_address_not_found 
                                      || ',' || r_dataline.total_demolished 
                                      || ',' || r_dataline.total_derelict 
                                      || ',' || r_dataline.total_duplicate  
                                      || ',' || r_dataline.total_no_access 
                                      || ',' || r_dataline.total_vacant_inc2nd_holidayhome 
                                      || ',' || r_dataline.total_under_construction;
             v_rows := v_rows+1;
          END LOOP;
       WHEN 5 THEN 
          FOR r_dataline IN (SELECT * FROM casesvc.mi_hh_return_metrics) LOOP
             v_contents := v_contents || chr(10) 
                                      || r_dataline.total 
                                      || ',' || r_dataline.total_er 
                                      || ',' || r_dataline.total_ner
                                      || ',' || r_dataline.total_returns
                                      || ',' || r_dataline.total_online_returns
                                      || ',' || r_dataline.total_paper_returns 
                                      || ',' || r_dataline.total_multi_return;
             v_rows := v_rows+1;
          END LOOP;         
       WHEN 6 THEN 
          FOR r_dataline IN (SELECT * FROM casesvc.mi_ce_return_metrics_hotel) LOOP
             v_contents := v_contents || chr(10) 
                                      || r_dataline.total 
                                      || ',' || r_dataline.total_er
                                      || ',' || r_dataline.total_ner 
                                      || ',' || r_dataline.total_returns
                                      || ',' || r_dataline.total_online_returns
                                      || ',' || r_dataline.total_paper_returns 
                                      || ',' || r_dataline.total_multi_return;
             v_rows := v_rows+1;
          END LOOP;          
       WHEN 7 THEN 
          FOR r_dataline IN (SELECT * FROM casesvc.mi_ce_return_metrics_uni) LOOP
             v_contents := v_contents || chr(10)
                                      || r_dataline.total 
                                      || ',' || r_dataline.total_er 
                                      || ',' || r_dataline.total_ner
                                      || ',' || r_dataline.total_returns
                                      || ',' || r_dataline.total_online_returns 
                                      || ',' || r_dataline.total_paper_returns 
                                      || ',' || r_dataline.total_multi_return;
             v_rows := v_rows+1;
          END LOOP;                       
       END CASE;
       
       -- Insert the data into the reportrepository table
       INSERT INTO casesvc.reportrepository (reporttype, contents, createddatetime) VALUES(v_filename[v_loop], v_contents, CURRENT_TIMESTAMP); 
        
       PERFORM casesvc.logmessage(p_messagetext := 'OPERATIONAL MI REPORT ' || v_filename[v_loop] || ' COMPLETED ROWS WRIITEN = ' || v_rows
                              ,p_jobid := 0
                              ,p_messagelevel := 'INFO'
                              ,p_functionname := 'casesvc.generate_operational_mi_reports');    

    END LOOP;
    
    PERFORM casesvc.logmessage(p_messagetext := 'OPERATIONAL MI REPORTS GENERATED'
                              ,p_jobid := 0
                              ,p_messagelevel := 'INFO'
                              ,p_functionname := 'casesvc.generate_operational_mi_reports'); 
  RETURN TRUE;

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
