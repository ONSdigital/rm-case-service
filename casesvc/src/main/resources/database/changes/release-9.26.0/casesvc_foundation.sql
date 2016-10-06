
SET SCHEMA 'casesvc';


--
-- TOC entry 371 (class 1259 OID 45146)
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
-- TOC entry 362 (class 1259 OID 45031)
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
-- TOC entry 372 (class 1259 OID 45271)
-- Name: case; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE "case" (
    caseid bigint NOT NULL,
    casegroupid bigint NOT NULL,
    caseref varchar(16),
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
-- TOC entry 363 (class 1259 OID 45037)
-- Name: caseeventidseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE caseeventidseq
    START WITH 762
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE casesvc.caseeventidseq OWNER TO postgres;

--
-- TOC entry 373 (class 1259 OID 45285)
-- Name: caseevent; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE caseevent (
    caseeventid bigint DEFAULT nextval('caseeventidseq'::regclass) NOT NULL,
    caseid bigint NOT NULL,
    description character varying(350),
    createdby character varying(50),
    createddatetime timestamp with time zone,
    categoryid integer,
    subcategory character varying(100)
);


ALTER TABLE casesvc.caseevent OWNER TO postgres;

--
-- TOC entry 364 (class 1259 OID 45046)
-- Name: casegroup; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE casegroup (
    casegroupid bigint NOT NULL,
    uprn numeric(12,0),
    sampleid integer
);


ALTER TABLE casesvc.casegroup OWNER TO postgres;

--
-- TOC entry 365 (class 1259 OID 45049)
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
-- TOC entry 366 (class 1259 OID 45051)
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
-- TOC entry 378 (class 1259 OID 45327)
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
-- TOC entry 374 (class 1259 OID 45294)
-- Name: casestate; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE casestate (
    state character varying(20) NOT NULL
);


ALTER TABLE casesvc.casestate OWNER TO postgres;

--
-- TOC entry 379 (class 1259 OID 45334)
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
-- TOC entry 375 (class 1259 OID 45304)
-- Name: category; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE category (
    categoryid integer NOT NULL,
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
-- TOC entry 367 (class 1259 OID 45062)
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
-- TOC entry 368 (class 1259 OID 45072)
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
-- TOC entry 369 (class 1259 OID 45074)
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
-- TOC entry 376 (class 1259 OID 45309)
-- Name: questionset; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE questionset (
    questionset character varying(10) NOT NULL
);


ALTER TABLE casesvc.questionset OWNER TO postgres;

--
-- TOC entry 381 (class 1259 OID 45441)
-- Name: respondenttype; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE respondenttype (
    respondenttype character varying(10) NOT NULL
);


ALTER TABLE casesvc.respondenttype OWNER TO postgres;

--
-- TOC entry 370 (class 1259 OID 45139)
-- Name: response; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE response (
    responseid bigint NOT NULL,
    caseid bigint,
    inboundchannel character varying(10),
    datetime timestamp with time zone
);


ALTER TABLE casesvc.response OWNER TO postgres;

--
-- TOC entry 377 (class 1259 OID 45317)
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
-- TOC entry 382 (class 1259 OID 45446)
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
-- TOC entry 380 (class 1259 OID 45416)
-- Name: survey; Type: TABLE; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE TABLE survey (
    survey character varying(20) NOT NULL
);


ALTER TABLE casesvc.survey OWNER TO postgres;

--
-- TOC entry 3497 (class 2606 OID 45150)
-- Name: actionplanmapping_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY actionplanmapping
    ADD CONSTRAINT actionplanmapping_pkey PRIMARY KEY (actionplanmappingid);


--
-- TOC entry 3487 (class 2606 OID 45104)
-- Name: address_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (uprn);


--
-- TOC entry 3499 (class 2606 OID 45275)
-- Name: case_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (caseid);


--
-- TOC entry 3501 (class 2606 OID 45293)
-- Name: caseevent_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventid);


--
-- TOC entry 3489 (class 2606 OID 45345)
-- Name: casegroup_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT casegroup_pkey PRIMARY KEY (casegroupid);


--
-- TOC entry 3503 (class 2606 OID 45298)
-- Name: casestate_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casestate
    ADD CONSTRAINT casestate_pkey PRIMARY KEY (state);


--
-- TOC entry 3511 (class 2606 OID 45338)
-- Name: casetype_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_pkey PRIMARY KEY (casetypeid);


--
-- TOC entry 3505 (class 2606 OID 45308)
-- Name: category_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (categoryid);


--
-- TOC entry 3491 (class 2606 OID 45116)
-- Name: contact_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY contact
    ADD CONSTRAINT contact_pkey PRIMARY KEY (contactid);


--
-- TOC entry 3493 (class 2606 OID 45118)
-- Name: messageid_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY messagelog
    ADD CONSTRAINT messageid_pkey PRIMARY KEY (messageid);


--
-- TOC entry 3507 (class 2606 OID 45313)
-- Name: questionset_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY questionset
    ADD CONSTRAINT questionset_pkey PRIMARY KEY (questionset);


--
-- TOC entry 3515 (class 2606 OID 45445)
-- Name: respondenttype_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY respondenttype
    ADD CONSTRAINT respondenttype_pkey PRIMARY KEY (respondenttype);


--
-- TOC entry 3495 (class 2606 OID 45409)
-- Name: response_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (responseid);


--
-- TOC entry 3509 (class 2606 OID 45321)
-- Name: sample_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (sampleid);


--
-- TOC entry 3517 (class 2606 OID 45450)
-- Name: samplecasetypeselector_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT samplecasetypeselector_pkey PRIMARY KEY (samplecasetypeselectorid);


--
-- TOC entry 3513 (class 2606 OID 45420)
-- Name: survey_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (survey);


--
-- TOC entry 3484 (class 1259 OID 45125)
-- Name: address_lad12cd_idx; Type: INDEX; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE INDEX address_lad12cd_idx ON address USING btree (lad12cd);


--
-- TOC entry 3485 (class 1259 OID 45126)
-- Name: address_msoa11cd_idx; Type: INDEX; Schema: casesvc; Owner: postgres; Tablespace: 
--

CREATE INDEX address_msoa11cd_idx ON address USING btree (msoa11cd);


--
-- TOC entry 3525 (class 2606 OID 45361)
-- Name: actionplanmappingid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT actionplanmappingid_fkey FOREIGN KEY (actionplanmappingid) REFERENCES actionplanmapping(actionplanmappingid);


--
-- TOC entry 3522 (class 2606 OID 45346)
-- Name: casegroupid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casegroupid_fkey FOREIGN KEY (casegroupid) REFERENCES casegroup(casegroupid);


--
-- TOC entry 3527 (class 2606 OID 45371)
-- Name: caseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- TOC entry 3520 (class 2606 OID 45410)
-- Name: caseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY response
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- TOC entry 3532 (class 2606 OID 45451)
-- Name: casetyeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT casetyeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3521 (class 2606 OID 45339)
-- Name: casetypeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY actionplanmapping
    ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3524 (class 2606 OID 45356)
-- Name: casetypeid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);


