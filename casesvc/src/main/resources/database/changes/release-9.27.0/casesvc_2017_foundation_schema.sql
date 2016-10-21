set schema 'casesvc';

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
    v_sql_text character varying;
    v_caseeventid bigint;
    v_casegroupid bigint;
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
     v_caseeventid := nextval('casesvc.caseeventidseq') ;
     v_casegroupid := nextval('casesvc.casegroupidseq') ;
     

--insert intitial record into casegroup  and case table
   
     INSERT INTO casesvc.casegroup(casegroupid, uprn, sampleid)
     Values (
     v_casegroupid 
    ,v_address_loop.uprn
    ,v_sampleid);


    INSERT INTO casesvc.case(casegroupid, state, casetypeid, actionplanmappingid, createddatetime, createdby)
     Values ( 
     v_casegroupid
    ,'SAMPLED_INIT'
    ,v_casetypeid
    ,v_actionplanmappingid
    ,CURRENT_TIMESTAMP
    ,'SYSTEM');

 --    v_number_of_cases := v_number_of_cases + 1;

  INSERT INTO casesvc.caseevent(
     caseeventid, caseid, description, createdby, createddatetime, category)
     VALUES(v_caseeventid
    ,currval('casesvc.caseidseq')   
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
-- TOC entry 492 (class 1255 OID 55719)
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

--
-- TOC entry 359 (class 1259 OID 55720)
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
-- TOC entry 360 (class 1259 OID 55723)
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
-- TOC entry 366 (class 1259 OID 55743)
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
-- TOC entry 367 (class 1259 OID 55745)
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
-- TOC entry 361 (class 1259 OID 55726)
-- Name: case; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE "case" (
    caseid bigint DEFAULT nextval('caseidseq'::regclass) NOT NULL,
    casegroupid bigint NOT NULL,
    caseref character varying(16) DEFAULT nextval('caserefseq'::regclass),
    state character varying(20),
    casetypeid integer,
    actionplanmappingid integer,
    createddatetime timestamp with time zone,
    createdby character varying(50),
    iac character(24),
    contactid bigint,
    sourcecaseid bigint
);


ALTER TABLE casesvc."case" OWNER TO postgres;

--
-- TOC entry 362 (class 1259 OID 55729)
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
-- TOC entry 363 (class 1259 OID 55731)
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
-- TOC entry 364 (class 1259 OID 55738)
-- Name: casegroup; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE casegroup (
    casegroupid bigint NOT NULL,
    uprn numeric(12,0),
    sampleid integer
);


ALTER TABLE casesvc.casegroup OWNER TO postgres;

--
-- TOC entry 365 (class 1259 OID 55741)
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
-- TOC entry 368 (class 1259 OID 55747)
-- Name: casestate; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE casestate (
    state character varying(20) NOT NULL
);


ALTER TABLE casesvc.casestate OWNER TO postgres;

--
-- TOC entry 369 (class 1259 OID 55750)
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
-- TOC entry 370 (class 1259 OID 55753)
-- Name: category; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE category (
    name character varying(40) NOT NULL,
    longdescription character varying(50),
    eventtype character varying(20),
    manual boolean,
    role character varying(100),
    generatedactiontype character varying(100),
    "group" character varying(20),
    shortdescription character varying(50),
    newcaserespondenttype character varying(10),
    oldcaserespondenttype character varying(10)
);


ALTER TABLE casesvc.category OWNER TO postgres;

--
-- TOC entry 382 (class 1259 OID 55918)
-- Name: contactidseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE contactidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE casesvc.contactidseq OWNER TO postgres;

--
-- TOC entry 371 (class 1259 OID 55756)
-- Name: contact; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE contact (
    contactid integer DEFAULT nextval('contactidseq'::regclass) NOT NULL,
    forename character varying(35),
    surname character varying(35),
    phonenumber character varying(20),
    emailaddress character varying(50),
    title character varying(20)
);


ALTER TABLE casesvc.contact OWNER TO postgres;

--
-- TOC entry 372 (class 1259 OID 55759)
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
-- TOC entry 373 (class 1259 OID 55761)
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
-- TOC entry 374 (class 1259 OID 55768)
-- Name: questionset; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE questionset (
    questionset character varying(10) NOT NULL
);


ALTER TABLE casesvc.questionset OWNER TO postgres;

--
-- TOC entry 375 (class 1259 OID 55771)
-- Name: respondenttype; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE respondenttype (
    respondenttype character varying(10) NOT NULL
);


ALTER TABLE casesvc.respondenttype OWNER TO postgres;

--
-- TOC entry 376 (class 1259 OID 55774)
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
-- TOC entry 377 (class 1259 OID 55776)
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
-- TOC entry 378 (class 1259 OID 55780)
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
-- TOC entry 379 (class 1259 OID 55783)
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
-- TOC entry 380 (class 1259 OID 55786)
-- Name: survey; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE survey (
    survey character varying(20) NOT NULL
);


ALTER TABLE casesvc.survey OWNER TO postgres;

--
-- TOC entry 381 (class 1259 OID 55908)
-- Name: unlinkedcasereceipt; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE unlinkedcasereceipt (
    caseref character varying(16) NOT NULL,
    inboundchannel character varying(10),
    responsedatetime timestamp with time zone
);


ALTER TABLE casesvc.unlinkedcasereceipt OWNER TO postgres;

--
-- TOC entry 3452 (class 2606 OID 55790)
-- Name: actionplanmapping_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY actionplanmapping
    ADD CONSTRAINT actionplanmapping_pkey PRIMARY KEY (actionplanmappingid);


--
-- TOC entry 3456 (class 2606 OID 55792)
-- Name: address_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (uprn);


--
-- TOC entry 3458 (class 2606 OID 55794)
-- Name: case_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (caseid);


--
-- TOC entry 3460 (class 2606 OID 55796)
-- Name: caseevent_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventid);


