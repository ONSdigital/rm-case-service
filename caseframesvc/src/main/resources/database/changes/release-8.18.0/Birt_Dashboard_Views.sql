
-- View: caseframe.responses_by_day
-- DROP MATERIALIZED VIEW IF EXISTS caseframe.responses_by_day;

CREATE MATERIALIZED VIEW caseframe.responses_by_day AS 
 SELECT t.days_from_survey, t.days_from_survey || ' DAY' AS day,
    count(t.*) AS responded
   FROM ( SELECT date_part('day', r.responsedatetime - '2016-07-01 00:00:00+01'::timestamp with time zone) AS days_from_survey
           FROM ( SELECT q.responsedatetime
                   FROM caseframe.case c,
                    caseframe.questionnaire q,
                    caseframe.address a 
                  WHERE c.state  = 'CLOSED'  AND c.caseid = q.caseid AND q.responsedatetime IS NOT NULL
                  AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn) r) t
 GROUP BY t.days_from_survey
  ORDER BY t.days_from_survey;

ALTER TABLE caseframe.responses_by_day
  OWNER TO postgres;





-- View: caseframe.responses_by_perc
-- DROP MATERIALIZED VIEW IF EXISTS caseframe.responses_by_perc;

CREATE MATERIALIZED VIEW caseframe.responses_by_perc AS 
 WITH t AS (
         SELECT count(*) AS hh_cases_cnt
           FROM caseframe.case c
          WHERE c.questionset  = 'HH' 
        )
 SELECT op.outstanding,
    rp.responded,
    fp.refused
   FROM ( SELECT round(o.oustanding::numeric / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%'  AS outstanding
           FROM ( SELECT count(*) AS oustanding
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
                               WHERE c.caseid = ce.caseid AND UPPER(ce.description) LIKE '%REFUSAL%'))) oc) o,
            t) op,
    ( SELECT round(r.responded::numeric / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%'  AS responded
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
                          AND a.addresstype = 'HH' AND  a.region11cd = 'E12000005' and c.uprn = a.uprn) rc) r,
            t) rp,
    ( SELECT round(f.refusal::numeric / t.hh_cases_cnt::numeric * 100::numeric)::integer || '%'  AS refused
           FROM ( SELECT count(*) AS refusal
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
                                  WHERE c.caseid = ce.caseid AND UPPER(ce.description) LIKE '%REFUSAL%'))) fc) f,
            t) fp;

ALTER TABLE caseframe.responses_by_perc
  OWNER TO postgres;




-- View: caseframe.responses_by_sector
-- DROP MATERIALIZED VIEW IF EXISTS caseframe.responses_by_sector;

CREATE MATERIALIZED VIEW caseframe.responses_by_sector AS 
SELECT d.sector
    ,d.tot_cnt as total
    ,d.res_cnt as responded
    ,d.percentage 
FROM
(SELECT s.sector,s.tot_cnt,s.res_cnt,ROUND((s.res_cnt::numeric / s.tot_cnt::numeric * 100::numeric)::decimal,1) AS percentage
FROM
(SELECT sector,
       tot_cnt,
       CASE WHEN res_cnt IS NULL THEN 0 ELSE res_cnt END AS res_cnt                 
FROM (SELECT t.sector, COUNT(*) as tot_cnt
FROM (SELECT SUBSTRING(postcode,1,(STRPOS(UPPER(postcode),' '))) AS sector
 FROM caseframe.address 
 WHERE addresstype = 'HH'
 AND region11cd = 'E12000005') t
GROUP by sector) t2
FULL JOIN
(SELECT r.sector, COUNT(*) as res_cnt
FROM
(SELECT SUBSTRING(postcode,1,(STRPOS(UPPER(postcode),' '))) AS sector
FROM caseframe.address a,caseframe.case c, caseframe.questionnaire q
WHERE a.addresstype = 'HH'
AND   a.region11cd = 'E12000005'
AND   c.state = 'CLOSED'
AND   c.caseid = q.caseid
AND   q.responsedatetime IS NOT NULL 
AND   a.uprn = c.uprn) r
GROUP BY sector) r2
USING (sector)
)s) d ;

ALTER TABLE caseframe.responses_by_sector
  OWNER TO postgres;



-- Function: caseframe.refresh_materialised_views()
-- DROP FUNCTION caseframe.refresh_materialised_views();

CREATE OR REPLACE FUNCTION caseframe.refresh_materialised_views()
  RETURNS integer AS
$BODY$BEGIN
	/* Add any other materialised views here */
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_DAY;
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_PERC;
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_SECTOR;

	RETURN 1;
END$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.refresh_materialised_views()
  OWNER TO postgres;
COMMENT ON FUNCTION caseframe.refresh_materialised_views() IS 'Refreshs the materialised views that are used in the system. Should be called periodically via a crontab or manually on-demand';



