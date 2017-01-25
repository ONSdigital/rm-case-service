DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_base_data CASCADE;

CREATE MATERIALIZED VIEW casesvc.MI_base_data AS 
(-- Sum all the data by treatment, LAD and respondent type
SELECT 
 sample_size.survey 
,sample_size.treatment                         
,sample_size.lad                                  
,group_events.respondenttype                      
,sample_size.sample_size                          
,SUM(group_events.actionable)                     actionable
,SUM(group_events.no_expected_response)           no_expected_response
,SUM(group_events.response)                       response
,SUM(group_events.response_paper)                 response_paper
,SUM(group_events.response_online)                response_online
,SUM(CASE WHEN group_events.response_paper = 1 AND group_events.response_online = 1 THEN 1 ELSE 0 END) paper_and_online_returned
,SUM(CASE WHEN group_events.refusal        = 1 AND group_events.response        = 1 THEN 1 ELSE 0 END) refused_and_responded
,SUM(group_events.undeliverable)                  undeliverable_address_not_found
,SUM(group_events.communal)                       incorrect_classification
,SUM(group_events.refusal)                        refusal
,SUM(group_events.fwmt_demolished)                fwmt_demolished
,SUM(group_events.fwmt_derelict)                  fwmt_derelict
,SUM(group_events.fwmt_duplicate)                 fwmt_duplicate
,SUM(group_events.fwmt_no_access)                 fwmt_no_access
,SUM(group_events.fwmt_under_construction)        fwmt_under_construction
,SUM(group_events.fwmt_vacant)                    fwmt_vacant
 FROM
 (SELECT a.sample treatment 
       , s.survey
       , a.lad
       , COUNT(*) sample_size -- Need the original sample size from the address data
  FROM casesvc.address a
      ,casesvc.sample s
  WHERE a.sample = s.name     
  GROUP BY a.sample
         , s.survey
         , a.lad) sample_size 
 -- Pivot the results - check multi responses for example online and paper - only count once
 -- These should all be cases with a state = INACTIONABLE else should be case state of ACTIONABLE
,(SELECT 
 ce.caseid
,c.casetypeid 
,ct.respondenttype
,a.lad
,a.sample
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND  TRIM(UPPER(ce.description)) LIKE '%(NOADDRESS)'  
            OR ce.category = 'UNDELIVERABLE'                                                                        THEN 1 ELSE 0 END) undeliverable    -- Help line undeliverable/FWMT Address Not Found
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND (TRIM(UPPER(ce.description)) LIKE '%(COMMUNAL)' OR TRIM(UPPER(ce.description)) LIKE '%(NON_RESIDENTIAL)')
           OR  ce.category = 'CLASSIFICATION_INCORRECT'                                                             THEN 1 ELSE 0 END) communal         -- FWMT Communal Establishment/Help line classification incorrect/FWMT Non Residential
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(REFUSAL)' 
            OR ce.category = 'REFUSAL'                                                                              THEN 1 ELSE 0 END) refusal                 -- Help line OR FWMT refusal     
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(DEMOLISHED)'   THEN 1 ELSE 0 END) fwmt_demolished         -- FWMT demolished
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(DERELICT)'     THEN 1 ELSE 0 END) fwmt_derelict           -- FWMT Derelict
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(DUPLICATE)'    THEN 1 ELSE 0 END) fwmt_duplicate          -- FWMT Duplicate
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(NO_ACCESS)'    THEN 1 ELSE 0 END) fwmt_no_access          -- FWMT No Access
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(UNDER_CONST)'  THEN 1 ELSE 0 END) fwmt_under_construction -- FWMT Under Construction
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(VACANT)'       THEN 1 ELSE 0 END) fwmt_vacant             -- FWMT Vacant Including 2nd/holiday Home
,MAX(CASE WHEN ce.category = 'PAPER_QUESTIONNAIRE_RESPONSE'                                                         THEN 1 ELSE 0 END) response_paper          -- Paper response received
,MAX(CASE WHEN ce.category = 'ONLINE_QUESTIONNAIRE_RESPONSE'                                                        THEN 1 ELSE 0 END) response_online         -- Online response received
,MAX(CASE WHEN ce.category IN('PAPER_QUESTIONNAIRE_RESPONSE' ,'ONLINE_QUESTIONNAIRE_RESPONSE')                      THEN 1 ELSE 0 END) response                -- Add up responses
,MAX(CASE WHEN c.state     = 'ACTIONABLE'                                                                           THEN 1 ELSE 0 END) actionable              -- If none of the above applies count the number of cases that are actionable
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND
               (  TRIM(UPPER(ce.description)) LIKE '%(NOADDRESS)'
               OR TRIM(UPPER(ce.description)) LIKE '%(COMMUNAL)'
               OR TRIM(UPPER(ce.description)) LIKE '%(NON_RESIDENTIAL)'
               OR TRIM(UPPER(ce.description)) LIKE '%(UNDER_CONST)'
               OR TRIM(UPPER(ce.description)) LIKE '%(REFUSAL)'
               OR TRIM(UPPER(ce.description)) LIKE '%(DEMOLISHED)'
               OR TRIM(UPPER(ce.description)) LIKE '%(DUPLICATE)'
               OR TRIM(UPPER(ce.description)) LIKE '%(DERELICT)'
               OR TRIM(UPPER(ce.description)) LIKE '%(NO_ACCESS)'
               OR TRIM(UPPER(ce.description)) LIKE '%(VACANT)')
               OR  ce.category IN('PAPER_QUESTIONNAIRE_RESPONSE' ,'ONLINE_QUESTIONNAIRE_RESPONSE','REFUSAL','CLASSIFICATION_INCORRECT','UNDELIVERABLE') THEN 1 ELSE 0 END) no_expected_response -- No response/further response expected