--
-- TOC entry 3462 (class 2606 OID 55798)
-- Name: casegroup_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT casegroup_pkey PRIMARY KEY (casegroupid);


--
-- TOC entry 3464 (class 2606 OID 55800)
-- Name: casestate_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casestate
    ADD CONSTRAINT casestate_pkey PRIMARY KEY (state);


--
-- TOC entry 3466 (class 2606 OID 55802)
-- Name: casetype_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_pkey PRIMARY KEY (casetypeid);


--
-- TOC entry 3468 (class 2606 OID 55804)
-- Name: category_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (name);


--
-- TOC entry 3470 (class 2606 OID 55806)
-- Name: contact_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY contact
    ADD CONSTRAINT contact_pkey PRIMARY KEY (contactid);


--
-- TOC entry 3472 (class 2606 OID 55808)
-- Name: messageid_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY messagelog
    ADD CONSTRAINT messageid_pkey PRIMARY KEY (messageid);


--
-- TOC entry 3474 (class 2606 OID 55810)
-- Name: questionset_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY questionset
    ADD CONSTRAINT questionset_pkey PRIMARY KEY (questionset);


--
-- TOC entry 3476 (class 2606 OID 55812)
-- Name: respondenttype_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY respondenttype
    ADD CONSTRAINT respondenttype_pkey PRIMARY KEY (respondenttype);


--
-- TOC entry 3478 (class 2606 OID 55814)
-- Name: response_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (responseid);


--
-- TOC entry 3480 (class 2606 OID 55816)
-- Name: sample_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (sampleid);


--
-- TOC entry 3482 (class 2606 OID 55818)
-- Name: samplecasetypeselector_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT samplecasetypeselector_pkey PRIMARY KEY (samplecasetypeselectorid);


--
-- TOC entry 3484 (class 2606 OID 55820)
-- Name: survey_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (survey);


