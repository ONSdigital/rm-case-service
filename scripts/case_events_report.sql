SELECT
  events.sampleunitref,
  events.sampleunittype,
  events.casePK as case,
  events.case_created,
  events.action_created
  --, events.action_cancellation_created
  --, events.action_cancellation_completed
  ,
  events.action_completed
  --, events.action_updated
  ,
  events.respondent_account_created,
  events.respondent_enroled,
  events.access_code_authentication_attempt_ind,
  events.collection_instrument_downloaded_ind,
  events.unsuccessful_response_upload_ind,
  events.successful_response_upload_ind,
  events.offline_response_processed_ind

FROM
  (SELECT
     cg.sampleunitref,
     c.sampleunittype,
     c.casePK
     -- response chasing categories
     ,
     MAX(CASE WHEN ce.categoryFK = 'ACCESS_CODE_AUTHENTICATION_ATTEMPT'
       THEN 1
         ELSE 0 END) access_code_authentication_attempt_ind  --(B)  -- count distinct event
     ,
     SUM(CASE WHEN ce.categoryFK = 'RESPONDENT_ACCOUNT_CREATED'
       THEN 1
         ELSE 0 END) respondent_account_created      --(B)  -- count all events
     ,
     SUM(CASE WHEN ce.categoryFK = 'RESPONDENT_ENROLED'
       THEN 1
         ELSE 0 END) respondent_enroled        --(B)  -- count all events
     ,
     MAX(CASE WHEN ce.categoryFK = 'COLLECTION_INSTRUMENT_DOWNLOADED'
       THEN 1
         ELSE 0 END) collection_instrument_downloaded_ind  --(BI) -- count distinct event
     ,
     MAX(CASE WHEN ce.categoryFK = 'SUCCESSFUL_RESPONSE_UPLOAD'
       THEN 1
         ELSE 0 END) successful_response_upload_ind    --(BI) -- count distinct event
     -- remaining categories
     ,
     SUM(CASE WHEN ce.categoryFK = 'CASE_CREATED'
       THEN 1
         ELSE 0 END) case_created        --(B,BI) -- count all event
     ,
     SUM(CASE WHEN ce.categoryFK = 'ACTION_CREATED'
       THEN 1
         ELSE 0 END) action_created          --(B,BI) -- count all events
     ,
     SUM(CASE WHEN ce.categoryFK = 'ACTION_CANCELLATION_COMPLETED'
       THEN 1
         ELSE 0 END) action_cancellation_completed    --(B,BI) -- count all events
     ,
     SUM(CASE WHEN ce.categoryFK = 'ACTION_CANCELLATION_CREATED'
       THEN 1
         ELSE 0 END) action_cancellation_created    --(B,BI) -- count all events
     ,
     SUM(CASE WHEN ce.categoryFK = 'ACTION_COMPLETED'
       THEN 1
         ELSE 0 END) action_completed        --(B,BI) -- count all events
     ,
     SUM(CASE WHEN ce.categoryFK = 'ACTION_UPDATED'
       THEN 1
         ELSE 0 END) action_updated        --(B,BI) -- count all events
     ,
     MAX(CASE WHEN ce.categoryFK = 'OFFLINE_RESPONSE_PROCESSED'
       THEN 1
         ELSE 0 END) offline_response_processed_ind    --(BI)	 -- count distinct event
     ,
     MAX(CASE WHEN ce.categoryFK = 'UNSUCCESSFUL_RESPONSE_UPLOAD'
       THEN 1
         ELSE 0 END) unsuccessful_response_upload_ind --(BI)   -- count distinct event
   FROM casesvc.caseevent ce
     RIGHT OUTER JOIN casesvc.case c ON c.casePK = ce.caseFK
     INNER JOIN casesvc.casegroup cg ON c.casegroupFK = cg.casegroupPK
   GROUP BY cg.sampleunitref
     , c.sampleunittype
     , c.casePK) events
ORDER BY events.sampleunitref
  , events.sampleunittype
  , events.casePK