FROM  casesvc.caseevent ce
    , casesvc.case c 
    , casesvc.casegroup cg
    , casesvc.casetype ct
    , casesvc.address a       
WHERE c.caseid      = ce.caseid 
AND   c.casegroupid = cg.casegroupid
AND   a.uprn        = cg.uprn
AND   ct.casetypeid = c.casetypeid
GROUP BY ce.caseid
       , c.casetypeid
       , ct.respondenttype
       , a.lad
       , a.sample) group_events 
WHERE sample_size.lad       = group_events.lad
AND   sample_size.treatment = group_events.sample
AND   group_events.respondenttype IN('H','CI','C') -- Household, University, Hotel, sheltered housing
GROUP BY sample_size.survey
       , sample_size.treatment
       , sample_size.lad
       , group_events.respondenttype
       , sample_size.sample_size 
ORDER BY sample_size.survey
       , sample_size.treatment
       , sample_size.lad);


DROP MATERIALIZED VIEW IF EXISTS casesvc.HH_RETURNRATE;
CREATE MATERIALIZED VIEW casesvc.HH_RETURNRATE AS 
(
SELECT
  SUM(b.sample_size)                     Total
 ,SUM(b.actionable)                      Total_ER
 ,SUM(b.no_expected_response)            Total_NER
 ,SUM(b.response)                        Total_returns
 ,SUM(b.response_online)                 Total_online_returns
 ,SUM(b.response_paper)                  Total_paper_receipted
 ,SUM(b.paper_and_online_returned)       Total_multi_return
 FROM casesvc.mi_base_data b
 WHERE b.respondenttype = 'H'
   AND b.treatment != 'SHOUSING'
   AND b.survey = '2017 TEST'
);


DROP MATERIALIZED VIEW IF EXISTS casesvc.HH_NORETURNS;
CREATE MATERIALIZED VIEW casesvc.HH_NORETURNS AS 
(
SELECT
  SUM(b.sample_size)                     Total
 ,SUM(b.no_expected_response)            Total_NER
 ,SUM(b.refusal)                         Total_Refused
 ,SUM(b.incorrect_classification)        Total_Incorrect_Classification
 ,SUM(b.undeliverable_address_not_found) Total_Address_Not_Found
 ,SUM(b.fwmt_demolished)                 Total_Demolished
 ,SUM(b.fwmt_derelict)                   Total_Derelict
 ,SUM(b.fwmt_duplicate)                  Total_Duplicate
 ,SUM(b.fwmt_no_access)                  Total_No_Access
 ,SUM(b.fwmt_vacant)                     Total_Vacant_Inc2nd_HolidayHome
 ,SUM(b.fwmt_under_construction)         Total_Under_construction 
 FROM casesvc.mi_base_data b
 WHERE b.respondenttype = 'H'
   AND b.treatment != 'SHOUSING'
   AND b.survey = '2017 TEST'
);