--
-- TOC entry 3453 (class 1259 OID 55821)
-- Name: address_lad12cd_idx; Type: INDEX; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE INDEX address_lad12cd_idx ON address USING btree (lad12cd);


--
-- TOC entry 3454 (class 1259 OID 55822)
-- Name: address_msoa11cd_idx; Type: INDEX; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE INDEX address_msoa11cd_idx ON address USING btree (msoa11cd);


--
-- TOC entry 3491 (class 2606 OID 55823)
-- Name: actionplanmappingid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT actionplanmappingid_fkey FOREIGN KEY (actionplanmappingid) REFERENCES actionplanmapping(actionplanmappingid);


--
-- TOC entry 3490 (class 2606 OID 55828)
-- Name: casegroupid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casegroupid_fkey FOREIGN KEY (casegroupid) REFERENCES casegroup(casegroupid);


--
-- TOC entry 3493 (class 2606 OID 55833)
-- Name: caseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- TOC entry 3500 (class 2606 OID 55838)
-- Name: caseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY response
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- TOC entry 3504 (class 2606 OID 55843)
-- Name: casetyeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT casetyeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3485 (class 2606 OID 55848)
-- Name: casetypeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY actionplanmapping
    ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3489 (class 2606 OID 55853)
-- Name: casetypeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3492 (class 2606 OID 55928)
-- Name: category_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT category_fkey FOREIGN KEY (category) REFERENCES category(name);


--
-- TOC entry 3488 (class 2606 OID 55863)
-- Name: contactid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT contactid_fkey FOREIGN KEY (contactid) REFERENCES contact(contactid);


--
-- TOC entry 3499 (class 2606 OID 55923)
-- Name: newcaserespondenttype_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY category
    ADD CONSTRAINT newcaserespondenttype_fkey FOREIGN KEY (newcaserespondenttype) REFERENCES respondenttype(respondenttype);


--
-- TOC entry 3498 (class 2606 OID 55945)
-- Name: oldcaserespondenttype_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY category
    ADD CONSTRAINT oldcaserespondenttype_fkey FOREIGN KEY (oldcaserespondenttype) REFERENCES respondenttype(respondenttype);


--
-- TOC entry 3497 (class 2606 OID 55868)
-- Name: questionset_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);


--
-- TOC entry 3503 (class 2606 OID 55873)
-- Name: respondenttype_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT respondenttype_fkey FOREIGN KEY (respondenttype) REFERENCES respondenttype(respondenttype);


--
-- TOC entry 3496 (class 2606 OID 55878)
-- Name: respondenttype_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT respondenttype_fkey FOREIGN KEY (respondenttype) REFERENCES respondenttype(respondenttype);


--
-- TOC entry 3495 (class 2606 OID 55883)
-- Name: sampleid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);


--
-- TOC entry 3502 (class 2606 OID 55888)
-- Name: sampleid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);


--
-- TOC entry 3486 (class 2606 OID 55940)
-- Name: sourcecaseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT sourcecaseid_fkey FOREIGN KEY (sourcecaseid) REFERENCES "case"(caseid);


--
-- TOC entry 3487 (class 2606 OID 55893)
-- Name: state_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT state_fkey FOREIGN KEY (state) REFERENCES casestate(state);


--
-- TOC entry 3501 (class 2606 OID 55898)
-- Name: survey_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT survey_fkey FOREIGN KEY (survey) REFERENCES survey(survey);


--
-- TOC entry 3494 (class 2606 OID 55903)
-- Name: uprn_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT uprn_fkey FOREIGN KEY (uprn) REFERENCES address(uprn);


--
-- TOC entry 3624 (class 0 OID 0)
-- Dependencies: 27
-- Name: casesvc; Type: ACL; Schema: -; Owner: role_connect
--

REVOKE ALL ON SCHEMA casesvc FROM PUBLIC;
REVOKE ALL ON SCHEMA casesvc FROM role_connect;
GRANT ALL ON SCHEMA casesvc TO role_connect;
GRANT ALL ON SCHEMA casesvc TO casesvc;
GRANT ALL ON SCHEMA casesvc TO postgres;


