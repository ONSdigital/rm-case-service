SET  schema 'casesvc';

--
-- TOC entry 453 (class 1255 OID 53945)
-- Name: generate_initial_cases(integer, character varying, character varying); Type: FUNCTION; Schema: casesvc; Owner: postgres
--

CREATE FUNCTION generate_initial_cases(p_sampleid integer, p_geog_area_type character varying, p_geog_area_code character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_address_loop RECORD;
    v_sampleid integer;
    v_geog_select_text character varying;
    v_address_criteria character varying;
    v_casetypeid integer;
    v_actionplanid integer;
    v_questionset character varying;
    v_surveyid integer;
    v_caseid bigint;
    v_sql_text character varying;
    v_caseeventid bigint;
    v_casegroupid bigint;
    v_caseref bigint;
    v_actionplanmappingid integer;
    v_cases_already_generated integer;
    v_number_of_cases integer;
    v_survey character varying(20);
    v_sample character varying(20);
   

BEGIN

--check that have a valid area type passed in

IF p_geog_area_type NOT IN ('MSOA','LA','REGION')
THEN
 RAISE SQLSTATE 'Z0001' using message = p_geog_area_type ||' is not a valid geographic area type. Must be of type MSOA , LA or REGION' ;
END IF;

--When the address criteria is fincalised for 2017 then this should be tidied up

 --SELECT addresscriteria FROM casesvc.sample WHERE sampleid = p_sampleid INTO v_address_criteria;

 SELECT name FROM casesvc.sample WHERE sampleid = p_sampleid INTO v_sample;
 
           SELECT CASE p_geog_area_type
           WHEN 'OA' THEN   'oa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LSOA' THEN  'lsoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'MSOA' THEN 'msoa11cd = ''' || p_geog_area_code ||''''
           WHEN 'LA' THEN 'lad12cd = ''' || p_geog_area_code ||''''
           WHEN 'REGION' THEN 'region11cd = ''' || p_geog_area_code || ''''
          ELSE '0=1' --not a valid area type
       END INTO v_geog_select_text ;


--assign into variables for insert statement


v_sampleid := p_sampleid;

SELECT survey FROM casesvc.sample WHERE sampleid = v_sampleid INTO v_survey;

SELECT name FROM casesvc.sample WHERE sampleid = v_sampleid INTO v_sample;
select casetypeid
from casesvc.samplecasetypeselector
where sampleid = v_sampleid
and respondenttype = 'H' into v_casetypeid;

select actionplanmappingid
from casesvc.actionplanmapping
where casetypeid = v_casetypeid
and isdefault = TRUE
into v_actionplanmappingid;


v_sql_text := 'SELECT uprn FROM casesvc.address where ' || v_geog_select_text || ' and sample = ''' || v_sample || '''';

 FOR v_address_loop IN  EXECUTE v_sql_text LOOP

     v_caseid := nextval('casesvc.caseidseq') ;
     v_caseeventid := nextval('casesvc.caseeventidseq') ;
     v_casegroupid := nextval('casesvc.casegroupidseq') ;
     v_caseref := nextval('casesvc.caserefseq') ;
     

--insert intitial record into casegroup  and case table
   
     INSERT INTO casesvc.casegroup(casegroupid, uprn, sampleid)
     Values ( --nextval('casesvc.caseidseq') 
     v_casegroupid 
    ,v_address_loop.uprn
    ,v_sampleid);



    INSERT INTO casesvc.case( caseid, casegroupid, caseref, state, casetypeid, actionplanmappingid, createddatetime, createdby)
     Values ( v_caseid
    ,v_casegroupid
    ,v_caseref 
    ,'SAMPLED_INIT'
    ,v_casetypeid
    ,v_actionplanmappingid
    ,CURRENT_TIMESTAMP
    ,'SYSTEM');

 --    v_number_of_cases := v_number_of_cases + 1;

  INSERT INTO casesvc.caseevent(
     caseeventid, caseid, description, createdby, createddatetime, category)
     VALUES(v_caseeventid
    ,v_caseid      
    ,'Initial Creation Of Case'
    ,'SYSTEM'
    ,CURRENT_TIMESTAMP
    ,'CASE_CREATED');

        
END LOOP;

    PERFORM casesvc.logmessage(p_messagetext := v_number_of_cases || ' cases generated for sampleid ' || v_sampleid || ' : Area Type ' || p_geog_area_type || ' : Area Code ' || p_geog_area_code
                             ,p_jobid := 0
                             ,p_messagelevel := 'INFO'
                             ,p_functionname := 'casesvc.generate_cases');

RETURN TRUE;

EXCEPTION

  WHEN sqlstate  'Z0001' THEN
       PERFORM casesvc.logmessage(p_messagetext := 'EXCEPTION TRIGGERED ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                               ,p_jobid := 0   
                               ,p_messagelevel := 'WARNING'
                               ,p_functionname := 'casesvc.generate_cases'); 
RETURN FALSE;
                               
  WHEN OTHERS THEN
    PERFORM casesvc.logmessage(p_messagetext := 'GENERATE CASES EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || SQLSTATE
                             ,p_jobid := 0
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'casesvc.generate_cases');

                             
RETURN FALSE;
   
END;
$$;


ALTER FUNCTION casesvc.generate_initial_cases(p_sampleid integer, p_geog_area_type character varying, p_geog_area_code character varying) OWNER TO postgres;

--
-- TOC entry 452 (class 1255 OID 53944)
-- Name: logmessage(text, numeric, text, text); Type: FUNCTION; Schema: casesvc; Owner: postgres
--

CREATE FUNCTION logmessage(p_messagetext text DEFAULT NULL::text, p_jobid numeric DEFAULT NULL::numeric, p_messagelevel text DEFAULT NULL::text, p_functionname text DEFAULT NULL::text) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
v_text TEXT ;
v_function TEXT;
BEGIN
INSERT INTO casesvc.messagelog
(messagetext, jobid, messagelevel, functionname, createddatetime )
values (p_messagetext, p_jobid, p_messagelevel, p_functionname, current_timestamp);
  RETURN TRUE;
EXCEPTION
WHEN OTHERS THEN
RETURN FALSE;
END;
$$;


ALTER FUNCTION casesvc.logmessage(p_messagetext text, p_jobid numeric, p_messagelevel text, p_functionname text) OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 324 (class 1259 OID 53632)
-- Name: actionplanmapping; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE actionplanmapping (
    actionplanmappingid integer NOT NULL,
    actionplanid integer,
    casetypeid integer,
    isdefault boolean,
    inboundchannel character varying(10),
    variant character varying(10),
    outboundchannel character varying(10)
);


ALTER TABLE casesvc.actionplanmapping OWNER TO postgres;

--
-- TOC entry 325 (class 1259 OID 53635)
-- Name: address; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
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
    longitude double precision,
    sample character varying(20),
    casetype character varying(20)
);


ALTER TABLE casesvc.address OWNER TO postgres;

--
-- TOC entry 326 (class 1259 OID 53638)
-- Name: case; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE "case" (
    caseid bigint NOT NULL,
    casegroupid bigint NOT NULL,
    caseref character varying(16),
    state character varying(20),
    casetypeid integer,
    actionplanmappingid integer,
    createddatetime timestamp with time zone,
    createdby character varying(50),
    iac character(24),
    contactid bigint
);


ALTER TABLE casesvc."case" OWNER TO postgres;

--
-- TOC entry 327 (class 1259 OID 53641)
-- Name: caseeventidseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE caseeventidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE casesvc.caseeventidseq OWNER TO postgres;

--
-- TOC entry 328 (class 1259 OID 53643)
-- Name: caseevent; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
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


ALTER TABLE casesvc.caseevent OWNER TO postgres;

--
-- TOC entry 329 (class 1259 OID 53650)
-- Name: casegroup; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE casegroup (
    casegroupid bigint NOT NULL,
    uprn numeric(12,0),
    sampleid integer
);


ALTER TABLE casesvc.casegroup OWNER TO postgres;

--
-- TOC entry 330 (class 1259 OID 53653)
-- Name: casegroupidseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casegroupidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE casesvc.casegroupidseq OWNER TO postgres;

--
-- TOC entry 331 (class 1259 OID 53655)
-- Name: caseidseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE caseidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE casesvc.caseidseq OWNER TO postgres;

--
-- TOC entry 332 (class 1259 OID 53657)
-- Name: caserefseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE caserefseq
    START WITH 1000000000000001
    INCREMENT BY 1
    MINVALUE 1000000000000001
    MAXVALUE 9999999999999999
    CACHE 1;


ALTER TABLE casesvc.caserefseq OWNER TO postgres;

--
-- TOC entry 333 (class 1259 OID 53659)
-- Name: casestate; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE casestate (
    state character varying(20) NOT NULL
);


ALTER TABLE casesvc.casestate OWNER TO postgres;

--
-- TOC entry 334 (class 1259 OID 53662)
-- Name: casetype; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE casetype (
    casetypeid integer NOT NULL,
    name character varying(20),
    description character varying(100),
    questionset character varying(10),
    respondenttype character varying(10)
);


ALTER TABLE casesvc.casetype OWNER TO postgres;

--
-- TOC entry 335 (class 1259 OID 53665)
-- Name: category; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE category (
    name character varying(40) NOT NULL,
    description character varying(50),
    eventtype character varying(20),
    manual boolean,
    role character varying(50),
    generatedactiontype character varying(100),
    "group" character varying(20)
);


ALTER TABLE casesvc.category OWNER TO postgres;

--
-- TOC entry 336 (class 1259 OID 53668)
-- Name: contact; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE contact (
    contactid integer NOT NULL,
    forename character varying(35),
    surname character varying(35),
    phonenumber integer,
    emailaddress character varying(50)
);


ALTER TABLE casesvc.contact OWNER TO postgres;

--
-- TOC entry 337 (class 1259 OID 53671)
-- Name: messageseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE messageseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE casesvc.messageseq OWNER TO postgres;

--
-- TOC entry 338 (class 1259 OID 53673)
-- Name: messagelog; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE messagelog (
    messageid bigint DEFAULT nextval('messageseq'::regclass) NOT NULL,
    messagetext character varying,
    jobid numeric,
    messagelevel character varying,
    functionname character varying,
    createddatetime timestamp with time zone
);


ALTER TABLE casesvc.messagelog OWNER TO postgres;

--
-- TOC entry 339 (class 1259 OID 53680)
-- Name: questionset; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE questionset (
    questionset character varying(10) NOT NULL
);


ALTER TABLE casesvc.questionset OWNER TO postgres;

--
-- TOC entry 340 (class 1259 OID 53683)
-- Name: respondenttype; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE respondenttype (
    respondenttype character varying(10) NOT NULL
);


ALTER TABLE casesvc.respondenttype OWNER TO postgres;

--
-- TOC entry 345 (class 1259 OID 53980)
-- Name: responseidseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE responseidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE casesvc.responseidseq OWNER TO postgres;

--
-- TOC entry 341 (class 1259 OID 53686)
-- Name: response; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE response (
    responseid bigint DEFAULT nextval('responseidseq'::regclass) NOT NULL,
    caseid bigint,
    inboundchannel character varying(10),
    datetime timestamp with time zone
);


ALTER TABLE casesvc.response OWNER TO postgres;

--
-- TOC entry 342 (class 1259 OID 53689)
-- Name: sample; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE sample (
    sampleid integer NOT NULL,
    name character varying(20),
    description character varying(100),
    addresscriteria character varying(100),
    survey character varying(20)
);


ALTER TABLE casesvc.sample OWNER TO postgres;

--
-- TOC entry 343 (class 1259 OID 53692)
-- Name: samplecasetypeselector; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE samplecasetypeselector (
    samplecasetypeselectorid integer NOT NULL,
    sampleid integer,
    casetypeid integer,
    respondenttype character varying(10)
);


ALTER TABLE casesvc.samplecasetypeselector OWNER TO postgres;

--
-- TOC entry 344 (class 1259 OID 53695)
-- Name: survey; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE survey (
    survey character varying(20) NOT NULL
);


ALTER TABLE casesvc.survey OWNER TO postgres;

--
-- TOC entry 3343 (class 2606 OID 53699)
-- Name: actionplanmapping_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY actionplanmapping
    ADD CONSTRAINT actionplanmapping_pkey PRIMARY KEY (actionplanmappingid);


--
-- TOC entry 3347 (class 2606 OID 53701)
-- Name: address_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (uprn);


--
-- TOC entry 3349 (class 2606 OID 53703)
-- Name: case_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (caseid);


--
-- TOC entry 3351 (class 2606 OID 53705)
-- Name: caseevent_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventid);


--
-- TOC entry 3353 (class 2606 OID 53707)
-- Name: casegroup_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT casegroup_pkey PRIMARY KEY (casegroupid);


--
-- TOC entry 3355 (class 2606 OID 53709)
-- Name: casestate_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casestate
    ADD CONSTRAINT casestate_pkey PRIMARY KEY (state);


--
-- TOC entry 3357 (class 2606 OID 53711)
-- Name: casetype_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_pkey PRIMARY KEY (casetypeid);


--
-- TOC entry 3359 (class 2606 OID 53713)
-- Name: category_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (name);


--
-- TOC entry 3361 (class 2606 OID 53715)
-- Name: contact_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY contact
    ADD CONSTRAINT contact_pkey PRIMARY KEY (contactid);


--
-- TOC entry 3363 (class 2606 OID 53717)
-- Name: messageid_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY messagelog
    ADD CONSTRAINT messageid_pkey PRIMARY KEY (messageid);


--
-- TOC entry 3365 (class 2606 OID 53719)
-- Name: questionset_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY questionset
    ADD CONSTRAINT questionset_pkey PRIMARY KEY (questionset);


--
-- TOC entry 3367 (class 2606 OID 53721)
-- Name: respondenttype_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY respondenttype
    ADD CONSTRAINT respondenttype_pkey PRIMARY KEY (respondenttype);


--
-- TOC entry 3369 (class 2606 OID 53723)
-- Name: response_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (responseid);


--
-- TOC entry 3371 (class 2606 OID 53725)
-- Name: sample_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (sampleid);


--
-- TOC entry 3373 (class 2606 OID 53727)
-- Name: samplecasetypeselector_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT samplecasetypeselector_pkey PRIMARY KEY (samplecasetypeselectorid);


--
-- TOC entry 3375 (class 2606 OID 53729)
-- Name: survey_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (survey);


--
-- TOC entry 3344 (class 1259 OID 53730)
-- Name: address_lad12cd_idx; Type: INDEX; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE INDEX address_lad12cd_idx ON address USING btree (lad12cd);


--
-- TOC entry 3345 (class 1259 OID 53731)
-- Name: address_msoa11cd_idx; Type: INDEX; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE INDEX address_msoa11cd_idx ON address USING btree (msoa11cd);


--
-- TOC entry 3381 (class 2606 OID 53732)
-- Name: actionplanmappingid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT actionplanmappingid_fkey FOREIGN KEY (actionplanmappingid) REFERENCES actionplanmapping(actionplanmappingid);


--
-- TOC entry 3380 (class 2606 OID 53737)
-- Name: casegroupid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casegroupid_fkey FOREIGN KEY (casegroupid) REFERENCES casegroup(casegroupid);


--
-- TOC entry 3383 (class 2606 OID 53742)
-- Name: caseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- TOC entry 3388 (class 2606 OID 53747)
-- Name: caseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY response
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- TOC entry 3392 (class 2606 OID 53752)
-- Name: casetyeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT casetyeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3376 (class 2606 OID 53757)
-- Name: casetypeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY actionplanmapping
    ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3379 (class 2606 OID 53762)
-- Name: casetypeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3382 (class 2606 OID 53767)
-- Name: category_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT category_fkey FOREIGN KEY (category) REFERENCES category(name);


--
-- TOC entry 3378 (class 2606 OID 53772)
-- Name: contactid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT contactid_fkey FOREIGN KEY (contactid) REFERENCES contact(contactid);


--
-- TOC entry 3387 (class 2606 OID 53777)
-- Name: questionset_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);


--
-- TOC entry 3391 (class 2606 OID 53782)
-- Name: respondenttype_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT respondenttype_fkey FOREIGN KEY (respondenttype) REFERENCES respondenttype(respondenttype);


--
-- TOC entry 3386 (class 2606 OID 53787)
-- Name: respondenttype_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT respondenttype_fkey FOREIGN KEY (respondenttype) REFERENCES respondenttype(respondenttype);


--
-- TOC entry 3385 (class 2606 OID 53792)
-- Name: sampleid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);


--
-- TOC entry 3390 (class 2606 OID 53797)
-- Name: sampleid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);


--
-- TOC entry 3377 (class 2606 OID 53802)
-- Name: state_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT state_fkey FOREIGN KEY (state) REFERENCES casestate(state);


--
-- TOC entry 3389 (class 2606 OID 53807)
-- Name: survey_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT survey_fkey FOREIGN KEY (survey) REFERENCES survey(survey);


--
-- TOC entry 3384 (class 2606 OID 53812)
-- Name: uprn_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT uprn_fkey FOREIGN KEY (uprn) REFERENCES address(uprn);


--
-- TOC entry 3512 (class 0 OID 0)
-- Dependencies: 23
-- Name: casesvc; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA casesvc FROM PUBLIC;
REVOKE ALL ON SCHEMA casesvc FROM postgres;
GRANT ALL ON SCHEMA casesvc TO postgres;
GRANT ALL ON SCHEMA casesvc TO role_connect;


--
-- TOC entry 3513 (class 0 OID 0)
-- Dependencies: 324
-- Name: actionplanmapping; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE actionplanmapping FROM PUBLIC;
REVOKE ALL ON TABLE actionplanmapping FROM postgres;
GRANT ALL ON TABLE actionplanmapping TO postgres;


--
-- TOC entry 3514 (class 0 OID 0)
-- Dependencies: 325
-- Name: address; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE address FROM PUBLIC;
REVOKE ALL ON TABLE address FROM postgres;
GRANT ALL ON TABLE address TO postgres;


--
-- TOC entry 3515 (class 0 OID 0)
-- Dependencies: 326
-- Name: case; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE "case" FROM PUBLIC;
REVOKE ALL ON TABLE "case" FROM postgres;
GRANT ALL ON TABLE "case" TO postgres;


--
-- TOC entry 3516 (class 0 OID 0)
-- Dependencies: 327
-- Name: caseeventidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caseeventidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caseeventidseq FROM postgres;
GRANT ALL ON SEQUENCE caseeventidseq TO postgres;


--
-- TOC entry 3517 (class 0 OID 0)
-- Dependencies: 328
-- Name: caseevent; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE caseevent FROM PUBLIC;
REVOKE ALL ON TABLE caseevent FROM postgres;
GRANT ALL ON TABLE caseevent TO postgres;


--
-- TOC entry 3518 (class 0 OID 0)
-- Dependencies: 329
-- Name: casegroup; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casegroup FROM PUBLIC;
REVOKE ALL ON TABLE casegroup FROM postgres;
GRANT ALL ON TABLE casegroup TO postgres;


--
-- TOC entry 3519 (class 0 OID 0)
-- Dependencies: 330
-- Name: casegroupidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE casegroupidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE casegroupidseq FROM postgres;
GRANT ALL ON SEQUENCE casegroupidseq TO postgres;


--
-- TOC entry 3520 (class 0 OID 0)
-- Dependencies: 331
-- Name: caseidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caseidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caseidseq FROM postgres;
GRANT ALL ON SEQUENCE caseidseq TO postgres;


--
-- TOC entry 3521 (class 0 OID 0)
-- Dependencies: 332
-- Name: caserefseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caserefseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caserefseq FROM postgres;
GRANT ALL ON SEQUENCE caserefseq TO postgres;


--
-- TOC entry 3522 (class 0 OID 0)
-- Dependencies: 333
-- Name: casestate; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casestate FROM PUBLIC;
REVOKE ALL ON TABLE casestate FROM postgres;
GRANT ALL ON TABLE casestate TO postgres;


--
-- TOC entry 3523 (class 0 OID 0)
-- Dependencies: 334
-- Name: casetype; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casetype FROM PUBLIC;
REVOKE ALL ON TABLE casetype FROM postgres;
GRANT ALL ON TABLE casetype TO postgres;


--
-- TOC entry 3524 (class 0 OID 0)
-- Dependencies: 335
-- Name: category; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE category FROM PUBLIC;
REVOKE ALL ON TABLE category FROM postgres;
GRANT ALL ON TABLE category TO postgres;


--
-- TOC entry 3525 (class 0 OID 0)
-- Dependencies: 336
-- Name: contact; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE contact FROM PUBLIC;
REVOKE ALL ON TABLE contact FROM postgres;
GRANT ALL ON TABLE contact TO postgres;


--
-- TOC entry 3526 (class 0 OID 0)
-- Dependencies: 337
-- Name: messageseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE messageseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE messageseq FROM postgres;
GRANT ALL ON SEQUENCE messageseq TO postgres;


--
-- TOC entry 3527 (class 0 OID 0)
-- Dependencies: 338
-- Name: messagelog; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE messagelog FROM PUBLIC;
REVOKE ALL ON TABLE messagelog FROM postgres;
GRANT ALL ON TABLE messagelog TO postgres;


--
-- TOC entry 3528 (class 0 OID 0)
-- Dependencies: 339
-- Name: questionset; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE questionset FROM PUBLIC;
REVOKE ALL ON TABLE questionset FROM postgres;
GRANT ALL ON TABLE questionset TO postgres;


--
-- TOC entry 3529 (class 0 OID 0)
-- Dependencies: 345
-- Name: responseidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE responseidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE responseidseq FROM postgres;
GRANT ALL ON SEQUENCE responseidseq TO postgres;


--
-- TOC entry 3530 (class 0 OID 0)
-- Dependencies: 341
-- Name: response; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE response FROM PUBLIC;
REVOKE ALL ON TABLE response FROM postgres;
GRANT ALL ON TABLE response TO postgres;


--
-- TOC entry 3531 (class 0 OID 0)
-- Dependencies: 342
-- Name: sample; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE sample FROM PUBLIC;
REVOKE ALL ON TABLE sample FROM postgres;
GRANT ALL ON TABLE sample TO postgres;


--
-- TOC entry 3532 (class 0 OID 0)
-- Dependencies: 344
-- Name: survey; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE survey FROM PUBLIC;
REVOKE ALL ON TABLE survey FROM postgres;
GRANT ALL ON TABLE survey TO postgres;


-- Completed on 2016-10-10 09:38:35 BST

--
-- PostgreSQL database dump complete
--