--
-- TOC entry 3528 (class 2606 OID 45376)
-- Name: categoryid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT categoryid_fkey FOREIGN KEY (categoryid) REFERENCES category(categoryid);


--
-- TOC entry 3526 (class 2606 OID 45366)
-- Name: contactid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT contactid_fkey FOREIGN KEY (contactid) REFERENCES contact(contactid);


--
-- TOC entry 3530 (class 2606 OID 45396)
-- Name: questionset_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);


--
-- TOC entry 3534 (class 2606 OID 45461)
-- Name: respondenttype_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT respondenttype_fkey FOREIGN KEY (respondenttype) REFERENCES respondenttype(respondenttype);


--
-- TOC entry 3531 (class 2606 OID 45466)
-- Name: respondenttype_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casetype
    ADD CONSTRAINT respondenttype_fkey FOREIGN KEY (respondenttype) REFERENCES respondenttype(respondenttype);


--
-- TOC entry 3519 (class 2606 OID 45386)
-- Name: sampleid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);


--
-- TOC entry 3533 (class 2606 OID 45456)
-- Name: sampleid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);


--
-- TOC entry 3523 (class 2606 OID 45351)
-- Name: state_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT state_fkey FOREIGN KEY (state) REFERENCES casestate(state);


--
-- TOC entry 3529 (class 2606 OID 45421)
-- Name: survey_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT survey_fkey FOREIGN KEY (survey) REFERENCES survey(survey);


--
-- TOC entry 3518 (class 2606 OID 45381)
-- Name: uprn_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT uprn_fkey FOREIGN KEY (uprn) REFERENCES address(uprn);


--
-- TOC entry 3661 (class 0 OID 0)
-- Dependencies: 30
-- Name: casesvc; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA casesvc FROM PUBLIC;
REVOKE ALL ON SCHEMA casesvc FROM postgres;
GRANT ALL ON SCHEMA casesvc TO postgres;
GRANT ALL ON SCHEMA casesvc TO role_connect;


--
-- TOC entry 3662 (class 0 OID 0)
-- Dependencies: 371
-- Name: actionplanmapping; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE actionplanmapping FROM PUBLIC;
REVOKE ALL ON TABLE actionplanmapping FROM postgres;
GRANT ALL ON TABLE actionplanmapping TO postgres;


