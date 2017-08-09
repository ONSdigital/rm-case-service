--create sequence for message logging
CREATE SEQUENCE casesvc.messagelogseq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 999999999999
  START 1
  CACHE 1;

--create messagelog table
CREATE TABLE casesvc.messagelog
(
  messagelogpk bigint NOT NULL DEFAULT nextval('casesvc.messagelogseq'::regclass),
  messagetext character varying,
  jobid numeric,
  messagelevel character varying,
  functionname character varying,
  createddatetime timestamp with time zone,
  CONSTRAINT messagelogpk_pkey PRIMARY KEY (messagelogpk)
)
WITH (
  OIDS=FALSE
);

--create function to log messages

CREATE OR REPLACE FUNCTION casesvc.logmessage(p_messagetext text DEFAULT NULL::text, p_jobid numeric DEFAULT NULL::numeric, p_messagelevel text DEFAULT NULL::text, p_functionname text DEFAULT NULL::text)
  RETURNS boolean AS
$BODY$
DECLARE
v_text TEXT ;
v_function TEXT;
BEGIN

INSERT INTO casesvc.messagelog (messagetext, jobid, messagelevel, functionname, createddatetime )
VALUES (p_messagetext, p_jobid, p_messagelevel, p_functionname, current_timestamp);

  RETURN TRUE;
EXCEPTION
WHEN OTHERS THEN
RETURN FALSE;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

--create sequence for reportpk
CREATE SEQUENCE casesvc.reportpkseq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 999999999999
  START 1
  CACHE 1;

-- Table: casesvc.reporttype

-- DROP TABLE casesvc.reporttype cascade;

CREATE TABLE casesvc.reporttype
(
    reporttypepk character varying (20),
    displayorder integer,
    displayname character varying(40),
    CONSTRAINT reporttype_pkey PRIMARY KEY (reporttypepk)
);

CREATE TABLE casesvc.report
(
    id uuid NOT NULL,
    reportpk bigint NOT NULL,
    reporttypefk character varying (20),
    contents text ,
    createddatetime timestamp with time zone,
    CONSTRAINT report_pkey PRIMARY KEY (reportpk),
    CONSTRAINT report_uuid_key UNIQUE (id),
    CONSTRAINT reporttypefk_fkey FOREIGN KEY (reporttypefk)
    REFERENCES casesvc.reporttype (reporttypepk) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

--INSERT seed data into reporttype
INSERT INTO casesvc.reporttype(	reporttypepk, displayorder, displayname)
	VALUES ('CASE_EVENTS', 1, 'Case Events Report');

INSERT INTO casesvc.reporttype(	reporttypepk, displayorder, displayname)
	VALUES ('RESPONSE_CHASING', 2, 'Response Chasing Report');

-- FUNCTION: casesvc.generate_case_events_report()

-- DROP FUNCTION casesvc.generate_case_events_report();

CREATE OR REPLACE FUNCTION casesvc.generate_case_events_report(
	)
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
      v_contents    := 'Sample Unit Ref, Sample Unit Type, Case ID, Case Created , Action Created, Action Completed, Respondent Account Created, Respondent Enrolled, Access Code Authentication Attempt, Collection Instrument Downloaded, Unsuccessful Response Upload, Successful Response Upload, Offline Response Processed'; -- Set header line    

      FOR r_dataline IN (SELECT
  events.sampleunitref 
, events.sampleunittype
, events.casePK
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
  , c.casePK
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
       , events.casePK) LOOP
            v_contents := v_contents || chr(10) || r_dataline.sampleunitref || ',' || r_dataline.sampleunittype || ',' 
            || r_dataline.casePK || ',' 
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

	--function for report chasing
	
	-- FUNCTION: casesvc.generate_report_chasing_report()

	-- DROP FUNCTION casesvc.generate_report_chasing_report();

	CREATE OR REPLACE FUNCTION casesvc.generate_report_chasing_report(
		)
	    RETURNS boolean AS
		$BODY$
DECLARE

	v_contents      text;
	r_dataline      record;
	v_rows          integer;

	BEGIN
   
	   PERFORM casesvc.logmessage(p_messagetext := 'GENERATING REPORT CHASING REPORT'
	                             ,p_jobid := 0
	                             ,p_messagelevel := 'INFO'
	                             ,p_functionname := 'casesvc.generate_report_chasing_report');  
   
	      v_rows := 0;
	      v_contents    := '';
	      v_contents    := 'Sample Unit Ref, Sample Unit Type, Case ID, Authentication Attempt, Account Created , Collection Instrument Downloaded ';   

	      FOR r_dataline IN (SELECT
	  events.sampleunitref
	, events.sampleunittype
	, events.casePK     
	, CASE WHEN events.access_code_authentication_attempt_ind = 1  AND events.respondent_account_created 	 = 0 THEN 1 ELSE 0 END  authentication_attempt_no_account_created  --(B) 
	, (events.respondent_account_created - events.respondent_enroled) account_created_no_enrolment  --(B)  
	, CASE WHEN events.collection_instrument_downloaded_ind   = 1  AND events.successful_response_upload_ind = 0 THEN 1 ELSE 0 END  collection_instrument_downloaded_no_successful_upload --(BI)
	FROM 
	(SELECT 
	    cg.sampleunitref
	  , c.sampleunittype 
	  , c.casePK
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
	       , events.casePK) LOOP
	            v_contents := v_contents || chr(10) || r_dataline.sampleunitref || ',' || r_dataline.sampleunittype || ',' 
	            || r_dataline.casePK || ',' 
	            || r_dataline.authentication_attempt_no_account_created ||',' 
	            || r_dataline.account_created_no_enrolment ||',' 
	            || r_dataline.collection_instrument_downloaded_no_successful_upload ;                                    
	            v_rows := v_rows+1;  
	      END LOOP;      

	      IF v_rows > 0 THEN  
	         -- Insert the data into the report table
	         INSERT INTO casesvc.report(id, reportPK,reporttypeFK,contents, createddatetime) VALUES(gen_random_uuid(), nextval('casesvc.reportPKseq'), 'RESPONSE_CHASING', v_contents, CURRENT_TIMESTAMP);
	      END IF;

	      PERFORM casesvc.logmessage(p_messagetext := 'GENERATING REPORT CHASING REPORT COMPLETED ROWS WRIITEN = ' || v_rows
	                                       ,p_jobid := 0
	                                       ,p_messagelevel := 'INFO'
	                                       ,p_functionname := 'casesvc.generate_report_chasing_report');
     
   
	      PERFORM casesvc.logmessage(p_messagetext := 'REPORT CHASING REPORT GENERATED'
	                                       ,p_jobid := 0
	                                       ,p_messagelevel := 'INFO'
	                                       ,p_functionname := 'casesvc.generate_report_chasing_report');
	 RETURN TRUE;

	 EXCEPTION
	 WHEN OTHERS THEN  
	    PERFORM casesvc.logmessage(p_messagetext := 'GENERATING REPORT CHASING REPORT EXCEPTION TRIGGERED SQLERRM: ' ||

	SQLERRM || ' SQLSTATE : ' || SQLSTATE
	                              ,p_jobid := 0
	                              ,p_messagelevel := 'FATAL'
	                              ,p_functionname := 'casesvc.generate_report_chasing_report');
                             
	 RETURN FALSE;
	END;
	$BODY$
	LANGUAGE plpgsql VOLATILE
	COST 100; 



