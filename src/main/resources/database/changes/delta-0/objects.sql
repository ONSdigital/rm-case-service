--
-- PostgreSQL database dump
--

-- Dumped from database version 11.6
-- Dumped by pg_dump version 14.4

--SET statement_timeout = 0;
--SET lock_timeout = 0;
--SET idle_in_transaction_session_timeout = 0;
--SET client_encoding = 'UTF8';
--SET standard_conforming_strings = on;
--SELECT pg_catalog.set_config('search_path', '', false);
--SET check_function_bodies = false;
--SET xmloption = content;
--SET client_min_messages = warning;
--SET row_security = off;

--
-- Name: casesvc; Type: SCHEMA; Schema: -; Owner: postgres
--

--CREATE SCHEMA casesvc;


--ALTER SCHEMA casesvc OWNER TO postgres;

--
-- Name: caserefseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.caserefseq
    START WITH 1000000000000001
    INCREMENT BY 1
    MINVALUE 1000000000000001
    MAXVALUE 9999999999999999
    CACHE 1;


--ALTER TABLE casesvc.caserefseq OWNER TO postgres;

--SET default_tablespace = '';

--
-- Name: case; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc."case" (
    case_pk bigint NOT NULL,
    id uuid NOT NULL,
    case_ref character varying(16) DEFAULT nextval('casesvc.caserefseq'::regclass),
    case_group_fk bigint NOT NULL,
    case_group_id uuid NOT NULL,
    party_id uuid,
    sample_unit_type character varying(2),
    collection_instrument_id uuid,
    state_fk character varying(20),
    action_plan_id uuid,
    created_date_time timestamp with time zone,
    created_by character varying(50),
    source_case bigint,
    opt_lock_version integer DEFAULT 0,
    sampleunit_id uuid,
    active_enrolment boolean DEFAULT false
);


--ALTER TABLE casesvc."case" OWNER TO postgres;

--
-- Name: case_action_audit_event; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.case_action_audit_event (
    id integer NOT NULL,
    case_id uuid NOT NULL,
    collection_exercise_id uuid NOT NULL,
    type character varying(40) NOT NULL,
    event_tag character varying(100) NOT NULL,
    handler character varying,
    status character varying,
    processed_timestamp timestamp without time zone,
    CONSTRAINT case_action_audit_event_handler_check CHECK ((((handler)::text = 'EMAIL'::text) OR ((handler)::text = 'LETTER'::text))),
    CONSTRAINT case_action_audit_event_status_check CHECK ((((status)::text = 'PROCESSED'::text) OR ((status)::text = 'FAILED'::text)))
);


--ALTER TABLE casesvc.case_action_audit_event OWNER TO postgres;

--
-- Name: case_action_audit_event_id_seq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.case_action_audit_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE casesvc.case_action_audit_event_id_seq OWNER TO postgres;

--
-- Name: case_action_audit_event_id_seq; Type: SEQUENCE OWNED BY; Schema: casesvc; Owner: postgres
--

ALTER SEQUENCE casesvc.case_action_audit_event_id_seq OWNED BY casesvc.case_action_audit_event.id;


--
-- Name: case_action_event_request; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.case_action_event_request (
    id integer NOT NULL,
    collection_exercise_id uuid NOT NULL,
    event_tag character varying(100) NOT NULL,
    process_event_requested_time timestamp without time zone,
    status character varying,
    CONSTRAINT case_action_event_request_status_check CHECK ((((status)::text = 'INPROGRESS'::text) OR ((status)::text = 'COMPLETED'::text) OR ((status)::text = 'FAILED'::text) OR ((status)::text = 'RETRY'::text)))
);


--ALTER TABLE casesvc.case_action_event_request OWNER TO postgres;

--
-- Name: case_action_event_request_id_seq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.case_action_event_request_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE casesvc.case_action_event_request_id_seq OWNER TO postgres;

--
-- Name: case_action_event_request_id_seq; Type: SEQUENCE OWNED BY; Schema: casesvc; Owner: postgres
--

ALTER SEQUENCE casesvc.case_action_event_request_id_seq OWNED BY casesvc.case_action_event_request.id;


--
-- Name: case_action_template; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.case_action_template (
    type character varying(40) NOT NULL,
    description character varying(350) NOT NULL,
    event_tag character varying(100) NOT NULL,
    handler character varying,
    prefix character varying(100),
    CONSTRAINT case_action_template_handler_check CHECK ((((handler)::text = 'EMAIL'::text) OR ((handler)::text = 'LETTER'::text)))
);


--ALTER TABLE casesvc.case_action_template OWNER TO postgres;

--
-- Name: caseevent; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.caseevent (
    case_event_pk bigint NOT NULL,
    case_fk bigint NOT NULL,
    description character varying(350),
    created_by character varying(50),
    created_date_time timestamp with time zone,
    category_fk character varying(60),
    subcategory character varying(100),
    metadata jsonb
);


--ALTER TABLE casesvc.caseevent OWNER TO postgres;

