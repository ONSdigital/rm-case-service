-- Function: casesvc.generate_case_events_report()

-- DROP FUNCTION casesvc.generate_case_events_report();

CREATE OR REPLACE FUNCTION casesvc.generate_case_events_report()
  RETURNS boolean AS
$BODY$
DECLARE

v_contents      text;
r_dataline      record;
v_rows          integer;

BEGIN
   
   PERFORM casesvc.logmessage(p_messagetext := 'GENERATING CASE EVENTS REPORT'
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.generate_case_events_report');  
   
      v_rows := 0;
      v_contents    := '';
      v_contents    := 'Sample Unit Ref,Sample Unit Type,Case Ref,Case Created,Action Created,Action Completed,Respondent Account Created,Respondent Enrolled,Access Code Authentication Attempt,Collection Instrument Downloaded,Unsuccessful Response Upload,Successful Response Upload,Offline Response Processed'; -- Set header line    

      FOR r_dataline IN (SELECT
  events.sampleunitref 
, events.sampleunittype
, events.caseref
, events.case_created
, events.action_created
--, events.action_cancellation_created
--, events.action_cancellation_completed
, events.action_completed
--, events.action_updated     
, events.respondent_account_created                                                                                           
, events.respondent_enroled         
, events.access_code_authentication_attempt_ind 
, events.collection_instrument_downloaded_ind 
, events.unsuccessful_response_upload_ind  
, events.successful_response_upload_ind 
, events.offline_response_processed_ind   
  
FROM 
(SELECT 
    cg.sampleunitref
  , c.sampleunittype 
  , c.caseref
  -- response chasing categories
  , MAX(CASE WHEN ce.categoryFK = 'ACCESS_CODE_AUTHENTICATION_ATTEMPT'  THEN 1 ELSE  0 END) access_code_authentication_attempt_ind 	--(B)  -- count distinct event
  , SUM(CASE WHEN ce.categoryFK = 'RESPONDENT_ACCOUNT_CREATED' 		THEN 1 ELSE  0 END) respondent_account_created 			--(B)  -- count all events
  , SUM(CASE WHEN ce.categoryFK = 'RESPONDENT_ENROLED' 			THEN 1 ELSE  0 END) respondent_enroled 				--(B)  -- count all events
  , MAX(CASE WHEN ce.categoryFK = 'COLLECTION_INSTRUMENT_DOWNLOADED'    THEN 1 ELSE  0 END) collection_instrument_downloaded_ind	--(BI) -- count distinct event
  , MAX(CASE WHEN ce.categoryFK = 'SUCCESSFUL_RESPONSE_UPLOAD' 		THEN 1 ELSE  0 END) successful_response_upload_ind		--(BI) -- count distinct event
  -- remaining categories
  , SUM(CASE WHEN ce.categoryFK = 'CASE_CREATED'                        THEN 1 ELSE  0 END) case_created 				--(B,BI) -- count all event
  , SUM(CASE WHEN ce.categoryFK = 'ACTION_CREATED'                      THEN 1 ELSE  0 END) action_created  				--(B,BI) -- count all events
  , SUM(CASE WHEN ce.categoryFK = 'ACTION_CANCELLATION_COMPLETED' 	THEN 1 ELSE  0 END) action_cancellation_completed 		--(B,BI) -- count all events
  , SUM(CASE WHEN ce.categoryFK = 'ACTION_CANCELLATION_CREATED' 	THEN 1 ELSE  0 END) action_cancellation_created 		--(B,BI) -- count all events
  , SUM(CASE WHEN ce.categoryFK = 'ACTION_COMPLETED' 			THEN 1 ELSE  0 END) action_completed 				--(B,BI) -- count all events
  , SUM(CASE WHEN ce.categoryFK = 'ACTION_UPDATED' 			THEN 1 ELSE  0 END) action_updated 				--(B,BI) -- count all events  
  , MAX(CASE WHEN ce.categoryFK = 'OFFLINE_RESPONSE_PROCESSED' 		THEN 1 ELSE  0 END) offline_response_processed_ind		--(BI)	 -- count distinct event
  , MAX(CASE WHEN ce.categoryFK = 'UNSUCCESSFUL_RESPONSE_UPLOAD' 	THEN 1 ELSE  0 END) unsuccessful_response_upload_ind 		--(BI)   -- count distinct event   
FROM   casesvc.caseevent ce
RIGHT OUTER JOIN casesvc.case c  ON c.casePK = ce.caseFK 
INNER JOIN casesvc.casegroup cg  ON c.casegroupFK = cg.casegroupPK
GROUP BY cg.sampleunitref
       , c.sampleunittype
       , c.casePK) events
ORDER BY events.sampleunitref
       , events.sampleunittype
       , events.caseref) LOOP
            v_contents := v_contents || chr(10) || r_dataline.sampleunitref || ',' || r_dataline.sampleunittype || ',' 
            || r_dataline.caseref || ',' 
            || r_dataline.case_created ||',' 
            || r_dataline.action_created ||',' 
            || r_dataline.action_completed ||',' 
            || r_dataline.respondent_account_created ||',' 
            || r_dataline.respondent_enroled ||',' 
            || r_dataline.access_code_authentication_attempt_ind ||',' 
            || r_dataline.collection_instrument_downloaded_ind ||',' 
            || r_dataline.unsuccessful_response_upload_ind ||',' 
            || r_dataline.successful_response_upload_ind ||',' 
            || r_dataline.offline_response_processed_ind ;                                    
            v_rows := v_rows+1;  
      END LOOP;      

      IF v_rows > 0 THEN  
         -- Insert the data into the report table
         INSERT INTO casesvc.report(id, reportPK,reporttypeFK,contents, createddatetime) VALUES(gen_random_uuid(), nextval('casesvc.reportPKseq'), 'CASE_EVENTS', v_contents, CURRENT_TIMESTAMP);
      END IF;

      PERFORM casesvc.logmessage(p_messagetext := 'GENERATING CASE EVENTS REPORT COMPLETED ROWS WRIITEN = ' || v_rows
                                       ,p_jobid := 0
                                       ,p_messagelevel := 'INFO'
                                       ,p_functionname := 'casesvc.generate_case_events_report');
     
   
      PERFORM casesvc.logmessage(p_messagetext := 'CASE EVENTS REPORT GENERATED'
                                       ,p_jobid := 0
                                       ,p_messagelevel := 'INFO'
                                       ,p_functionname := 'casesvc.generate_case_events_report');
 RETURN TRUE;

 EXCEPTION
 WHEN OTHERS THEN  
    PERFORM casesvc.logmessage(p_messagetext := 'GENERATING CASE EVENTS REPORT EXCEPTION TRIGGERED SQLERRM: ' ||

SQLERRM || ' SQLSTATE : ' || SQLSTATE
                              ,p_jobid := 0
                              ,p_messagelevel := 'FATAL'
                              ,p_functionname := 'casesvc.generate_case_events_report');
                             
 RETURN FALSE;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;