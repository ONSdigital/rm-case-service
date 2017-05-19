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

--Create tables  

CREATE TABLE "case" (
    caseid bigint NOT NULL,
    id uuid NOT NULL,
    casegroupid bigint NOT NULL,
    caseref character varying(16),
    partyid uuid,
    sampleunittype character varying(2),
    collectioninstrumentid uuid,
    state character varying(20),
    actionplanid integer,
    createddatetime timestamp with time zone,
    createdby character varying(50),
    iac character varying(20),
    sourcecaseid bigint,
    optlockversion integer DEFAULT 0
);

CREATE TABLE caseevent (
    caseeventid bigint NOT NULL,
    caseid bigint NOT NULL,
    description character varying(350),
    createdby character varying(50),
    createddatetime timestamp with time zone,
    category character varying(40),
    subcategory character varying(100)
);

CREATE TABLE casegroup (
    casegroupid bigint NOT NULL,
    id uuid NOT NULL,
    partyid uuid,
    collectionexerciseid uuid,
    sampleunitref  character varying(20),
    sampleunittype character varying(2)
);

CREATE TABLE casestate (
    state character varying(20) NOT NULL
);

CREATE TABLE category (
    name character varying(40) NOT NULL,
    shortdescription character varying(50),
    longdescription character varying(50),
    eventtype character varying(20),
    role character varying(100),
    generatedactiontype character varying(100),
    "group" character varying(20),
    oldcasesampleunittype character varying(10),
    newcasesampleunittype character varying(10),
    recalcCollectionInstrument boolean
);

CREATE TABLE response (
    responseid bigint DEFAULT nextval('responseidseq'::regclass) NOT NULL,
    caseid bigint,
    inboundchannel character varying(10),
    responsedatetime timestamp with time zone
);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (caseid);

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventid);

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT casegroup_pkey PRIMARY KEY (casegroupid);

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (name);

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (responseid);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casegroupid_fkey FOREIGN KEY (casegroupid) REFERENCES casegroup(casegroupid);

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);

ALTER TABLE ONLY response
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);
