-- View: caseframe.responses_by_day
DROP MATERIALIZED VIEW IF EXISTS caseframe.responses_by_day;

CREATE MATERIALIZED VIEW caseframe.responses_by_day AS 
 SELECT t.days_from_survey, t.days_from_survey || ' DAY' AS day,
    count(t.*) AS responded
   FROM ( SELECT date_part('day', r.responsedatetime - '2016-07-01 00:00:00+01'::timestamp with time zone) AS days_from_survey
           FROM ( SELECT q.responsedatetime
                   FROM caseframe.case c,
                    caseframe.questionnaire q,
                    caseframe.address a 
                  WHERE c.caseid = q.caseid AND q.responsedatetime IS NOT NULL
                  AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn) r) t
 GROUP BY t.days_from_survey
  ORDER BY t.days_from_survey WITH NO DATA;

ALTER TABLE caseframe.responses_by_day
  OWNER TO postgres;





-- View: caseframe.responses_by_perc
DROP MATERIALIZED VIEW IF EXISTS caseframe.responses_by_perc;

CREATE MATERIALIZED VIEW caseframe.responses_by_perc AS 
WITH t AS (SELECT count(*) AS hh_cases_cnt FROM caseframe.case c, caseframe.address a WHERE a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn)    
SELECT  
 CASE WHEN o.outstanding = 0 THEN '0%' ELSE round(o.outstanding::numeric / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%' END  AS outstanding
,CASE WHEN r.responded   = 0 THEN '0%' ELSE round(r.responded::numeric   / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%' END  AS responded
,CASE WHEN f.refused     = 0 THEN '0%' ELSE round(f.refused::numeric     / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%' END AS refused
FROM t, 

(SELECT count(*) AS outstanding
FROM
(SELECT c.caseid
FROM caseframe.case c, caseframe.address a,caseframe.questionnaire q
WHERE c.caseid = q.caseid AND q.responsedatetime IS NULL
AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn
EXCEPT
SELECT c.caseid
FROM caseframe.case c, caseframe.address a,caseframe.caseevent ce
WHERE a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn
AND  c.caseid = ce.caseid 
AND (ce.category  = 'Refusal' OR ce.description LIKE '%(Contac)%') )ref) o 
                               
,(SELECT count(*) AS responded
  FROM caseframe.case c, caseframe.address a,caseframe.questionnaire q
  WHERE c.caseid = q.caseid AND q.responsedatetime IS NOT NULL
  AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn) r
  
,(SELECT count(*) AS refused
  FROM caseframe.case c, caseframe.address a, caseframe.questionnaire q
  WHERE a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn
  AND   c.caseid = q.caseid AND q.responsedatetime IS NULL 
  AND (EXISTS (SELECT ce.caseid
               FROM caseframe.caseevent ce
               WHERE c.caseid = ce.caseid 
               AND (ce.category  = 'Refusal' OR ce.description LIKE '%(Contac)%')))) f;
ALTER TABLE caseframe.responses_by_perc
 OWNER TO postgres;




-- View: caseframe.responses_by_sector
DROP MATERIALIZED VIEW IF EXISTS caseframe.responses_by_sector;      
CREATE MATERIALIZED VIEW caseframe.responses_by_sector AS 

WITH total as
(SELECT SUBSTRING(a.postcode,1,(STRPOS(UPPER(a.postcode),' '))) AS sector, count(*) as tot
FROM caseframe.address a
WHERE a.addresstype = 'HH'
AND a.region11cd = 'E12000005' 
GROUP by sector)

SELECT sector,tot as total,res_cnt as responded,ref_cnt as refused ,ROUND(((res_cnt::numeric + ref_cnt::numeric) / tot::numeric * 100::numeric)::decimal,1) AS perc_complete
FROM (
SELECT total.sector, total.tot
,CASE WHEN responded.res IS NULL THEN 0 ELSE responded.res END as res_cnt
,CASE WHEN refused.ref   IS NULL THEN 0 ELSE refused.ref END as ref_cnt
FROM 
total
FULL JOIN

(SELECT SUBSTRING(a1.postcode,1,(STRPOS(UPPER(a1.postcode),' '))) AS sector, count(*) as res
FROM caseframe.address a1,caseframe.case c1, caseframe.questionnaire q1
WHERE a1.addresstype = 'HH'
AND   a1.region11cd = 'E12000005'
AND   c1.caseid = q1.caseid
AND   q1.responsedatetime IS NOT NULL 
AND   a1.uprn = c1.uprn
GROUP by sector) responded
USING (sector)
FULL JOIN

(SELECT SUBSTRING(fc.postcode,1,(STRPOS(UPPER(fc.postcode),' '))) AS sector, count(*) as ref 
 FROM 
 (SELECT a2.postcode, c2.caseid
 FROM caseframe.case c2, caseframe.address a2, caseframe.questionnaire q2
 WHERE a2.addresstype = 'HH' AND  a2.region11cd = 'E12000005' and c2.uprn = a2.uprn
 AND   c2.caseid = q2.caseid AND q2.responsedatetime IS NULL 
 AND (EXISTS (SELECT ce.caseid
              FROM caseframe.caseevent ce
              WHERE c2.caseid = ce.caseid 
              AND (ce.category  = 'Refusal' OR ce.description LIKE '%(Contac)%')))) fc
 GROUP by sector) refused
 USING (sector)) c
 WITH NO DATA;

ALTER TABLE caseframe.responses_by_sector
  OWNER TO postgres;