--
-- Name: caseeventseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.caseeventseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


--ALTER TABLE casesvc.caseeventseq OWNER TO postgres;

--
-- Name: casegroup; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.casegroup (
    case_group_pk bigint NOT NULL,
    id uuid NOT NULL,
    party_id uuid,
    collection_exercise_id uuid,
    sample_unit_ref character varying(20),
    sample_unit_type character varying(2),
    status character varying(20) NOT NULL,
    survey_id uuid
);


--ALTER TABLE casesvc.casegroup OWNER TO postgres;

--
-- Name: casegroupseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.casegroupseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


--ALTER TABLE casesvc.casegroupseq OWNER TO postgres;

--
-- Name: casegroupstatusaudit; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.casegroupstatusaudit (
    case_group_status_audit_pk bigint NOT NULL,
    case_group_fk bigint NOT NULL,
    party_id uuid,
    status character varying(20),
    created_date_time timestamp with time zone
);


--ALTER TABLE casesvc.casegroupstatusaudit OWNER TO postgres;

--
-- Name: casegroupstatusauditseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.casegroupstatusauditseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE casesvc.casegroupstatusauditseq OWNER TO postgres;

--
-- Name: caseiacauditseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.caseiacauditseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE casesvc.caseiacauditseq OWNER TO postgres;

--
-- Name: caseiacaudit; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.caseiacaudit (
    case_iac_audit_pk bigint DEFAULT nextval('casesvc.caseiacauditseq'::regclass) NOT NULL,
    case_fk bigint NOT NULL,
    iac character varying(12) NOT NULL,
    created_date_time timestamp with time zone
);


--ALTER TABLE casesvc.caseiacaudit OWNER TO postgres;

--
-- Name: caseseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.caseseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


--ALTER TABLE casesvc.caseseq OWNER TO postgres;

--
-- Name: casestate; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.casestate (
    state_pk character varying(20) NOT NULL
);


--ALTER TABLE casesvc.casestate OWNER TO postgres;

--
-- Name: category; Type: TABLE; Schema: casesvc; Owner: postgres
--

CREATE TABLE casesvc.category (
    category_pk character varying(60) NOT NULL,
    short_description character varying(50),
    long_description character varying(100),
    event_type character varying(20),
    role character varying(100),
    generated_action_type character varying(100),
    "group" character varying(20),
    old_case_sample_unit_types character varying(10) NOT NULL,
    new_case_sample_unit_type character varying(10),
    recalc_collection_instrument boolean
);


--ALTER TABLE casesvc.category OWNER TO postgres;

--
-- Name: databasechangelog; Type: TABLE; Schema: casesvc; Owner: postgres
--

--CREATE TABLE casesvc.databasechangelog (
--    id character varying(255) NOT NULL,
--    author character varying(255) NOT NULL,
--    filename character varying(255) NOT NULL,
--    dateexecuted timestamp without time zone NOT NULL,
--    orderexecuted integer NOT NULL,
--    exectype character varying(10) NOT NULL,
--    md5sum character varying(35),
--    description character varying(255),
--    comments character varying(255),
--    tag character varying(255),
--    liquibase character varying(20),
--    contexts character varying(255),
--    labels character varying(255),
--    deployment_id character varying(10)
--);


--ALTER TABLE casesvc.databasechangelog OWNER TO postgres;

--
-- Name: databasechangeloglock; Type: TABLE; Schema: casesvc; Owner: postgres
--

--CREATE TABLE casesvc.databasechangeloglock (
--    id integer NOT NULL,
--    locked boolean NOT NULL,
--    lockgranted timestamp without time zone,
--    lockedby character varying(255)
--);


--ALTER TABLE casesvc.databasechangeloglock OWNER TO postgres;

--
-- Name: messagelogseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.messagelogseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


--ALTER TABLE casesvc.messagelogseq OWNER TO postgres;

--
-- Name: reportpkseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.reportpkseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


--ALTER TABLE casesvc.reportpkseq OWNER TO postgres;

--
-- Name: responseseq; Type: SEQUENCE; Schema: casesvc; Owner: postgres
--

CREATE SEQUENCE casesvc.responseseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


--ALTER TABLE casesvc.responseseq OWNER TO postgres;

--
-- Name: case_action_audit_event id; Type: DEFAULT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.case_action_audit_event ALTER COLUMN id SET DEFAULT nextval('casesvc.case_action_audit_event_id_seq'::regclass);


--
-- Name: case_action_event_request id; Type: DEFAULT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.case_action_event_request ALTER COLUMN id SET DEFAULT nextval('casesvc.case_action_event_request_id_seq'::regclass);


--
-- Name: case_action_audit_event case_action_audit_event_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.case_action_audit_event
    ADD CONSTRAINT case_action_audit_event_pkey PRIMARY KEY (id);


--
-- Name: case_action_event_request case_action_event_request_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.case_action_event_request
    ADD CONSTRAINT case_action_event_request_pkey PRIMARY KEY (id);