--
-- TOC entry 3625 (class 0 OID 0)
-- Dependencies: 359
-- Name: actionplanmapping; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE actionplanmapping FROM PUBLIC;
REVOKE ALL ON TABLE actionplanmapping FROM postgres;
GRANT ALL ON TABLE actionplanmapping TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE actionplanmapping TO casesvc;


--
-- TOC entry 3626 (class 0 OID 0)
-- Dependencies: 360
-- Name: address; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE address FROM PUBLIC;
REVOKE ALL ON TABLE address FROM postgres;
GRANT ALL ON TABLE address TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE address TO casesvc;


--
-- TOC entry 3627 (class 0 OID 0)
-- Dependencies: 366
-- Name: caseidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caseidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caseidseq FROM postgres;
GRANT ALL ON SEQUENCE caseidseq TO postgres;
GRANT ALL ON SEQUENCE caseidseq TO casesvc;


--
-- TOC entry 3628 (class 0 OID 0)
-- Dependencies: 367
-- Name: caserefseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caserefseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caserefseq FROM postgres;
GRANT ALL ON SEQUENCE caserefseq TO postgres;
GRANT ALL ON SEQUENCE caserefseq TO casesvc;


--
-- TOC entry 3629 (class 0 OID 0)
-- Dependencies: 361
-- Name: case; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE "case" FROM PUBLIC;
REVOKE ALL ON TABLE "case" FROM postgres;
GRANT ALL ON TABLE "case" TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE "case" TO casesvc;


--
-- TOC entry 3630 (class 0 OID 0)
-- Dependencies: 362
-- Name: caseeventidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caseeventidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caseeventidseq FROM postgres;
GRANT ALL ON SEQUENCE caseeventidseq TO postgres;
GRANT ALL ON SEQUENCE caseeventidseq TO casesvc;


--
-- TOC entry 3631 (class 0 OID 0)
-- Dependencies: 363
-- Name: caseevent; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE caseevent FROM PUBLIC;
REVOKE ALL ON TABLE caseevent FROM postgres;
GRANT ALL ON TABLE caseevent TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE caseevent TO casesvc;


--
-- TOC entry 3632 (class 0 OID 0)
-- Dependencies: 364
-- Name: casegroup; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casegroup FROM PUBLIC;
REVOKE ALL ON TABLE casegroup FROM postgres;
GRANT ALL ON TABLE casegroup TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE casegroup TO casesvc;


--
-- TOC entry 3633 (class 0 OID 0)
-- Dependencies: 365
-- Name: casegroupidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE casegroupidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE casegroupidseq FROM postgres;
GRANT ALL ON SEQUENCE casegroupidseq TO postgres;
GRANT ALL ON SEQUENCE casegroupidseq TO casesvc;


--
-- TOC entry 3634 (class 0 OID 0)
-- Dependencies: 368
-- Name: casestate; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casestate FROM PUBLIC;
REVOKE ALL ON TABLE casestate FROM postgres;
GRANT ALL ON TABLE casestate TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE casestate TO casesvc;


--
-- TOC entry 3635 (class 0 OID 0)
-- Dependencies: 369
-- Name: casetype; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casetype FROM PUBLIC;
REVOKE ALL ON TABLE casetype FROM postgres;
GRANT ALL ON TABLE casetype TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE casetype TO casesvc;


--
-- TOC entry 3636 (class 0 OID 0)
-- Dependencies: 370
-- Name: category; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE category FROM PUBLIC;
REVOKE ALL ON TABLE category FROM postgres;
GRANT ALL ON TABLE category TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE category TO casesvc;


--
-- TOC entry 3637 (class 0 OID 0)
-- Dependencies: 382
-- Name: contactidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE contactidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE contactidseq FROM postgres;
GRANT ALL ON SEQUENCE contactidseq TO postgres;
GRANT ALL ON SEQUENCE contactidseq TO casesvc;


