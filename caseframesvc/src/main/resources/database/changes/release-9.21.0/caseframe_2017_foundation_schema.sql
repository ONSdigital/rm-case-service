--script to create all tables, functions, views and sequences for foundation for 2017
--11 tables, 4 sequences, 10 functions, 4 views


SET SCHEMA 'caseframe';


-- Name: create_questionnaire(bigint); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION create_questionnaire(p_caseid bigint) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
v_questionset character varying(10);
v_errmess text;

BEGIN

-- Called to create a questionnaire for the case id passed in

-- Get the formtype from the case table for the case id passed in
SELECT c.questionset INTO v_questionset
FROM caseframe.case c
WHERE caseid = p_caseid;

-- Insert a record into the questionnaire table

INSERT INTO caseframe.questionnaire
(
 questionnaireid
,caseid
--,state
--,dispatchdatetime
--,responsedatetime
--,receiptdatetime
,questionset
,iac 
) 
(SELECT
 NEXTVAL('caseframe.qidseq'::regclass)
,p_caseid
--,state
--,dispatchdatetime
--,responsedatetime
--,receiptdatetime
,v_questionset
,SUBSTRING(CURRVAL('caseframe.qidseq'::regclass)::text,6,5) || SUBSTRING(CURRVAL('caseframe.qidseq'::regclass)::text,1,5)
);

  RETURN TRUE;

EXCEPTION

 WHEN OTHERS THEN
    PERFORM caseframe.logmessage(p_messagetext := 'CREATE QUESTIONNAIRE EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.create_questionnaire');
  RETURN FALSE; 
END;
$$;


ALTER FUNCTION caseframe.create_questionnaire(p_caseid bigint) OWNER TO postgres;

-- Name: create_questionnaires(); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION create_questionnaires() RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
r_case record;
v_case_count integer;
BEGIN
   -- Initialse case count
   v_case_count := 0;

   -- For each case in the case table call the funtion to create a questionnaire
   FOR r_case IN SELECT c.caseid 
                  FROM caseframe.case c
                  WHERE NOT EXISTS
                        (SELECT q.caseid 
                         FROM caseframe.questionnaire q 
                         WHERE q.caseid = c.caseid) LOOP
        
	EXECUTE 'SELECT * FROM caseframe.create_questionnaire(' || r_case.caseid || ')';
        v_case_count := v_case_count + 1;
   END LOOP;

   PERFORM caseframe.logmessage(p_messagetext := v_case_count || ' NEW QUESTIONNAIRE(S) CREATED FROM CASE TABLE'
                            ,p_jobid := 0   
                            ,p_messagelevel := 'INFO'
                            ,p_functionname := 'caseframe.create_questionnaires');


RETURN TRUE;

EXCEPTION

 WHEN OTHERS THEN
    PERFORM action.logmessage(p_messagetext := 'CREATE QUESTIONNAIES EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.create_questionnaires');
  RETURN FALSE;    

END;
$$;


ALTER FUNCTION caseframe.create_questionnaires() OWNER TO postgres;

--
-- Name: eastings_northings_to_lat(numeric, numeric); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION eastings_northings_to_lat(eastings numeric, northings numeric) RETURNS double precision
    LANGUAGE plpgsql
    AS $$
DECLARE
	a float := 6377563.396;
	b float := 6356256.91;
	F0 float := 0.9996012717;
	E0 float := 400000;
	N0 float := -100000;
	PHI0 float := 0.855211333;
	LAM0 float := -0.034906585;
	north1 integer;
  	east1 integer;
  	aFo float;
  	bFo float;
  	e2 float;
  	n float;
  	initPHI float;
  	nuPL float;
  	rhoPL float;
  	eta2PL float;
  	M float;
  	Et float;
  	vii float;
  	viii float;
  	ix float;
  	lat float;
BEGIN
	/** cast to integer*/
	north1 := CAST(northings as integer);   
	east1 :=CAST(eastings as integer);
      
	aFo := a * F0;
	bFo := b * F0;
	e2 := ((aFo ^ 2) - (bFo ^ 2)) / (aFo ^ 2);
	n := (aFo - bFo) / (aFo + bFo);
	
	InitPHI = caseframe.PHId(North1, N0, aFo, PHI0, n, bFo);
	nuPL := aFo / ((1 - (e2 * (Sin(InitPHI)) ^ 2)) ^ 0.5);
	rhoPL := (nuPL * (1 - e2)) / (1 - (e2 * (Sin(InitPHI)) ^ 2));
	eta2PL = (nuPL / rhoPL) - 1;
	M = caseframe.Marc(bFo, n, PHI0, InitPHI);
	Et = East1 - E0;
	VII = (Tan(InitPHI)) / (2 * nuPL * rhoPL);
	VIII = ((Tan(InitPHI)) / (24 * rhoPL * nuPL ^ 3)) * (5 + (3 * ((Tan(InitPHI)) ^ 2)) + eta2PL - (9 * ((Tan(InitPHI)) ^ 2) * eta2PL));
	IX = ((Tan(InitPHI)) / (720 * rhoPL * nuPL ^ 5)) * (61 + (90 * ((Tan(InitPHI)) ^ 2)) + (45 * ((Tan(InitPHI)) ^ 4)));
	lat = (InitPHI - ((Et ^ 2) * VII) + ((Et ^ 4) * VIII) - ((Et ^ 6) * IX));
	return degrees(lat);