DROP MATERIALIZED VIEW IF EXISTS casesvc.HH_RETURNRATE_SAMPLE;
CREATE MATERIALIZED VIEW casesvc.HH_RETURNRATE_SAMPLE AS 
(
SELECT
  b.treatment                      Sample
 ,SUM(b.sample_size)               Total
 ,SUM(b.actionable)                Total_ER
 ,SUM(b.no_expected_response)      Total_NER
 ,SUM(b.response)                  Total_returns
 ,SUM(b.response_online)           Total_online_returns
 ,SUM(b.response_paper)            Total_paper_receipted
 ,SUM(b.paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data b
 WHERE b.respondenttype = 'H'
   AND b.treatment != 'SHOUSING'
   AND b.survey = '2017 TEST'
 GROUP BY b.treatment
);

DROP MATERIALIZED VIEW IF EXISTS casesvc.HH_RETURNRATE_LA;
CREATE MATERIALIZED VIEW casesvc.HH_RETURNRATE_LA AS 
(
SELECT
  b.LAD                      
 ,SUM(b.sample_size)               Total
 ,SUM(b.actionable)                Total_ER
 ,SUM(b.no_expected_response)      Total_NER
 ,SUM(b.response)                  Total_returns
 ,SUM(b.response_online)           Total_online_returns
 ,SUM(b.response_paper)            Total_paper_receipted
 ,SUM(b.paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data b
 WHERE b.respondenttype = 'H'
   AND b.treatment != 'SHOUSING'
   AND b.survey = '2017 TEST'
 GROUP BY b.LAD
);


DROP MATERIALIZED VIEW IF EXISTS casesvc.CE_RETURNRATE_UNI;
CREATE MATERIALIZED VIEW casesvc.CE_RETURNRATE_UNI AS 
(
SELECT
  SUM(b.sample_size)               Total
 ,SUM(b.actionable)                Total_ER
 ,SUM(b.no_expected_response)      Total_NER
 ,SUM(b.response)                  Total_returns
 ,SUM(b.response_online)           Total_online_returns
 ,SUM(b.response_paper)            Total_paper_receipted
 ,SUM(b.paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data b
 WHERE b.respondenttype = 'CI'
   AND b.survey = '2017 TEST'
);


DROP MATERIALIZED VIEW IF EXISTS casesvc.CE_RETURNRATE_HOTEL;
CREATE MATERIALIZED VIEW casesvc.CE_RETURNRATE_HOTEL AS 
( -- Hotels can only respond online
SELECT
  SUM(b.sample_size)               Total
 ,SUM(b.actionable)                Total_ER
 ,SUM(b.no_expected_response)      Total_NER
 ,SUM(b.response)                  Total_returns
 ,SUM(b.response_online)           Total_online_returns
 ,SUM(b.response_paper)            Total_paper_receipted
 ,SUM(b.paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data b
 WHERE b.respondenttype = 'C'
   AND b.survey = '2017 TEST' 
);
  

DROP MATERIALIZED VIEW IF EXISTS casesvc.CE_RETURNRATE_SHOUSING;
CREATE MATERIALIZED VIEW casesvc.CE_RETURNRATE_SHOUSING AS 
( -- Hotels can only respond online
SELECT
  SUM(b.sample_size)               Total
 ,SUM(b.actionable)                Total_ER
 ,SUM(b.no_expected_response)      Total_NER
 ,SUM(b.response)                  Total_returns
 ,SUM(b.response_online)           Total_online_returns
 ,SUM(b.response_paper)            Total_paper_receipted
 ,SUM(b.paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data b
 WHERE b.respondenttype = 'H'
   AND b.treatment = 'SHOUSING' 
   AND b.survey = '2017 TEST'
);