--
-- TOC entry 3638 (class 0 OID 0)
-- Dependencies: 371
-- Name: contact; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE contact FROM PUBLIC;
REVOKE ALL ON TABLE contact FROM postgres;
GRANT ALL ON TABLE contact TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE contact TO casesvc;


--
-- TOC entry 3639 (class 0 OID 0)
-- Dependencies: 372
-- Name: messageseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE messageseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE messageseq FROM postgres;
GRANT ALL ON SEQUENCE messageseq TO postgres;
GRANT ALL ON SEQUENCE messageseq TO casesvc;


--
-- TOC entry 3640 (class 0 OID 0)
-- Dependencies: 373
-- Name: messagelog; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE messagelog FROM PUBLIC;
REVOKE ALL ON TABLE messagelog FROM postgres;
GRANT ALL ON TABLE messagelog TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE messagelog TO casesvc;


--
-- TOC entry 3641 (class 0 OID 0)
-- Dependencies: 374
-- Name: questionset; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE questionset FROM PUBLIC;
REVOKE ALL ON TABLE questionset FROM postgres;
GRANT ALL ON TABLE questionset TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE questionset TO casesvc;


--
-- TOC entry 3642 (class 0 OID 0)
-- Dependencies: 375
-- Name: respondenttype; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE respondenttype FROM PUBLIC;
REVOKE ALL ON TABLE respondenttype FROM postgres;
GRANT ALL ON TABLE respondenttype TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE respondenttype TO casesvc;


--
-- TOC entry 3643 (class 0 OID 0)
-- Dependencies: 376
-- Name: responseidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE responseidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE responseidseq FROM postgres;
GRANT ALL ON SEQUENCE responseidseq TO postgres;
GRANT ALL ON SEQUENCE responseidseq TO casesvc;


--
-- TOC entry 3644 (class 0 OID 0)
-- Dependencies: 377
-- Name: response; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE response FROM PUBLIC;
REVOKE ALL ON TABLE response FROM postgres;
GRANT ALL ON TABLE response TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE response TO casesvc;


--
-- TOC entry 3645 (class 0 OID 0)
-- Dependencies: 378
-- Name: sample; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE sample FROM PUBLIC;
REVOKE ALL ON TABLE sample FROM postgres;
GRANT ALL ON TABLE sample TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE sample TO casesvc;


--
-- TOC entry 3646 (class 0 OID 0)
-- Dependencies: 379
-- Name: samplecasetypeselector; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE samplecasetypeselector FROM PUBLIC;
REVOKE ALL ON TABLE samplecasetypeselector FROM postgres;
GRANT ALL ON TABLE samplecasetypeselector TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE samplecasetypeselector TO casesvc;


--
-- TOC entry 3647 (class 0 OID 0)
-- Dependencies: 380
-- Name: survey; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE survey FROM PUBLIC;
REVOKE ALL ON TABLE survey FROM postgres;
GRANT ALL ON TABLE survey TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE survey TO casesvc;


--
-- TOC entry 3648 (class 0 OID 0)
-- Dependencies: 381
-- Name: unlinkedcasereceipt; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE unlinkedcasereceipt FROM PUBLIC;
REVOKE ALL ON TABLE unlinkedcasereceipt FROM postgres;
GRANT ALL ON TABLE unlinkedcasereceipt TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE unlinkedcasereceipt TO casesvc;


--
-- TOC entry 2194 (class 826 OID 55705)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: casesvc; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA casesvc REVOKE ALL ON SEQUENCES  FROM PUBLIC;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA casesvc REVOKE ALL ON SEQUENCES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA casesvc GRANT ALL ON SEQUENCES  TO casesvc;


--
-- TOC entry 2193 (class 826 OID 55704)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: casesvc; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA casesvc REVOKE ALL ON TABLES  FROM PUBLIC;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA casesvc REVOKE ALL ON TABLES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA casesvc GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES  TO casesvc;