END;$$;


ALTER FUNCTION caseframe.eastings_northings_to_lat(eastings numeric, northings numeric) OWNER TO postgres;

--
-- Name: eastings_northings_to_long(numeric, numeric); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION eastings_northings_to_long(eastings numeric, northings numeric) RETURNS double precision
    LANGUAGE plpgsql
    AS $$
DECLARE
	a float = 6377563.396;
	b float = 6356256.91;
	F0 float = 0.9996012717;
	E0 float = 400000;
	N0 float = -100000;
	PHI0 float = 0.855211333;
	LAM0 float = -0.034906585;
	aFo float;
	bFo float;
	e2 float;
	n float;
	initphi float;
	nupl float;
	rhopl float;
	eta2pl float;
	m float;
	et float;
	x float;
	XI float;
	XII float;
	XIIA float;
	long floaT;
	east1 integer;
	north1 integer;
BEGIN
	/** cast to integer*/
	north1 := CAST(northings as integer);   
	east1 :=CAST(eastings as integer);
	
	aFo = a * F0;
	bFo = b * F0;
	e2 = (aFo ^ 2 - bFo ^ 2) / aFo ^ 2;
	n = (aFo - bFo) / (aFo + bFo);
	InitPHI = caseframe.PHId(North1, N0, aFo, PHI0, n, bFo);
	nuPL = aFo / ((1 - (e2 * (Sin(InitPHI)) ^ 2)) ^ 0.5);
	rhoPL = (nuPL * (1 - e2)) / (1 - (e2 * (Sin(InitPHI)) ^ 2));
	eta2PL = (nuPL / rhoPL) - 1;
	M = caseframe.Marc(bFo, n, PHI0, InitPHI);
	Et = East1 - E0;
	X = ((Cos(InitPHI)) ^ -1) / nuPL;
	XI = (((Cos(InitPHI)) ^ -1) / (6 * nuPL ^ 3)) * ((nuPL / rhoPL) + (2 * ((Tan(InitPHI)) ^ 2)));
	XII = (((Cos(InitPHI)) ^ -1) / (120 * nuPL ^ 5)) * (5 + (28 * ((Tan(InitPHI)) ^ 2)) + (24 * ((Tan(InitPHI)) ^ 4)));
	XIIA = (((Cos(InitPHI)) ^ -1) / (5040 * nuPL ^ 7)) * (61 + (662 * ((Tan(InitPHI)) ^ 2)) + (1320 * ((Tan(InitPHI)) ^ 4)) + (720 * ((Tan(InitPHI)) ^ 6)));
	long = (LAM0 + (Et * X) - ((Et ^ 3) * XI) + ((Et ^ 5) * XII) - ((Et ^ 7) * XIIA));
	return degrees(long);
END;$$;


ALTER FUNCTION caseframe.eastings_northings_to_long(eastings numeric, northings numeric) OWNER TO postgres;

