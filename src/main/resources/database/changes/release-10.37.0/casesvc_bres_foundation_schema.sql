SET schema 'casesvc';

--create sequences, all defaults taken off tables

CREATE SEQUENCE caseeventidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE SEQUENCE caseidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE SEQUENCE caserefseq
    START WITH 1000000000000001
    INCREMENT BY 1
    MINVALUE 1000000000000001
    MAXVALUE 9999999999999999
    CACHE 1;

CREATE SEQUENCE casegroupidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE SEQUENCE responseidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


CREATE TABLE "case" (
    caseid bigint NOT NULL,
    casegroupid bigint NOT NULL,
    caseref character varying(16),
    partyid character varying(128),
    sampleunitref  character varying(20),
    sampleunittype character varying(2),
    collectioninstrumentid character varying(128),
    state character varying(20),
    actionplanid integer,
    createddatetime timestamp with time zone,
    createdby character varying(50),
    iac character varying(20),
    sourcecaseid bigint,
    optlockversion integer DEFAULT 0
);


--
-- TOC entry 527 (class 1259 OID 76317)
-- Name: caseevent; Type: TABLE; Schema: casesvc; Owner: casesvc; Tablespace:
--

CREATE TABLE caseevent (
    caseeventid bigint NOT NULL,
    caseid bigint NOT NULL,
    description character varying(350),
    createdby character varying(50),
    createddatetime timestamp with time zone,
    category character varying(40),
    subcategory character varying(100)
);


--
-- TOC entry 528 (class 1259 OID 76324)
-- Name: casegroup; Type: TABLE; Schema: casesvc; Owner: casesvc; Tablespace:
--

CREATE TABLE casegroup (
    casegroupid bigint NOT NULL,
    partyid character varying(128),
    sampleunitref  character varying(20),
    sampleunittype character varying(2),
    collectioninstrumentid character varying(128)
);


--
-- TOC entry 530 (class 1259 OID 76329)
-- Name: casestate; Type: TABLE; Schema: casesvc; Owner: casesvc; Tablespace:
--

CREATE TABLE casestate (
    state character varying(20) NOT NULL
);

--
-- TOC entry 532 (class 1259 OID 76335)
-- Name: category; Type: TABLE; Schema: casesvc; Owner: casesvc; Tablespace:
--

CREATE TABLE category (
    name character varying(40) NOT NULL,
    shortdescription character varying(50),
    longdescription character varying(50),
    eventtype character varying(20),
    manual boolean,
    role character varying(100),
    generatedactiontype character varying(100),
    "group" character varying(20),
    oldcasesampleunittype character varying(10),
    newcasesampleunittype character varying(10)
);



--
-- TOC entry 540 (class 1259 OID 76361)
-- Name: response; Type: TABLE; Schema: casesvc; Owner: casesvc; Tablespace:
--

CREATE TABLE response (
    responseid bigint DEFAULT nextval('responseidseq'::regclass) NOT NULL,
    caseid bigint,
    inboundchannel character varying(10),
    responsedatetime timestamp with time zone
);


--
-- TOC entry 4075 (class 2606 OID 76382)
-- Name: case_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: casesvc; Tablespace:
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (caseid);


--
-- TOC entry 4079 (class 2606 OID 76384)
-- Name: caseevent_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: casesvc; Tablespace:
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventid);


--
-- TOC entry 4081 (class 2606 OID 76386)
-- Name: casegroup_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: casesvc; Tablespace:
--

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT casegroup_pkey PRIMARY KEY (casegroupid);

--
-- TOC entry 4087 (class 2606 OID 76392)
-- Name: category_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: casesvc; Tablespace:
--

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (name);

--
-- TOC entry 4097 (class 2606 OID 76402)
-- Name: response_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: casesvc; Tablespace:
--

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (responseid);

--
-- TOC entry 4116 (class 2606 OID 76416)
-- Name: casegroupid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: casesvc
--

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casegroupid_fkey FOREIGN KEY (casegroupid) REFERENCES casegroup(casegroupid);

--
-- TOC entry 4118 (class 2606 OID 76421)
-- Name: caseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: casesvc
--

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);


--
-- TOC entry 4125 (class 2606 OID 76426)
-- Name: caseid_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: casesvc
--

ALTER TABLE ONLY response
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);
