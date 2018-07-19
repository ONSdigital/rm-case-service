SELECT
  events.sampleunitref,
  events.sampleunittype,
  events.casePK as                                                case,
  CASE WHEN events.access_code_authentication_attempt_ind = 1 AND
            events.respondent_account_created = 0
    THEN 1
  ELSE 0 END                                                      authentication_attempt_no_account_created
  --(B)
  ,
  (events.respondent_account_created - events.respondent_enroled) account_created_no_enrolment
  --(B)
  ,
  CASE WHEN events.collection_instrument_downloaded_ind = 1 AND
            events.successful_response_upload_ind = 0
    THEN 1
  ELSE 0 END                                                      collection_instrument_downloaded_no_successful_upload --(BI)
FROM
  (SELECT
     cg.sampleunitref,
     c.sampleunittype,
     c.casePK,
     MAX(CASE WHEN ce.categoryFK = 'ACCESS_CODE_AUTHENTICATION_ATTEMPT'
       THEN 1
         ELSE 0 END) access_code_authentication_attempt_ind --(B)  -- count distinct event
     ,
     SUM(CASE WHEN ce.categoryFK = 'RESPONDENT_ACCOUNT_CREATED'
       THEN 1
         ELSE 0 END) respondent_account_created       --(B)  -- count all events
     ,
     SUM(CASE WHEN ce.categoryFK = 'RESPONDENT_ENROLED'
       THEN 1
         ELSE 0 END) respondent_enroled         --(B)  -- count all events
     ,
     MAX(CASE WHEN ce.categoryFK = 'COLLECTION_INSTRUMENT_DOWNLOADED'
       THEN 1
         ELSE 0 END) collection_instrument_downloaded_ind   --(BI) -- count distinct event
     ,
     MAX(CASE WHEN ce.categoryFK = 'SUCCESSFUL_RESPONSE_UPLOAD'
       THEN 1
         ELSE 0 END) successful_response_upload_ind --(BI) -- count distinct event
   FROM casesvc.caseevent ce
     RIGHT OUTER JOIN casesvc.case c ON c.casePK = ce.caseFK
     INNER JOIN casesvc.casegroup cg ON c.casegroupFK = cg.casegroupPK
   WHERE ce.categoryFK = ANY
         (ARRAY ['ACCESS_CODE_AUTHENTICATION_ATTEMPT', 'RESPONDENT_ACCOUNT_CREATED', 'RESPONDENT_ENROLED', 'COLLECTION_INSTRUMENT_DOWNLOADED', 'SUCCESSFUL_RESPONSE_UPLOAD'])
   GROUP BY cg.sampleunitref
     , c.sampleunittype
     , c.casePK) events
WHERE (events.access_code_authentication_attempt_ind = 1 AND events.respondent_account_created = 0)
      OR (events.collection_instrument_downloaded_ind = 1 AND
          events.successful_response_upload_ind = 0)
      OR (events.respondent_account_created > events.respondent_enroled)
ORDER BY events.sampleunitref
  , events.sampleunittype
  , events.casePK
 