--
-- Name: generate_cases(integer, character varying, character varying); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION generate_cases(p_sampleid integer, p_geog_area_type character varying, p_geog_area_code character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
v_addresscriteria character varying(100);
v_sql_text text;
v_geog_select_text text;
v_rowcount integer;

BEGIN
           SELECT CASE p_geog_area_type
           WHEN 'OA' THEN   'oa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LSOA' THEN  'lsoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'MSOA' THEN 'msoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LA' THEN 'lad12cd = ''' || p_geog_area_code ||''''
           WHEN 'REGION' THEN 'region11cd = ''' || p_geog_area_code || ''''
          ELSE '0=1' --not a valid area type
       END INTO v_geog_select_text ;


 select addresscriteria from caseframe.sample where sampleid = p_sampleid into v_addresscriteria;

IF v_geog_select_text = '0=1' THEN
   PERFORM caseframe.logmessage(p_messagetext := '*** 0 CASES CREATED - INVALID Geography Area Type: ' || p_geog_area_type || ' *** SQL NOT created for Generate Cases for Sampleid: ' || p_sampleid || ', Geography Area Type: ' || p_geog_area_type 
                               || ', Geography Area Code: ' || p_geog_area_code || ', Address Criteria: ' || v_addresscriteria
                               ,p_jobid := 0   
                               ,p_messagelevel := 'ERROR'
                               ,p_functionname := 'caseframe.generate_cases');

ELSE


   v_sql_text := 'INSERT INTO caseframe.case(caseid, uprn, state, casetypeid, createddatetime, createdby, sampleid, actionplanid, surveyid, questionset)
   select nextval(''caseframe.caseidseq'')
   ,a.uprn
   ,''INIT''
   ,s.casetypeid
   ,CURRENT_TIMESTAMP
   ,''SYSTEM''
   ,s.sampleid
   ,ct.actionplanid
   ,s.surveyid
   ,ct.questionset
   from caseframe.sample s
  ,caseframe.casetype ct
  ,caseframe.address a where ' || v_geog_select_text  ||' and s.sampleid = ' || p_sampleid || ' and s.casetypeid = ct.casetypeid and ' || v_addresscriteria
   || 'and a.uprn NOT IN (SELECT uprn from caseframe.case )';


   PERFORM caseframe.logmessage(p_messagetext := 'SQL created for Generate Cases for Sampleid: ' || p_sampleid || ', Geography Area Type: ' || p_geog_area_type 
                               || ', Geography Area Code: ' || p_geog_area_code || ', Address Criteria: ' || v_addresscriteria
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'caseframe.generate_cases');

   EXECUTE v_sql_text;


   GET DIAGNOSTICS v_rowcount = ROW_COUNT;  

   PERFORM caseframe.logmessage(p_messagetext := v_rowcount  || ' NEW CASES generated for Sampleid: ' || p_sampleid || ', Geography Area Type: ' || p_geog_area_type 
                                || ', Geography Area Code: ' || p_geog_area_code || ', Address Criteria: ' || v_addresscriteria
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'caseframe.generate_cases');

   --for each case created log to caseevent that case created
   --this method is only acceptable for 2016 and will need to be revisited later
   --as cases are only created this way.
   --which is why has not been separated out to separate function

INSERT INTO caseframe.caseevent(
            caseeventid, caseid, description, createdby, createddatetime, 
            category)
SELECT nextval('caseframe.caseeventidseq'),
   c.caseid,      
   'Initial Creation Of Case',
   c.createdby,
   c.createddatetime,
   'CaseCreated'
            FROM caseframe.case c
           WHERE c.caseid NOT IN
           (SELECT caseid FROM caseframe.caseevent ce
           WHERE ce.category = 'CaseCreated');


   --create questionnaires for each case
  PERFORM caseframe.create_questionnaires();
END IF;


  RETURN TRUE;

  EXCEPTION

 WHEN OTHERS THEN
    PERFORM caseframe.logmessage(p_messagetext := 'GENERATE CASES EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'caseframe.generate_cases');
  RETURN FALSE;

END;
$$;


ALTER FUNCTION caseframe.generate_cases(p_sampleid integer, p_geog_area_type character varying, p_geog_area_code character varying) OWNER TO postgres;

--
-- Name: generate_helpline_mi_reports(); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION generate_helpline_mi_reports() RETURNS boolean
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE
v_sql_text text;
v_view_query text;
v_filename text;
v_directory text;
v_filedatestamp text;

BEGIN

 refresh materialized view caseframe.helpline_mi;

 v_directory := '/var/helpline-mi/' ;

 v_filedatestamp := to_char(current_timestamp, 'DDMMYYYY' );

--hourly

  v_view_query := '(SELECT to_char(helpline_mi.createddatetime,''DD-MM-YYYY HH24:00'' ) as Hour,
                   count(1) as Call_Total
                   FROM caseframe.helpline_mi 
                   WHERE role like ''%cso%''
                   AND subcategory is null
                   AND UPPER(category) <> ''REFUSAL''
                   GROUP BY hour)';

   
 v_filename := v_directory || 'hl_calls_hour_' || v_filedatestamp || '.csv' ;

 v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

 EXECUTE v_sql_text;

--daily

 v_view_query := '(SELECT to_char(helpline_mi.createddatetime, ''DD-MM-YYYY'') as Day,
		   count(1) as Call_Total
		   FROM caseframe.helpline_mi
	           WHERE role like ''%cso%''
	           AND subcategory is null
	           AND UPPER(category) <> ''REFUSAL''
                   GROUP BY day)';
   
 v_filename := v_directory ||'hl_calls_day_'|| v_filedatestamp || '.csv' ;

 v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

 EXECUTE v_sql_text;

--weekly

 v_view_query := '(SELECT to_char(date_trunc(''week''::text, helpline_mi.createddatetime),''DD-MM-YYYY'') as Week,
                   count(1) as Call_Total
                   FROM caseframe.helpline_mi
                   WHERE role like ''%cso%''
                   AND subcategory is null
                   AND UPPER(category) <> ''REFUSAL''
                   GROUP BY week)';
   
 v_filename := v_directory || 'hl_calls_week_' || v_filedatestamp || '.csv' ;

 v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

 EXECUTE v_sql_text;


--Calls resolved without escalation

 v_view_query := '(SELECT category, count(*) as Call_Total 
                   FROM caseframe.helpline_mi
                   WHERE role like ''%cso%''
                   AND subcategory is null
                   AND UPPER(category) not like ''%ESCALATED%''
                   AND UPPER(category) not like ''%REFUSAL%''
                   GROUP BY category
                   ORDER BY 1)';

  v_filename := v_directory || 'hl_calls_not_escalated_' || v_filedatestamp || '.csv' ;

  v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

  EXECUTE v_sql_text;

--escalated calls breakdown

  v_view_query := '(SELECT category, count(*) as Call_Total FROM caseframe.helpline_mi
                    WHERE role like ''%cso%''
                    AND subcategory is null
                    AND UPPER(category) like ''%ESCALATED%''
                    GROUP BY category
                    ORDER BY 1)';

  v_filename := v_directory || 'hl_calls_escalated_' || v_filedatestamp || '.csv' ;

  v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

  EXECUTE v_sql_text;

--escalated calls resolution

  v_view_query := '(SELECT category, count(*) as Call_Total FROM caseframe.helpline_mi 
                    WHERE role like ''%esc%''
                    AND UPPER(category) <> ''REFUSAL''
                    GROUP BY category
                    ORDER BY 1)';

  v_filename := v_directory || 'hl_escalated_outcomes_' || v_filedatestamp || '.csv' ;

  v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

  EXECUTE v_sql_text;

--refused calls details

  v_view_query := '(SELECT DESCRIPTION, CREATEDBY, TO_CHAR(CREATEDDATETIME, ''DD-MM-YYYY HH24:MM:SS'') as time_logged 
	            FROM CASEFRAME.HELPLINE_MI 
	            WHERE UPPER(CATEGORY) = ''REFUSAL''
		    ORDER BY time_logged)';

  v_filename := v_directory || 'hl_refusal_details_' || v_filedatestamp || '.csv' ;

  v_sql_text := ' COPY ' || v_view_query || ' TO ''' || v_filename || ''' DELIMITER '','' CSV HEADER';

  EXECUTE v_sql_text;
  
  RETURN true;

  EXCEPTION
  WHEN OTHERS THEN
   PERFORM caseframe.logmessage(p_messagetext := 'generate_helpline_mi_reports' || v_sql_text
                               ,p_jobid := 0   
                               ,p_messagelevel := 'INFO'
                               ,p_functionname := 'caseframe.generate_helpline_mi_reports');
  return false;

END;
$$;


ALTER FUNCTION caseframe.generate_helpline_mi_reports() OWNER TO postgres;

--
-- Name: logmessage(text, numeric, text, text); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION logmessage(p_messagetext text DEFAULT NULL::text, p_jobid numeric DEFAULT NULL::numeric, p_messagelevel text DEFAULT NULL::text, p_functionname text DEFAULT NULL::text) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
v_text TEXT ;
v_function TEXT;
BEGIN
INSERT INTO caseframe.messagelog
(messagetext, jobid, messagelevel, functionname, createddatetime )
values (p_messagetext, p_jobid, p_messagelevel, p_functionname, current_timestamp);
  RETURN TRUE;
EXCEPTION
WHEN OTHERS THEN
RETURN FALSE;
END;
$$;


ALTER FUNCTION caseframe.logmessage(p_messagetext text, p_jobid numeric, p_messagelevel text, p_functionname text) OWNER TO postgres;

--
-- TOC entry 390 (class 1255 OID 28928)
-- Name: marc(double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION marc(bfo double precision, n double precision, p1 double precision, p2 double precision) RETURNS double precision
    LANGUAGE plpgsql
    AS $$
BEGIN 
  RETURN bFo * (((1 + n + ((5 / 4) * (n ^ 2)) + ((5 / 4) * (n ^ 3))) * (P2 - P1)) - (((3 * n) + (3 * (n ^ 2)) + ((21 / 8) * (n ^ 3)))
              * (Sin(P2 - P1)) * (Cos(P2 + P1))) + ((((15 / 8) * (n ^ 2)) + ((15 / 8) * (n ^ 3))) * (Sin(2 * (P2 - P1)))
              * (Cos(2 * (P2 + P1)))) - (((35 / 24) * (n ^ 3)) * (Sin(3 * (P2 - P1))) * (Cos(3 * (P2 + P1)))));
End$$;


ALTER FUNCTION caseframe.marc(bfo double precision, n double precision, p1 double precision, p2 double precision) OWNER TO postgres;

--
-- TOC entry 391 (class 1255 OID 28929)
-- Name: phid(integer, double precision, double precision, double precision, double precision, double precision); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION phid(north1 integer, n0 double precision, afo double precision, phi0 double precision, n double precision, bfo double precision) RETURNS double precision
    LANGUAGE plpgsql
    AS $$
DECLARE
	phi1 float;
	phi2 float;
	m float;
BEGIN
	PHI1 = ((North1 - N0) / aFo) + PHI0;
	M = caseframe.Marc(bFo, n, PHI0, PHI1);
	PHI2 = ((North1 - N0 - M) / aFo) + PHI1;
	LOOP
  		IF Abs(North1 - N0 - M) > 0.000000001 THEN
    		EXIT;
  		END IF;
		PHI2 = ((North1 - N0 - M) / aFo) + PHI1;
		M = caseframe.Marc(bFo, n, PHI0, PHI2);
		PHI1 = PHI2;
	end Loop;
	return PHI2;
End$$;


ALTER FUNCTION caseframe.phid(north1 integer, n0 double precision, afo double precision, phi0 double precision, n double precision, bfo double precision) OWNER TO postgres;

--
-- TOC entry 393 (class 1255 OID 28959)
-- Name: refresh_materialised_views(); Type: FUNCTION; Schema: caseframe; Owner: postgres
--

CREATE FUNCTION refresh_materialised_views() RETURNS integer
    LANGUAGE plpgsql
    AS $$BEGIN
	/* Add any other materialised views here */
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_DAY;
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_PERC;
	REFRESH MATERIALIZED VIEW CASEFRAME.RESPONSES_BY_SECTOR;

	RETURN 1;
END$$;


ALTER FUNCTION caseframe.refresh_materialised_views() OWNER TO postgres;

--
-- Dependencies: 393
-- Name: FUNCTION refresh_materialised_views(); Type: COMMENT; Schema: caseframe; Owner: postgres
--

COMMENT ON FUNCTION refresh_materialised_views() IS 'Refreshs the materialised views that are used in the system. Should be called periodically via a crontab or manually on-demand';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 279 (class 1259 OID 28905)
-- Name: address; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE address (
    uprn numeric(12,0) NOT NULL,
    addresstype character varying(6),
    estabtype character varying(6),
    category character varying(20),
    organisation_name character varying(60),
    address_line1 character varying(60),
    address_line2 character varying(60),
    locality character varying(35),
    town_name character varying(30),
    postcode character varying(8),
    oa11cd character varying(9),
    lsoa11cd character varying(9),
    msoa11cd character varying(9),
    lad12cd character varying(9),
    region11cd character varying(9),
    eastings numeric(8,0),
    northings numeric(8,0),
    htc numeric(8,0),
    latitude double precision,
    longitude double precision
);


ALTER TABLE caseframe.address OWNER TO role_connect;

--
-- TOC entry 266 (class 1259 OID 28658)
-- Name: case; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE "case" (
    caseid bigint NOT NULL,
    uprn numeric(12,0),
    state character varying(10),
    casetypeid integer,
    createddatetime timestamp with time zone,
    createdby character varying(50),
    sampleid integer,
    actionplanid integer,
    surveyid integer,
    questionset character varying(10)
);


ALTER TABLE caseframe."case" OWNER TO role_connect;

--
-- Name: caseeventidseq; Type: SEQUENCE; Schema: caseframe; Owner: postgres
--

CREATE SEQUENCE caseeventidseq
    START WITH 762
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE caseframe.caseeventidseq OWNER TO postgres;

--
-- TOC entry 267 (class 1259 OID 28661)
-- Name: caseevent; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE caseevent (
    caseeventid bigint DEFAULT nextval('caseeventidseq'::regclass) NOT NULL,
    caseid bigint NOT NULL,
    description character varying(350),
    createdby character varying(50),
    createddatetime timestamp with time zone,
    category character varying(40),
    subcategory character varying(100)
);


ALTER TABLE caseframe.caseevent OWNER TO role_connect;

--
-- TOC entry 274 (class 1259 OID 28778)
-- Name: caseidseq; Type: SEQUENCE; Schema: caseframe; Owner: postgres
--

CREATE SEQUENCE caseidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE caseframe.caseidseq OWNER TO postgres;

--
-- TOC entry 280 (class 1259 OID 28930)
-- Name: casestate; Type: TABLE; Schema: caseframe; Owner: postgres; Tablespace: 
--

CREATE TABLE casestate (
    state character varying(100) NOT NULL,
    description character varying(250)
);


ALTER TABLE caseframe.casestate OWNER TO postgres;

--
-- TOC entry 268 (class 1259 OID 28664)
-- Name: casetype; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE casetype (
    casetypeid integer NOT NULL,
    name character varying(20),
    description character varying(100),
    actionplanid integer,
    questionset character varying(10)
);


ALTER TABLE caseframe.casetype OWNER TO role_connect;

--
-- TOC entry 276 (class 1259 OID 28806)
-- Name: category; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE category (
    name character varying(40) NOT NULL,
    description character varying(50),
    closecase boolean,
    manual boolean,
    role character varying(50),
    generatedactiontype character varying(100)
);


ALTER TABLE caseframe.category OWNER TO role_connect;


--
-- Name: helpline_mi; Type: MATERIALIZED VIEW; Schema: caseframe; Owner: postgres; Tablespace: 
--

CREATE MATERIALIZED VIEW helpline_mi AS
 SELECT caseevent.caseeventid,
    caseevent.caseid,
    caseevent.description,
    caseevent.createdby,
    caseevent.createddatetime,
    caseevent.category,
    caseevent.subcategory,
    category.role
   FROM caseevent,
    category
  WHERE (((caseevent.createdby)::text <> 'SYSTEM'::text) AND ((caseevent.category)::text = (category.name)::text))
  WITH NO DATA;


ALTER TABLE caseframe.helpline_mi OWNER TO postgres;

--
-- Name: messageseq; Type: SEQUENCE; Schema: caseframe; Owner: postgres
--

CREATE SEQUENCE messageseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE caseframe.messageseq OWNER TO postgres;

--
-- Name: messagelog; Type: TABLE; Schema: caseframe; Owner: postgres; Tablespace: 
--

CREATE TABLE messagelog (
    messageid bigint DEFAULT nextval('messageseq'::regclass) NOT NULL,
    messagetext character varying,
    jobid numeric,
    messagelevel character varying,
    functionname character varying,
    createddatetime timestamp with time zone
);


ALTER TABLE caseframe.messagelog OWNER TO postgres;

--
-- Name: qidseq; Type: SEQUENCE; Schema: caseframe; Owner: postgres
--

CREATE SEQUENCE qidseq
    START WITH 1234567890
    INCREMENT BY 1
    MINVALUE 1234567890
    MAXVALUE 9999999999
    CACHE 1;


ALTER TABLE caseframe.qidseq OWNER TO postgres;

--
-- Name: questionnaire; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE questionnaire (
    questionnaireid bigint NOT NULL,
    caseid bigint,
    state character varying(10),
    dispatchdatetime timestamp with time zone,
    responsedatetime timestamp with time zone,
    receiptdatetime timestamp with time zone,
    questionset character varying(10),
    iac character(10)
);


ALTER TABLE caseframe.questionnaire OWNER TO role_connect;

--
-- Name: questionset; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE questionset (
    questionset character varying(10) NOT NULL,
    description character varying(100)
);


ALTER TABLE caseframe.questionset OWNER TO role_connect;

--
-- Name: responses_by_day; Type: MATERIALIZED VIEW; Schema: caseframe; Owner: postgres; Tablespace: 
--

CREATE MATERIALIZED VIEW responses_by_day AS
 SELECT t.days_from_survey,
    (t.days_from_survey || ' DAY'::text) AS day,
    count(t.*) AS responded
   FROM ( SELECT date_part('day'::text, (r.responsedatetime - '2016-06-30 23:00:00+00'::timestamp with time zone)) AS days_from_survey
           FROM ( SELECT q.responsedatetime
                   FROM "case" c,
                    questionnaire q,
                    address a
                  WHERE (((((c.caseid = q.caseid) AND (q.responsedatetime IS NOT NULL)) AND ((a.addresstype)::text = 'HH'::text)) AND ((a.region11cd)::text = 'E12000005'::text)) AND (c.uprn = a.uprn))) r) t
  GROUP BY t.days_from_survey
  ORDER BY t.days_from_survey
  WITH NO DATA;


ALTER TABLE caseframe.responses_by_day OWNER TO postgres;

--
-- Name: responses_by_perc; Type: MATERIALIZED VIEW; Schema: caseframe; Owner: postgres; Tablespace: 
--

CREATE MATERIALIZED VIEW responses_by_perc AS
 WITH t AS (
         SELECT count(*) AS hh_cases_cnt
           FROM "case" c,
            address a
          WHERE ((((a.addresstype)::text = 'HH'::text) AND ((a.region11cd)::text = 'E12000005'::text)) AND (c.uprn = a.uprn))
        )
 SELECT
        CASE
            WHEN (o.outstanding = 0) THEN '0%'::text
            ELSE ((round((((o.outstanding)::numeric / (t.hh_cases_cnt)::numeric) * (100)::numeric)))::integer || '%'::text)
        END AS outstanding,
        CASE
            WHEN (r.responded = 0) THEN '0%'::text
            ELSE ((round((((r.responded)::numeric / (t.hh_cases_cnt)::numeric) * (100)::numeric)))::integer || '%'::text)
        END AS responded,
        CASE
            WHEN (f.refused = 0) THEN '0%'::text
            ELSE ((round((((f.refused)::numeric / (t.hh_cases_cnt)::numeric) * (100)::numeric)))::integer || '%'::text)
        END AS refused
   FROM t,
    ( SELECT count(*) AS outstanding
           FROM ( SELECT c.caseid
                   FROM "case" c,
                    address a,
                    questionnaire q
                  WHERE (((((c.caseid = q.caseid) AND (q.responsedatetime IS NULL)) AND ((a.addresstype)::text = 'HH'::text)) AND ((a.region11cd)::text = 'E12000005'::text)) AND (c.uprn = a.uprn))
                EXCEPT
                 SELECT c.caseid
                   FROM "case" c,
                    address a,
                    caseevent ce
                  WHERE ((((((a.addresstype)::text = 'HH'::text) AND ((a.region11cd)::text = 'E12000005'::text)) AND (c.uprn = a.uprn)) AND (c.caseid = ce.caseid)) AND (((ce.category)::text = 'Refusal'::text) OR ((ce.description)::text ~~ '%(Contac)%'::text)))) ref) o,
    ( SELECT count(*) AS responded
           FROM "case" c,
            address a,
            questionnaire q
          WHERE (((((c.caseid = q.caseid) AND (q.responsedatetime IS NOT NULL)) AND ((a.addresstype)::text = 'HH'::text)) AND ((a.region11cd)::text = 'E12000005'::text)) AND (c.uprn = a.uprn))) r,
    ( SELECT count(*) AS refused
           FROM "case" c,
            address a,
            questionnaire q
          WHERE (((((((a.addresstype)::text = 'HH'::text) AND ((a.region11cd)::text = 'E12000005'::text)) AND (c.uprn = a.uprn)) AND (c.caseid = q.caseid)) AND (q.responsedatetime IS NULL)) AND (EXISTS ( SELECT ce.caseid
                   FROM caseevent ce
                  WHERE ((c.caseid = ce.caseid) AND (((ce.category)::text = 'Refusal'::text) OR ((ce.description)::text ~~ '%(Contac)%'::text))))))) f
  WITH NO DATA;


ALTER TABLE caseframe.responses_by_perc OWNER TO postgres;

--
-- Name: responses_by_sector; Type: MATERIALIZED VIEW; Schema: caseframe; Owner: postgres; Tablespace: 
--

CREATE MATERIALIZED VIEW responses_by_sector AS
 WITH total AS (
         SELECT "substring"((a.postcode)::text, 1, strpos(upper((a.postcode)::text), ' '::text)) AS sector,
            count(*) AS tot
           FROM address a
          WHERE (((a.addresstype)::text = 'HH'::text) AND ((a.region11cd)::text = 'E12000005'::text))
          GROUP BY "substring"((a.postcode)::text, 1, strpos(upper((a.postcode)::text), ' '::text))
        )
 SELECT c.sector,
    c.tot AS total,
    c.res_cnt AS responded,
    c.ref_cnt AS refused,
    round(((((c.res_cnt)::numeric + (c.ref_cnt)::numeric) / (c.tot)::numeric) * (100)::numeric), 1) AS perc_complete
   FROM ( SELECT total.sector,
            total.tot,
                CASE
                    WHEN (responded.res IS NULL) THEN (0)::bigint
                    ELSE responded.res
                END AS res_cnt,
                CASE
                    WHEN (refused.ref IS NULL) THEN (0)::bigint
                    ELSE refused.ref
                END AS ref_cnt
           FROM ((total
             FULL JOIN ( SELECT "substring"((a1.postcode)::text, 1, strpos(upper((a1.postcode)::text), ' '::text)) AS sector,
                    count(*) AS res
                   FROM address a1,
                    "case" c1,
                    questionnaire q1
                  WHERE ((((((a1.addresstype)::text = 'HH'::text) AND ((a1.region11cd)::text = 'E12000005'::text)) AND (c1.caseid = q1.caseid)) AND (q1.responsedatetime IS NOT NULL)) AND (a1.uprn = c1.uprn))
                  GROUP BY "substring"((a1.postcode)::text, 1, strpos(upper((a1.postcode)::text), ' '::text))) responded USING (sector))
             FULL JOIN ( SELECT "substring"((fc.postcode)::text, 1, strpos(upper((fc.postcode)::text), ' '::text)) AS sector,
                    count(*) AS ref
                   FROM ( SELECT a2.postcode,
                            c2.caseid
                           FROM "case" c2,
                            address a2,
                            questionnaire q2
                          WHERE (((((((a2.addresstype)::text = 'HH'::text) AND ((a2.region11cd)::text = 'E12000005'::text)) AND (c2.uprn = a2.uprn)) AND (c2.caseid = q2.caseid)) AND (q2.responsedatetime IS NULL)) AND (EXISTS ( SELECT ce.caseid
                                   FROM caseevent ce
                                  WHERE ((c2.caseid = ce.caseid) AND (((ce.category)::text = 'Refusal'::text) OR ((ce.description)::text ~~ '%(Contac)%'::text))))))) fc
                  GROUP BY "substring"((fc.postcode)::text, 1, strpos(upper((fc.postcode)::text), ' '::text))) refused USING (sector))) c
  WITH NO DATA;


ALTER TABLE caseframe.responses_by_sector OWNER TO postgres;

--
-- Name: sample; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE sample (
    sampleid integer NOT NULL,
    name character varying(20),
    description character varying(100),
    addresscriteria character varying(100),
    casetypeid integer,
    surveyid integer
);


ALTER TABLE caseframe.sample OWNER TO role_connect;

--
-- Name: survey; Type: TABLE; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE TABLE survey (
    surveyid integer NOT NULL,
    name character varying(20),
    description character varying(100)
);


ALTER TABLE caseframe.survey OWNER TO role_connect;

--
-- Name: address_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (uprn);


--
-- Name: case_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (caseid);


--
-- Name: caseevent_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventid);


--
-- Name: casestate_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casestate
    ADD CONSTRAINT casestate_pkey PRIMARY KEY (state);


--
-- Name: casetype_pkey1; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_pkey1 PRIMARY KEY (casetypeid);


--
-- Name: category_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (name);


--
-- Name: messageid_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY messagelog
    ADD CONSTRAINT messageid_pkey PRIMARY KEY (messageid);


--
-- Name: questionnaire_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY questionnaire
    ADD CONSTRAINT questionnaire_pkey PRIMARY KEY (questionnaireid);


--
-- Name: questionset_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY questionset
    ADD CONSTRAINT questionset_pkey PRIMARY KEY (questionset);


--
-- Name: sample_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (sampleid);


--
-- Name: survey_pkey; Type: CONSTRAINT; Schema: caseframe; Owner: role_connect; Tablespace: 
--

ALTER TABLE ONLY survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (surveyid);


--
-- Name: address_lad12cd_idx; Type: INDEX; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE INDEX address_lad12cd_idx ON address USING btree (lad12cd);


--
-- Name: address_msoa11cd_idx; Type: INDEX; Schema: caseframe; Owner: role_connect; Tablespace: 
--

CREATE INDEX address_msoa11cd_idx ON address USING btree (msoa11cd);


-- Name: case_uprn_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_uprn_fkey FOREIGN KEY (uprn) REFERENCES address(uprn);


--
-- Name: case_casetypeid_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- Name: case_questionset_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);


--
-- Name: case_sampleid_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);


--
-- Name: case_surveyid_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_surveyid_fkey FOREIGN KEY (surveyid) REFERENCES survey(surveyid);


--
-- Name: caseevent_caseid_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- Name: caseevent_category_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_category_fkey FOREIGN KEY (category) REFERENCES category(name);


--
-- Name: casetype_questionset_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);


--
-- Name: questionnaire_caseid_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY questionnaire
    ADD CONSTRAINT questionnaire_caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- Name: questionnaire_questionset_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY questionnaire
    ADD CONSTRAINT questionnaire_questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);


--
-- Name: sample_casetypeid_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- Name: sample_surveyid_fkey; Type: FK CONSTRAINT; Schema: caseframe; Owner: role_connect
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_surveyid_fkey FOREIGN KEY (surveyid) REFERENCES survey(surveyid);


--
-- Name: caseframe; Type: ACL; Schema: -; Owner: role_connect
--

REVOKE ALL ON SCHEMA caseframe FROM PUBLIC;
REVOKE ALL ON SCHEMA caseframe FROM role_connect;
GRANT ALL ON SCHEMA caseframe TO role_connect;
GRANT ALL ON SCHEMA caseframe TO caseframesvc;


--
-- Name: address; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE address FROM PUBLIC;
REVOKE ALL ON TABLE address FROM role_connect;
GRANT ALL ON TABLE address TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE address TO caseframesvc;


--
-- Name: case; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE "case" FROM PUBLIC;
REVOKE ALL ON TABLE "case" FROM role_connect;
GRANT ALL ON TABLE "case" TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE "case" TO caseframesvc;


--
-- Name: caseeventidseq; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON SEQUENCE caseeventidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caseeventidseq FROM postgres;
GRANT ALL ON SEQUENCE caseeventidseq TO postgres;
GRANT ALL ON SEQUENCE caseeventidseq TO caseframesvc;


--
-- Name: caseevent; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE caseevent FROM PUBLIC;
REVOKE ALL ON TABLE caseevent FROM role_connect;
GRANT ALL ON TABLE caseevent TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE caseevent TO caseframesvc;


--
-- Name: caseidseq; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON SEQUENCE caseidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caseidseq FROM postgres;
GRANT ALL ON SEQUENCE caseidseq TO postgres;
GRANT ALL ON SEQUENCE caseidseq TO caseframesvc;


--
-- Name: casestate; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON TABLE casestate FROM PUBLIC;
REVOKE ALL ON TABLE casestate FROM postgres;
GRANT ALL ON TABLE casestate TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE casestate TO caseframesvc;


--
-- Name: casetype; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE casetype FROM PUBLIC;
REVOKE ALL ON TABLE casetype FROM role_connect;
GRANT ALL ON TABLE casetype TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE casetype TO caseframesvc;


--
-- Name: category; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE category FROM PUBLIC;
REVOKE ALL ON TABLE category FROM role_connect;
GRANT ALL ON TABLE category TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE category TO caseframesvc;


--
-- Name: helpline_mi; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON TABLE helpline_mi FROM PUBLIC;
REVOKE ALL ON TABLE helpline_mi FROM postgres;
GRANT ALL ON TABLE helpline_mi TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE helpline_mi TO caseframesvc;


--
-- Name: messageseq; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON SEQUENCE messageseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE messageseq FROM postgres;
GRANT ALL ON SEQUENCE messageseq TO postgres;
GRANT ALL ON SEQUENCE messageseq TO caseframesvc;


--
-- Name: messagelog; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON TABLE messagelog FROM PUBLIC;
REVOKE ALL ON TABLE messagelog FROM postgres;
GRANT ALL ON TABLE messagelog TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE messagelog TO caseframesvc;


--
-- Name: qidseq; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON SEQUENCE qidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE qidseq FROM postgres;
GRANT ALL ON SEQUENCE qidseq TO postgres;
GRANT ALL ON SEQUENCE qidseq TO caseframesvc;


--
-- Name: questionnaire; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE questionnaire FROM PUBLIC;
REVOKE ALL ON TABLE questionnaire FROM role_connect;
GRANT ALL ON TABLE questionnaire TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE questionnaire TO caseframesvc;


--
-- Name: questionset; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE questionset FROM PUBLIC;
REVOKE ALL ON TABLE questionset FROM role_connect;
GRANT ALL ON TABLE questionset TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE questionset TO caseframesvc;


--
-- Name: responses_by_day; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON TABLE responses_by_day FROM PUBLIC;
REVOKE ALL ON TABLE responses_by_day FROM postgres;
GRANT ALL ON TABLE responses_by_day TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE responses_by_day TO caseframesvc;


--
-- Name: responses_by_perc; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON TABLE responses_by_perc FROM PUBLIC;
REVOKE ALL ON TABLE responses_by_perc FROM postgres;
GRANT ALL ON TABLE responses_by_perc TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE responses_by_perc TO caseframesvc;


--
-- Name: responses_by_sector; Type: ACL; Schema: caseframe; Owner: postgres
--

REVOKE ALL ON TABLE responses_by_sector FROM PUBLIC;
REVOKE ALL ON TABLE responses_by_sector FROM postgres;
GRANT ALL ON TABLE responses_by_sector TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE responses_by_sector TO caseframesvc;


--
-- Name: sample; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE sample FROM PUBLIC;
REVOKE ALL ON TABLE sample FROM role_connect;
GRANT ALL ON TABLE sample TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE sample TO caseframesvc;


--
-- Name: survey; Type: ACL; Schema: caseframe; Owner: role_connect
--

REVOKE ALL ON TABLE survey FROM PUBLIC;
REVOKE ALL ON TABLE survey FROM role_connect;
GRANT ALL ON TABLE survey TO role_connect;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE survey TO caseframesvc;


--
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: caseframe; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA caseframe REVOKE ALL ON SEQUENCES  FROM PUBLIC;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA caseframe REVOKE ALL ON SEQUENCES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA caseframe GRANT ALL ON SEQUENCES  TO caseframesvc;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: caseframe; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA caseframe REVOKE ALL ON TABLES  FROM PUBLIC;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA caseframe REVOKE ALL ON TABLES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA caseframe GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES  TO caseframesvc;



