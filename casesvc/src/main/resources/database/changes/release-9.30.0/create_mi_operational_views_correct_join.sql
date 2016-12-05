DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_base_data CASCADE;

CREATE MATERIALIZED VIEW casesvc.MI_base_data AS 
(-- Sum all the data by treatment, LAD and respondent type
SELECT 
 sample_size.treatment                            treatment
,sample_size.lad                                  LAD
,final_events.respondenttype                      respondenttype
,sample_size.sample_size                          address_sample_size
,SUM(final_events.ACTIONABLE)                     actionable
,SUM(final_events.no_expected_response)           no_expected_response
,SUM(final_events.RESPONSE)                       response
,SUM(final_events.RESPONSE_PAPER)                 response_paper
,SUM(final_events.RESPONSE_ONLINE)                response_online
,SUM(final_events.paper_and_online_returned)      paper_and_online_returned
,SUM(final_events.refused_and_responded)          refused_and_responded
,SUM(final_events.UNDEL)                          undeliverable_address_not_found
,SUM(final_events.COMMUN)                         incorrect_classification
,SUM(final_events.REFUSAL)                        refusal
,SUM(final_events.FWMT_DEMOLI)                    FWMT_demolished
,SUM(final_events.FWMT_DERELI)                    FWMT_derelict
,SUM(final_events.FWMT_DUPLIC)                    FWMT_duplicate
,SUM(final_events.FWMT_NO_ACC)                    FWMT_no_access
,SUM(final_events.FWMT_UNDER)                     FWMT_under_construction
,SUM(final_events.FWMT_VACANT)                    FWMT_vacant
 FROM
 -- Need the original sample size from the address data
 (SELECT a.sample treatment, a.lad, count(*) sample_size FROM casesvc.address a GROUP BY a.sample, a.lad) sample_size
,(SELECT group_events.* -- calculate whether paper and online returned and whether refused and responded
        ,CASE WHEN group_events.response_paper = 1 AND group_events.response_online = 1 THEN 1 ELSE 0 END paper_and_online_returned 
        ,CASE WHEN group_events.refusal = 1 AND group_events.response = 1 THEN 1 ELSE 0 END refused_and_responded 
  FROM
-- Calculate the extra columns required for responses not expected (NER). Use MAX which should return a 1 distinct case events selected. 
(SELECT 
  pivot.caseid
 ,pivot.casetypeid 
 ,pivot.respondenttype
 ,pivot.lad
 ,pivot.sample
 ,MAX(pivot.no_expected_response)  no_expected_response 
 ,MAX(pivot.UNDEL)                 UNDEL
 ,MAX(pivot.COMMUN)                COMMUN
 ,MAX(pivot.FWMT_DEMOLI)           FWMT_DEMOLI
 ,MAX(pivot.FWMT_DERELI)           FWMT_DERELI
 ,MAX(pivot.FWMT_DUPLIC)           FWMT_DUPLIC
 ,MAX(pivot.FWMT_NO_ACC)           FWMT_NO_ACC
 ,MAX(pivot.REFUSAL)               REFUSAL
 ,MAX(pivot.FWMT_UNDER)            FWMT_UNDER
 ,MAX(pivot.FWMT_VACANT)           FWMT_VACANT 
 ,MAX(pivot.RESPONSE_PAPER)        RESPONSE_PAPER
 ,MAX(pivot.RESPONSE_ONLINE)       RESPONSE_ONLINE
 ,MAX(pivot.ACTIONABLE)            ACTIONABLE
 ,MAX(pivot.RESPONSE)              RESPONSE
FROM
-- pivot the results from the distinct case events 
-- check multi responses for example online - only count once
(SELECT 
 distinct_events.caseid
,distinct_events.casetypeid
,distinct_events.respondenttype
,distinct_events.lad
,distinct_events.sample
,CASE WHEN distinct_events.event = 'UNDEL'           THEN 1 ELSE 0 END  UNDEL
,CASE WHEN distinct_events.event = 'COMMUN'          THEN 1 ELSE 0 END  COMMUN
,CASE WHEN distinct_events.event = 'FWMT_DEMOLI'     THEN 1 ELSE 0 END  FWMT_DEMOLI
,CASE WHEN distinct_events.event = 'FWMT_DERELI'     THEN 1 ELSE 0 END  FWMT_DERELI
,CASE WHEN distinct_events.event = 'FWMT_DUPLIC'     THEN 1 ELSE 0 END  FWMT_DUPLIC
,CASE WHEN distinct_events.event = 'FWMT_NO_ACC'     THEN 1 ELSE 0 END  FWMT_NO_ACC
,CASE WHEN distinct_events.event = 'REFUSAL'         THEN 1 ELSE 0 END  REFUSAL
,CASE WHEN distinct_events.event = 'FWMT_UNDER'      THEN 1 ELSE 0 END  FWMT_UNDER
,CASE WHEN distinct_events.event = 'FWMT_VACANT'     THEN 1 ELSE 0 END  FWMT_VACANT
,CASE WHEN distinct_events.event = 'RESPONSE_PAPER'  THEN 1 ELSE 0 END  RESPONSE_PAPER
,CASE WHEN distinct_events.event = 'RESPONSE_ONLINE' THEN 1 ELSE 0 END  RESPONSE_ONLINE
,CASE WHEN distinct_events.event = 'ACTIONABLE'      THEN 1 ELSE 0 END  ACTIONABLE
,CASE WHEN distinct_events.event IN('RESPONSE_PAPER','RESPONSE_ONLINE') THEN 1 ELSE 0 END RESPONSE -- add up responses
,CASE WHEN distinct_events.event IN('UNDEL','COMMUN','FWMT_DEMOLI','FWMT_DERELI','FWMT_DUPLIC','FWMT_NO_ACC','REFUSAL','FWMT_UNDER','FWMT_VACANT','RESPONSE_PAPER','RESPONSE_ONLINE') THEN 1 ELSE 0 END  no_expected_response -- add up no expected result
FROM 
-- These should all be cases with a state = INACTIONABLE else should be case state of ACTIONABLE. Did distinct to ensure unique events counted
(SELECT DISTINCT 
 ce.caseid
,c.casetypeid 
,ct.respondenttype
,a.lad
,a.sample
,CASE WHEN ((ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(ADDRES)') 
        OR   ce.category  = 'UNDELIVERABLE')                                                                      THEN 'UNDEL' -- Help line undeliverable/FWMT Address Not Found
      WHEN ((ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(COMMUN)')
        OR  (ce.category = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description))  LIKE '%(NON RE)')
        OR   ce.category = 'CLASSIFICATION_INCORRECT') 						                  THEN 'COMMUN' -- FWMT Communal Establishment/Help line classification incorrect/FWMT Non Residential
      WHEN ((ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(REFUSA)') 
          OR ce.category  = 'REFUSAL')                                                                            THEN 'REFUSAL' -- Help line OR FWMT refusal     
      WHEN   ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(DEMOLI)'       THEN 'FWMT_DEMOLI'     -- FWMT demolished
      WHEN   ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(DERELI)'       THEN 'FWMT_DERELI'     -- FWMT Derelict
      WHEN   ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(DUPLIC)'       THEN 'FWMT_DUPLIC'     -- FWMT Duplicate
      WHEN   ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(NO ACC)'       THEN 'FWMT_NO_ACC'     -- FWMT No Access
      WHEN  (ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(UNDER)')
         OR (ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(UNDER )')      THEN 'FWMT_UNDER'      -- FWMT Under Construction
      WHEN   ce.category  = 'ACTION_COMPLETED_DEACTIVATED' AND TRIM(UPPER(ce.description)) LIKE '%(VACANT)'       THEN 'FWMT_VACANT'     -- FWMT Vacant Including 2nd/holiday Home
      WHEN   ce.category  = 'PAPER_QUESTIONNAIRE_RESPONSE'                                                        THEN 'RESPONSE_PAPER'  -- Paper response received
      WHEN   ce.category  = 'ONLINE_QUESTIONNAIRE_RESPONSE'                                                       THEN 'RESPONSE_ONLINE' -- Online response received
      WHEN   c.state      = 'ACTIONABLE'                                                                          THEN 'ACTIONABLE'      -- If none of the above applies count the number of cases that are actionable
END as event
FROM casesvc.caseevent ce
    ,casesvc.case c 
    ,casesvc.casegroup cg
    ,casesvc.casetype ct
    ,casesvc.address a       
WHERE c.caseid = ce.caseid 
AND   c.casegroupid = cg.casegroupid
AND   a.uprn = cg.uprn
AND   ct.casetypeid = c.casetypeid
ORDER BY ce.caseid 
) distinct_events
) pivot GROUP BY pivot.caseid, pivot.casetypeid, pivot.respondenttype, pivot.lad, pivot.sample
) group_events 
) final_events
WHERE sample_size.lad = final_events.lad
AND   sample_size.treatment = final_events.sample
AND   final_events.respondenttype IN('H','CI','C') -- Households, University, Hotels
GROUP BY sample_size.treatment, sample_size.lad, final_events.respondenttype, sample_size.sample_size 
ORDER BY sample_size.treatment,sample_size.lad
);


DROP MATERIALIZED VIEW IF EXISTS casesvc.MI_HH_Return_Metrics;
CREATE MATERIALIZED VIEW casesvc.MI_HH_Return_Metrics AS 
(
SELECT
  SUM(address_sample_size)             Total
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
  SUM(address_sample_size)             Total
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
 ,SUM(address_sample_size)       Total
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
 ,SUM(address_sample_size)       Total
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
  SUM(address_sample_size)       Total
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
  SUM(address_sample_size)       Total
 ,SUM(actionable)                Total_ER
 ,SUM(no_expected_response)      Total_NER
 ,SUM(response)                  Total_returns
 ,SUM(response_online)           Total_online_returns
 ,SUM(response_paper)            Total_paper_returns
 ,SUM(paper_and_online_returned) Total_multi_return
 FROM casesvc.mi_base_data
 WHERE respondenttype = 'C' 
);
  