--
-- TOC entry 3663 (class 0 OID 0)
-- Dependencies: 362
-- Name: address; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE address FROM PUBLIC;
REVOKE ALL ON TABLE address FROM postgres;
GRANT ALL ON TABLE address TO postgres;


--
-- TOC entry 3664 (class 0 OID 0)
-- Dependencies: 372
-- Name: case; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE "case" FROM PUBLIC;
REVOKE ALL ON TABLE "case" FROM postgres;
GRANT ALL ON TABLE "case" TO postgres;


--
-- TOC entry 3665 (class 0 OID 0)
-- Dependencies: 363
-- Name: caseeventidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caseeventidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caseeventidseq FROM postgres;
GRANT ALL ON SEQUENCE caseeventidseq TO postgres;


--
-- TOC entry 3666 (class 0 OID 0)
-- Dependencies: 373
-- Name: caseevent; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE caseevent FROM PUBLIC;
REVOKE ALL ON TABLE caseevent FROM postgres;
GRANT ALL ON TABLE caseevent TO postgres;


--
-- TOC entry 3667 (class 0 OID 0)
-- Dependencies: 364
-- Name: casegroup; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casegroup FROM PUBLIC;
REVOKE ALL ON TABLE casegroup FROM postgres;
GRANT ALL ON TABLE casegroup TO postgres;


--
-- TOC entry 3668 (class 0 OID 0)
-- Dependencies: 365
-- Name: casegroupidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE casegroupidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE casegroupidseq FROM postgres;
GRANT ALL ON SEQUENCE casegroupidseq TO postgres;


--
-- TOC entry 3669 (class 0 OID 0)
-- Dependencies: 366
-- Name: caseidseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caseidseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caseidseq FROM postgres;
GRANT ALL ON SEQUENCE caseidseq TO postgres;


--
-- TOC entry 3670 (class 0 OID 0)
-- Dependencies: 378
-- Name: caserefseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE caserefseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE caserefseq FROM postgres;
GRANT ALL ON SEQUENCE caserefseq TO postgres;


--
-- TOC entry 3671 (class 0 OID 0)
-- Dependencies: 374
-- Name: casestate; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casestate FROM PUBLIC;
REVOKE ALL ON TABLE casestate FROM postgres;
GRANT ALL ON TABLE casestate TO postgres;


--
-- TOC entry 3672 (class 0 OID 0)
-- Dependencies: 379
-- Name: casetype; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE casetype FROM PUBLIC;
REVOKE ALL ON TABLE casetype FROM postgres;
GRANT ALL ON TABLE casetype TO postgres;


--
-- TOC entry 3673 (class 0 OID 0)
-- Dependencies: 375
-- Name: category; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE category FROM PUBLIC;
REVOKE ALL ON TABLE category FROM postgres;
GRANT ALL ON TABLE category TO postgres;


--
-- TOC entry 3674 (class 0 OID 0)
-- Dependencies: 367
-- Name: contact; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE contact FROM PUBLIC;
REVOKE ALL ON TABLE contact FROM postgres;
GRANT ALL ON TABLE contact TO postgres;


--
-- TOC entry 3675 (class 0 OID 0)
-- Dependencies: 368
-- Name: messageseq; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON SEQUENCE messageseq FROM PUBLIC;
REVOKE ALL ON SEQUENCE messageseq FROM postgres;
GRANT ALL ON SEQUENCE messageseq TO postgres;


--
-- TOC entry 3676 (class 0 OID 0)
-- Dependencies: 369
-- Name: messagelog; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE messagelog FROM PUBLIC;
REVOKE ALL ON TABLE messagelog FROM postgres;
GRANT ALL ON TABLE messagelog TO postgres;


--
-- TOC entry 3677 (class 0 OID 0)
-- Dependencies: 376
-- Name: questionset; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE questionset FROM PUBLIC;
REVOKE ALL ON TABLE questionset FROM postgres;
GRANT ALL ON TABLE questionset TO postgres;


--
-- TOC entry 3678 (class 0 OID 0)
-- Dependencies: 370
-- Name: response; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE response FROM PUBLIC;
REVOKE ALL ON TABLE response FROM postgres;
GRANT ALL ON TABLE response TO postgres;


--
-- TOC entry 3679 (class 0 OID 0)
-- Dependencies: 377
-- Name: sample; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE sample FROM PUBLIC;
REVOKE ALL ON TABLE sample FROM postgres;
GRANT ALL ON TABLE sample TO postgres;


--
-- TOC entry 3680 (class 0 OID 0)
-- Dependencies: 380
-- Name: survey; Type: ACL; Schema: casesvc; Owner: postgres
--

REVOKE ALL ON TABLE survey FROM PUBLIC;
REVOKE ALL ON TABLE survey FROM postgres;
GRANT ALL ON TABLE survey TO postgres;



