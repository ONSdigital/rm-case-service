
-- View: caseframe.responses_by_perc
DROP MATERIALIZED VIEW IF EXISTS caseframe.responses_by_perc;

CREATE MATERIALIZED VIEW caseframe.responses_by_perc AS 

WITH t AS (SELECT count(*) AS hh_cases_cnt FROM caseframe.case c WHERE c.questionset  = 'HH')   
SELECT  
 CASE WHEN op.outstanding = 0 THEN '0%' ELSE round(op.outstanding::numeric / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%' END  AS outstanding
,CASE WHEN rp.responded   = 0 THEN '0%' ELSE round(rp.responded::numeric   / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%' END  AS responded
,CASE WHEN fp.refused     = 0 THEN '0%' ELSE round(fp.refused::numeric     / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%' END AS refused
FROM t, 
(SELECT o.outstanding
           FROM ( SELECT count(*) AS outstanding
                   FROM ( SELECT c.caseid,
                            c.uprn,
                            c.state,
                            c.casetypeid,
                            c.createddatetime,
                            c.createdby,
                            c.sampleid,
                            c.actionplanid,
                            c.surveyid,
                            c.questionset
                           FROM caseframe.case c, caseframe.address a
                          WHERE c.state  = 'INIT' 
                          AND  a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn
                        EXCEPT
                         SELECT c.caseid,
                            c.uprn,
                            c.state,
                            c.casetypeid,
                            c.createddatetime,
                            c.createdby,
                            c.sampleid,
                            c.actionplanid,
                            c.surveyid,
                            c.questionset
                           FROM caseframe.case c, caseframe.address a
                          WHERE c.state  = 'INIT'  AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn
                          AND (EXISTS (SELECT ce.caseid
                               FROM caseframe.caseevent ce
                               WHERE c.caseid = ce.caseid AND UPPER(ce.description) LIKE '%REFUSAL%'))) oc) o) op,
    ( SELECT r.responded
           FROM ( SELECT count(*) AS responded
                   FROM ( SELECT c.caseid,
                            c.uprn,
                            c.state,
                            c.casetypeid,
                            c.createddatetime,
                            c.createdby,
                            c.sampleid,
                            c.actionplanid,
                            c.surveyid,
                            c.questionset,
                            q.responsedatetime
                           FROM caseframe.case c, caseframe.address a,caseframe.questionnaire q
                          WHERE c.state  = 'CLOSED'  AND c.caseid = q.caseid AND q.responsedatetime IS NOT NULL
                          AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn) rc) r) rp,
    ( SELECT f.refused
           FROM ( SELECT count(*) AS refused
                   FROM ( SELECT c.caseid,
                            c.uprn,
                            c.state,
                            c.casetypeid,
                            c.createddatetime,
                            c.createdby,
                            c.sampleid,
                            c.actionplanid,
                            c.surveyid,
                            c.questionset
                           FROM caseframe.case c, caseframe.address a
                          WHERE c.state  = 'CLOSED'  AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn
                          AND (EXISTS (SELECT ce.caseid
                                   FROM caseframe.caseevent ce
                                  WHERE c.caseid = ce.caseid AND ce.category  = 'Refusal' ))
                        UNION
                         SELECT c.caseid,
                            c.uprn,
                            c.state,
                            c.casetypeid,
                            c.createddatetime,
                            c.createdby,
                            c.sampleid,
                            c.actionplanid,
                            c.surveyid,
                            c.questionset
                           FROM caseframe.case c, caseframe.address a
                          WHERE c.state  = 'INIT'  AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn
                          AND (EXISTS (SELECT ce.caseid
                                   FROM caseframe.caseevent ce
                                  WHERE c.caseid = ce.caseid AND UPPER(ce.description) LIKE '%REFUSAL%'))) fc) f) fp;

ALTER TABLE caseframe.responses_by_perc
 OWNER TO postgres;