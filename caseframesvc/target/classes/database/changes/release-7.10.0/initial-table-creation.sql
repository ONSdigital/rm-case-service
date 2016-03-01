set schema 'caseframe';


CREATE TABLE action (
    actionid integer NOT NULL,
    caseid integer,
    actionplanid integer,
    action_status character varying(10),
    action_type character varying(10),
    priority character varying(10),
    situation character varying(10),
    created_datetime timestamp with time zone,
    created_by character varying(20)
);


ALTER TABLE caseframe.action OWNER TO postgres;


CREATE TABLE actionplan (
    actionplanid integer NOT NULL,
    actionplan_name character varying(20),
    description character varying(100),
    rules character varying(100)
);


ALTER TABLE caseframe.actionplan OWNER TO postgres;


CREATE TABLE address (
    uprn numeric(12,0) NOT NULL,
    addresstype character varying(6),
    estabtype character varying(6),
    address_line1 character varying(60),
    address_line2 character varying(60),
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


ALTER TABLE caseframe.address OWNER TO postgres;


CREATE TABLE "case" (
    caseid integer NOT NULL,
    uprn numeric(12,0),
    case_status character varying(10),
    casetypeid integer,
    created_datetime timestamp with time zone,
    created_by character varying(20),
    sampleid integer,
    actionplanid integer,
    surveyid integer,
    questionset character varying(10)
);


ALTER TABLE caseframe."case" OWNER TO postgres;


CREATE TABLE caseevent (
    caseeventid integer NOT NULL,
    caseid integer NOT NULL,
    description character varying(100),
    created_by character varying(20),
    created_datetime timestamp with time zone,
    category character varying(20)
);


ALTER TABLE caseframe.caseevent OWNER TO postgres;


CREATE TABLE casetype (
    casetypeid integer NOT NULL,
    casetype_name character varying(20),
    description character varying(100),
    actionplanid integer,
    questionset character varying(10)
);


ALTER TABLE caseframe.casetype OWNER TO postgres;


CREATE TABLE questionnaire (
    questionnaireid integer NOT NULL,
    caseid integer,
    questionnaire_status character varying(10),
    dispatch_datetime timestamp with time zone,
    response_datetime timestamp with time zone,
    receipt_datetime timestamp with time zone,
    questionset character varying(10),
    iac character(20)
);


ALTER TABLE caseframe.questionnaire OWNER TO postgres;


CREATE TABLE questionset (
    questionset character varying(10) NOT NULL,
    description character varying(100)
);


ALTER TABLE caseframe.questionset OWNER TO postgres;


CREATE TABLE sample (
    sampleid integer NOT NULL,
    sample_name character varying(20),
    description character varying(100),
    address_criteria character varying(100),
    casetypeid integer,
    surveyid integer
);


ALTER TABLE caseframe.sample OWNER TO postgres;


CREATE TABLE survey (
    surveyid integer NOT NULL,
    survey_name character varying(20),
    description character varying(100)
);


ALTER TABLE caseframe.survey OWNER TO postgres;


ALTER TABLE ONLY action
    ADD CONSTRAINT action_pkey PRIMARY KEY (actionid);



ALTER TABLE ONLY actionplan
    ADD CONSTRAINT actionplan_pkey PRIMARY KEY (actionplanid);



ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (uprn);



ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (caseid);



ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventid);



ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_pkey1 PRIMARY KEY (casetypeid);



ALTER TABLE ONLY questionnaire
    ADD CONSTRAINT questionnaire_pkey PRIMARY KEY (questionnaireid);



ALTER TABLE ONLY questionset
    ADD CONSTRAINT questionset_pkey PRIMARY KEY (questionset);



ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (sampleid);



ALTER TABLE ONLY survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (surveyid);



CREATE INDEX address_lad12cd_idx ON address USING btree (lad12cd);



CREATE INDEX address_msoa11cd_idx ON address USING btree (msoa11cd);



ALTER TABLE ONLY action
    ADD CONSTRAINT action_actionplanid_fkey FOREIGN KEY (actionplanid) REFERENCES actionplan(actionplanid);



ALTER TABLE ONLY action
    ADD CONSTRAINT action_caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);



ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_actionplanid_fkey FOREIGN KEY (actionplanid) REFERENCES actionplan(actionplanid);



ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);



ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);



ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);



ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_surveyid_fkey FOREIGN KEY (surveyid) REFERENCES survey(surveyid);



ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_uprn_fkey FOREIGN KEY (uprn) REFERENCES address(uprn);



ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);



ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_actionplanid_fkey FOREIGN KEY (actionplanid) REFERENCES actionplan(actionplanid);



ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);



ALTER TABLE ONLY questionnaire
    ADD CONSTRAINT questionnaire_caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);



ALTER TABLE ONLY questionnaire
    ADD CONSTRAINT questionnaire_questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);



ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);



ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_surveyid_fkey FOREIGN KEY (surveyid) REFERENCES survey(surveyid);




