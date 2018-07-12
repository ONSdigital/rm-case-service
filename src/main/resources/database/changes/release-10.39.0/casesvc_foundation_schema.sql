SET schema 'casesvc';

--create sequences, all defaults taken off tables

CREATE SEQUENCE caseeventseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE SEQUENCE caseseq
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

CREATE SEQUENCE casegroupseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE SEQUENCE responseseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

--Create tables  

CREATE TABLE "case" (
    casePK bigint NOT NULL,
    id uuid NOT NULL,
    caseref character varying(16),
    caseGroupFK bigint NOT NULL,
    caseGroupid uuid NOT NULL,
    partyId uuid,
    sampleUnitType character varying(2),
    collectionInstrumentId uuid,
    state character varying(20),
    actionPlanId uuid,
    createdDateTime timestamp with time zone,
    createdBy character varying(50),
    iac character varying(20),
    sourcecase bigint,
    optlockversion integer DEFAULT 0
);

CREATE TABLE caseevent (
    caseEventPK bigint NOT NULL,
    caseFK bigint NOT NULL,
    description character varying(350),
    createdby character varying(50),
    createddatetime timestamp with time zone,
    categoryFK character varying(40),
    subcategory character varying(100)
);

CREATE TABLE casegroup (
    casegroupPK bigint NOT NULL,
    id uuid NOT NULL,
    partyId uuid,
    collectionExerciseId uuid,
    sampleunitref  character varying(20),
    sampleunittype character varying(2)
);

CREATE TABLE casestate (
    state character varying(20) NOT NULL
);

CREATE TABLE category (
    categoryPK character varying(40) NOT NULL,
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
    responsePK bigint NOT NULL,
    caseFK bigint NOT NULL,
    inboundChannel character varying(10),
    responseDateTime timestamp with time zone
);
--add primary keys to tables

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (casePK);

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventPK);

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT casegroup_pkey PRIMARY KEY (casegroupPK);

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (categoryPK);

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (responsePK);

ALTER TABLE ONLY casestate
    ADD CONSTRAINT state_pkey PRIMARY KEY (state);
-- add foreign key constraints

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casegroup_fkey FOREIGN KEY (casegroupFK) REFERENCES casegroup(casegroupPK);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT state_fkey FOREIGN KEY (state) REFERENCES casestate(state);

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT case_fkey FOREIGN KEY (caseFK) REFERENCES "case"(casePK);

ALTER TABLE ONLY response
    ADD CONSTRAINT case_fkey FOREIGN KEY (caseFK) REFERENCES "case"(casePK);

-- add constraints to ensure uuid fields are unique
ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_uuid_key UNIQUE (id);

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT casegroup_uuid_key UNIQUE (id);

