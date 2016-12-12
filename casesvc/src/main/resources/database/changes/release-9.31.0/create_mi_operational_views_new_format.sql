DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_base_data CASCADE;

CREATE MATERIALIZED VIEW casesvc.MI_base_data AS 
(-- Sum all the data by treatment, LAD and respondent type
SELECT 
 sample_size.treatment                            
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
,SUM(group_events.undel)                          undeliverable_address_not_found
,SUM(group_events.commun)                         incorrect_classification
,SUM(group_events.refusal)                        refusal
,SUM(group_events.fwmt_demoli)                    fwmt_demolished
,SUM(group_events.fwmt_dereli)                    fwmt_derelict
,SUM(group_events.fwmt_duplic)                    fwmt_duplicate
,SUM(group_events.fwmt_no_acc)                    fwmt_no_access
,SUM(group_events.fwmt_under)                     fwmt_under_construction
,SUM(group_events.fwmt_vacant)                    fwmt_vacant
 FROM
 (SELECT a.sample treatment 
       , a.lad
       , COUNT(*) sample_size -- Need the original sample size from the address data
  FROM casesvc.address a 
  GROUP BY a.sample
         , a.lad) sample_size 
 -- Pivot the results - check multi responses for example online and paper - only count once
 -- These should all be cases with a state = INACTIONABLE else should be case state of ACTIONABLE
,(SELECT 
 ce.caseid
,c.casetypeid 
,ct.respondenttype
,a.lad
,a.sample
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND  TRIM(UPPER(ce.description)) LIKE '%(ADDRES)' 
            OR ce.category = 'UNDELIVERABLE'                                                                                                                    THEN 1 ELSE 0 END) undel           -- Help line undeliverable/FWMT Address Not Found
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND (TRIM(UPPER(ce.description)) LIKE '%(COMMUN)' OR TRIM(UPPER(ce.description)) LIKE '%(NON RE)')
           OR  ce.category = 'CLASSIFICATION_INCORRECT'                                                                                                         THEN 1 ELSE 0 END) commun          -- FWMT Communal Establishment/Help line classification incorrect/FWMT Non Residential
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(REFUSA)' 
            OR ce.category = 'REFUSAL'                                                                                                                          THEN 1 ELSE 0 END) refusal         -- Help line OR FWMT refusal     
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(DEMOLI)'                                                   THEN 1 ELSE 0 END) fwmt_demoli     -- FWMT demolished
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(DERELI)'                                                   THEN 1 ELSE 0 END) fwmt_dereli     -- FWMT Derelict
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(DUPLIC)'                                                   THEN 1 ELSE 0 END) fwmt_duplic     -- FWMT Duplicate
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(NO ACC)'                                                   THEN 1 ELSE 0 END) fwmt_no_acc     -- FWMT No Access
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND (TRIM(UPPER(ce.description)) LIKE '%(UNDER)' OR TRIM(UPPER(ce.description)) LIKE '%(UNDER )')   THEN 1 ELSE 0 END) fwmt_under      -- FWMT Under Construction
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(VACANT)'                                                   THEN 1 ELSE 0 END) fwmt_vacant     -- FWMT Vacant Including 2nd/holiday Home
,MAX(CASE WHEN ce.category = 'PAPER_QUESTIONNAIRE_RESPONSE'                                                                                                     THEN 1 ELSE 0 END) response_paper  -- Paper response received
,MAX(CASE WHEN ce.category = 'ONLINE_QUESTIONNAIRE_RESPONSE'                                                                                                    THEN 1 ELSE 0 END) response_online -- Online response received
,MAX(CASE WHEN ce.category IN('PAPER_QUESTIONNAIRE_RESPONSE' ,'ONLINE_QUESTIONNAIRE_RESPONSE')                                                                  THEN 1 ELSE 0 END) response        -- Add up responses
,MAX(CASE WHEN c.state     = 'ACTIONABLE'                                                                                                                       THEN 1 ELSE 0 END) actionable      -- If none of the above applies count the number of cases that are actionable
,MAX(CASE WHEN ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND
               (  TRIM(UPPER(ce.description)) LIKE '%(ADDRES)'
               OR TRIM(UPPER(ce.description)) LIKE '%(COMMUN)'
               OR TRIM(UPPER(ce.description)) LIKE '%(NON RE)'
               OR TRIM(UPPER(ce.description)) LIKE '%(UNDER)'
               OR TRIM(UPPER(ce.description)) LIKE '%(UNDER )'
               OR TRIM(UPPER(ce.description)) LIKE '%(REFUSA)'
               OR TRIM(UPPER(ce.description)) LIKE '%(DEMOLI)'
               OR TRIM(UPPER(ce.description)) LIKE '%(DUPLIC)' 
               OR TRIM(UPPER(ce.description)) LIKE '%(DERELI)'
               OR TRIM(UPPER(ce.description)) LIKE '%(NO ACC)'
               OR TRIM(UPPER(ce.description)) LIKE '%(VACANT)')
               OR  ce.category IN('PAPER_QUESTIONNAIRE_RESPONSE' ,'ONLINE_QUESTIONNAIRE_RESPONSE','REFUSAL','CLASSIFICATION_INCORRECT','UNDELIVERABLE')        THEN 1 ELSE 0 END) no_expected_response -- No response/further response expected
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
AND   group_events.respondenttype IN('H','CI','C') -- Household, University, Hotel
GROUP BY sample_size.treatment
       , sample_size.lad
       , group_events.respondenttype
       , sample_size.sample_size 
