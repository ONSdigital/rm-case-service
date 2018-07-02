-- Function: casesvc.generate_response_chasing_report()

-- DROP FUNCTION casesvc.generate_response_chasing_report();

CREATE OR REPLACE FUNCTION casesvc.generate_response_chasing_report()
  RETURNS boolean AS
$BODY$
DECLARE

	v_contents      text;
	r_dataline      record;
	v_rows          integer;

	BEGIN
   
	   PERFORM casesvc.logmessage(p_messagetext := 'GENERATING RESPONSE CHASING REPORT'
	                             ,p_jobid := 0
	                             ,p_messagelevel := 'INFO'
	                             ,p_functionname := 'casesvc.generate_response_chasing_report');  
   
	      v_rows := 0;
	      v_contents    := '';
	      v_contents    := 'Sample Unit Ref,Sample Unit Type,Case Ref,Authentication Attempt No Account Created,Account Created No Enrolment,Collection Instrument Downloaded No Successful Response Upload';   

	      FOR r_dataline IN (SELECT
	  events.sampleunitref
	, events.sampleunittype
	, events.caseref  
	, CASE WHEN events.access_code_authentication_attempt_ind = 1  AND events.respondent_account_created 	 = 0 THEN 1 ELSE 0 END  authentication_attempt_no_account_created  --(B) 
	, (events.respondent_account_created - events.respondent_enroled) account_created_no_enrolment  --(B)  
	, CASE WHEN events.collection_instrument_downloaded_ind   = 1  AND events.successful_response_upload_ind = 0 THEN 1 ELSE 0 END  collection_instrument_downloaded_no_successful_upload --(BI)
	FROM 
	(SELECT 
	    cg.sampleunitref
	  , c.sampleunittype 
	  , c.caseref
	  , MAX(CASE WHEN ce.categoryFK = 'ACCESS_CODE_AUTHENTICATION_ATTEMPT'  THEN 1 ELSE  0 END) access_code_authentication_attempt_ind --(B)  -- count distinct event
	  , SUM(CASE WHEN ce.categoryFK = 'RESPONDENT_ACCOUNT_CREATED' 		THEN 1 ELSE  0 END) respondent_account_created 		   --(B)  -- count all events
	  , SUM(CASE WHEN ce.categoryFK = 'RESPONDENT_ENROLED' 			THEN 1 ELSE  0 END) respondent_enroled 			   --(B)  -- count all events
	  , MAX(CASE WHEN ce.categoryFK = 'COLLECTION_INSTRUMENT_DOWNLOADED'    THEN 1 ELSE  0 END) collection_instrument_downloaded_ind   --(BI) -- count distinct event
	  , MAX(CASE WHEN ce.categoryFK = 'SUCCESSFUL_RESPONSE_UPLOAD' 		THEN 1 ELSE  0 END) successful_response_upload_ind	   --(BI) -- count distinct event 
	FROM casesvc.caseevent ce
	RIGHT OUTER JOIN casesvc.case c  ON c.casePK      = ce.caseFK 
	INNER JOIN casesvc.casegroup cg  ON c.casegroupFK = cg.casegroupPK
	WHERE ce.categoryFK = ANY (ARRAY['ACCESS_CODE_AUTHENTICATION_ATTEMPT','RESPONDENT_ACCOUNT_CREATED','RESPONDENT_ENROLED','COLLECTION_INSTRUMENT_DOWNLOADED','SUCCESSFUL_RESPONSE_UPLOAD'])
	GROUP BY cg.sampleunitref
	       , c.sampleunittype
	       , c.casePK) events
	WHERE (events.access_code_authentication_attempt_ind = 1  AND events.respondent_account_created     = 0)
	OR    (events.collection_instrument_downloaded_ind   = 1  AND events.successful_response_upload_ind = 0)
	OR    (events.respondent_account_created > events.respondent_enroled)
	ORDER BY events.sampleunitref
	       , events.sampleunittype
	       , events.caseref) LOOP
	            v_contents := v_contents || chr(10) || r_dataline.sampleunitref || ',' || r_dataline.sampleunittype || ',' 
	            || r_dataline.caseref || ',' 
	            || r_dataline.authentication_attempt_no_account_created ||',' 
	            || r_dataline.account_created_no_enrolment ||',' 
	            || r_dataline.collection_instrument_downloaded_no_successful_upload ;                                    
	            v_rows := v_rows+1;  
	      END LOOP;      

	      IF v_rows > 0 THEN  
	         -- Insert the data into the report table
	         INSERT INTO casesvc.report(id, reportPK,reporttypeFK,contents, createddatetime) VALUES(gen_random_uuid(), nextval('casesvc.reportPKseq'), 'RESPONSE_CHASING', v_contents, CURRENT_TIMESTAMP);
	      END IF;

	      PERFORM casesvc.logmessage(p_messagetext := 'GENERATING RESPONSE CHASING REPORT COMPLETED ROWS WRIITEN = ' || v_rows
	                                       ,p_jobid := 0
	                                       ,p_messagelevel := 'INFO'
	                                       ,p_functionname := 'casesvc.generate_response_chasing_report');
     
   
	      PERFORM casesvc.logmessage(p_messagetext := 'RESPONSE CHASING REPORT GENERATED'
	                                       ,p_jobid := 0
	                                       ,p_messagelevel := 'INFO'
	                                       ,p_functionname := 'casesvc.generate_response_chasing_report');
	 RETURN TRUE;

	 EXCEPTION
	 WHEN OTHERS THEN  
	    PERFORM casesvc.logmessage(p_messagetext := 'GENERATING RESPONSE CHASING REPORT EXCEPTION TRIGGERED SQLERRM: ' ||

	SQLERRM || ' SQLSTATE : ' || SQLSTATE
	                              ,p_jobid := 0
	                              ,p_messagelevel := 'FATAL'
	                              ,p_functionname := 'casesvc.generate_response_chasing_report');
                             
	 RETURN FALSE;
	END;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;