--
-- Name: case case_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc."case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (case_pk);


--
-- Name: case case_uuid_key; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc."case"
    ADD CONSTRAINT case_uuid_key UNIQUE (id);


--
-- Name: caseevent caseevent_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (case_event_pk);


--
-- Name: casegroup casegroup_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.casegroup
    ADD CONSTRAINT casegroup_pkey PRIMARY KEY (case_group_pk);


--
-- Name: casegroup casegroup_uuid_key; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.casegroup
    ADD CONSTRAINT casegroup_uuid_key UNIQUE (id);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_pk);


--
-- Name: databasechangeloglock databasechangeloglock_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

--ALTER TABLE ONLY casesvc.databasechangeloglock
--    ADD CONSTRAINT databasechangeloglock_pkey PRIMARY KEY (id);


--
-- Name: casestate state_pkey; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.casestate
    ADD CONSTRAINT state_pkey PRIMARY KEY (state_pk);


--
-- Name: case_action_event_request unique_action_event_constraint; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.case_action_event_request
    ADD CONSTRAINT unique_action_event_constraint UNIQUE (collection_exercise_id, event_tag);


--
-- Name: casegroup unique_casegroup_constraint; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.casegroup
    ADD CONSTRAINT unique_casegroup_constraint UNIQUE (collection_exercise_id, sample_unit_ref);


--
-- Name: case_action_template unique_template_constraint; Type: CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.case_action_template
    ADD CONSTRAINT unique_template_constraint UNIQUE (type, event_tag, handler);


--
-- Name: case_casegroupfk_index; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX case_casegroupfk_index ON casesvc."case" USING btree (case_group_fk);


--
-- Name: case_partyid_index; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX case_partyid_index ON casesvc."case" USING btree (party_id);


--
-- Name: case_state_index; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX case_state_index ON casesvc."case" USING btree (state_fk);


--
-- Name: caseevent_casefk_index; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX caseevent_casefk_index ON casesvc.caseevent USING btree (case_fk);


--
-- Name: caseevent_categoryfk_index; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX caseevent_categoryfk_index ON casesvc.caseevent USING btree (category_fk);


--
-- Name: caseiacaudit_casefk_index; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX caseiacaudit_casefk_index ON casesvc.caseiacaudit USING btree (case_fk);


--
-- Name: caseiacaudit_iac_index; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE UNIQUE INDEX caseiacaudit_iac_index ON casesvc.caseiacaudit USING btree (iac);


--
-- Name: idx_case_action_audit_event_case_id; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_action_audit_event_case_id ON casesvc.case_action_audit_event USING btree (case_id);


--
-- Name: idx_case_action_audit_event_collection_exercise_id; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_action_audit_event_collection_exercise_id ON casesvc.case_action_audit_event USING btree (collection_exercise_id);


--
-- Name: idx_case_action_audit_event_handler; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_action_audit_event_handler ON casesvc.case_action_audit_event USING btree (handler);


--
-- Name: idx_case_action_audit_event_status; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_action_audit_event_status ON casesvc.case_action_audit_event USING btree (status);


--
-- Name: idx_case_action_audit_event_tag; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_action_audit_event_tag ON casesvc.case_action_audit_event USING btree (event_tag);


--
-- Name: idx_case_action_event_request_collex; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_action_event_request_collex ON casesvc.case_action_event_request USING btree (collection_exercise_id);


--
-- Name: idx_case_action_event_request_tag; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_action_event_request_tag ON casesvc.case_action_event_request USING btree (event_tag);


--
-- Name: idx_case_action_template_tag; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_action_template_tag ON casesvc.case_action_template USING btree (event_tag);


--
-- Name: idx_case_active_enrolment; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_case_active_enrolment ON casesvc."case" USING btree (active_enrolment);


--
-- Name: idx_casegroup_collection_exercise_id; Type: INDEX; Schema: casesvc; Owner: postgres
--

CREATE INDEX idx_casegroup_collection_exercise_id ON casesvc.casegroup USING btree (collection_exercise_id);


--
-- Name: caseevent case_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.caseevent
    ADD CONSTRAINT case_fkey FOREIGN KEY (case_fk) REFERENCES casesvc."case"(case_pk);


--
-- Name: case casegroup_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc."case"
    ADD CONSTRAINT casegroup_fkey FOREIGN KEY (case_group_fk) REFERENCES casesvc.casegroup(case_group_pk);


--
-- Name: caseevent category_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc.caseevent
    ADD CONSTRAINT category_fkey FOREIGN KEY (category_fk) REFERENCES casesvc.category(category_pk);


--
-- Name: case state_fkey; Type: FK CONSTRAINT; Schema: casesvc; Owner: postgres
--

ALTER TABLE ONLY casesvc."case"
    ADD CONSTRAINT state_fkey FOREIGN KEY (state_fk) REFERENCES casesvc.casestate(state_pk);


--
-- PostgreSQL database dump complete
--