ORDER BY sample_size.treatment
       , sample_size.lad
);


DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_HH_Return_Metrics;
CREATE MATERIALIZED VIEW casesvc.MI_HH_Return_Metrics AS 
(
SELECT
  SUM(sample_size)                     Total
 ,SUM(actionable)                      Total_ER
 ,SUM(no_expected_response)            Total_NER
 ,SUM(response)                        Total_returns
 ,SUM(response_online)                 Total_online_returns
 ,SUM(response_paper)                  Total_paper_returns
 ,SUM(paper_and_online_returned)       Total_multi_return
 FROM casesvc.mi_base_data
 WHERE respondenttype = 'H'
);


DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_HH_NoReturn_Metrics;
CREATE MATERIALIZED VIEW casesvc.MI_HH_NoReturn_Metrics AS 
(
SELECT
  SUM(sample_size)                     Total
 ,SUM(no_expected_response)            Total_NER
 ,SUM(refusal)                         Total_Refused
 ,SUM(incorrect_classification)        Total_Incorrect_Classification
 ,SUM(undeliverable_address_not_found) Total_Address_Not_Found
 ,SUM(fwmt_demolished)                 Total_Demolished
 ,SUM(fwmt_derelict)                   Total_Derelict
 ,SUM(fwmt_duplicate)                  Total_Duplicate
 ,SUM(fwmt_no_access)                  Total_No_Access
 ,SUM(fwmt_vacant)                     Total_Vacant_Inc2nd_HolidayHome
 ,SUM(fwmt_under_construction)         Total_Under_construction 
 FROM casesvc.mi_base_data
 WHERE respondenttype = 'H'
);

DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_HH_by_sample;
CREATE MATERIALIZED VIEW casesvc.MI_HH_by_sample AS 
(
SELECT
  treatment                      Sample
 ,SUM(sample_size)               Total
 ,SUM(actionable)                Total_ER
 ,SUM(no_expected_response)      Total_NER
 ,SUM(response)                  Total_returns
 ,SUM(response_online)           Total_online_returns
 ,SUM(response_paper)            Total_paper_returns
 ,SUM(paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data
 WHERE respondenttype = 'H'
 GROUP BY treatment
);

DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_HH_by_LAD;
CREATE MATERIALIZED VIEW casesvc.MI_HH_by_LAD AS 
(
SELECT
  LAD                      
 ,SUM(sample_size)               Total
 ,SUM(actionable)                Total_ER
 ,SUM(no_expected_response)      Total_NER
 ,SUM(response)                  Total_returns
 ,SUM(response_online)           Total_online_returns
 ,SUM(response_paper)            Total_paper_returns
 ,SUM(paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data
 WHERE respondenttype = 'H'
 GROUP BY LAD
);


DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_CE_Return_Metrics_Uni;
CREATE MATERIALIZED VIEW casesvc.MI_CE_Return_Metrics_Uni AS 
(
SELECT
  SUM(sample_size)               Total
 ,SUM(actionable)                Total_ER
 ,SUM(no_expected_response)      Total_NER
 ,SUM(response)                  Total_returns
 ,SUM(response_online)           Total_online_returns
 ,SUM(response_paper)            Total_paper_returns
 ,SUM(paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data
 WHERE respondenttype = 'CI'
);


DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_CE_Return_Metrics_Hotel;
CREATE MATERIALIZED VIEW casesvc.MI_CE_Return_Metrics_Hotel AS 
( -- Hotels can only respond online
SELECT
  SUM(sample_size)               Total
 ,SUM(actionable)                Total_ER
 ,SUM(no_expected_response)      Total_NER
 ,SUM(response)                  Total_returns
 ,SUM(response_online)           Total_online_returns
 ,SUM(response_paper)            Total_paper_returns
 ,SUM(paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data
 WHERE respondenttype = 'C' 
);
  