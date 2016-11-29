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
    ,'Initial creation of case'
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

CREATE TABLE actionplanmapping (
    actionplanmappingid integer NOT NULL,
    actionplanid integer,
    casetypeid integer,
    isdefault boolean,
    inboundchannel character varying(10),
    variant character varying(10),
    outboundchannel character varying(10)
);

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

CREATE SEQUENCE caseeventidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE TABLE caseevent (
    caseeventid bigint DEFAULT nextval('caseeventidseq'::regclass) NOT NULL,
    caseid bigint NOT NULL,
    description character varying(350),
    createdby character varying(50),
    createddatetime timestamp with time zone,
    category character varying(40),
    subcategory character varying(100)
);

CREATE TABLE casegroup (
    casegroupid bigint NOT NULL,
    uprn numeric(12,0),
    sampleid integer
);

CREATE SEQUENCE casegroupidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE TABLE casestate (
    state character varying(20) NOT NULL
);

CREATE TABLE casetype (
    casetypeid integer NOT NULL,
    name character varying(20),
    description character varying(100),
    questionset character varying(10),
    respondenttype character varying(10)
);

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

CREATE SEQUENCE contactidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE TABLE contact (
    contactid integer DEFAULT nextval('contactidseq'::regclass) NOT NULL,
    forename character varying(35),
    surname character varying(35),
    phonenumber character varying(20),
    emailaddress character varying(50),
    title character varying(20)
);

CREATE SEQUENCE messageseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE TABLE messagelog (
    messageid bigint DEFAULT nextval('messageseq'::regclass) NOT NULL,
    messagetext character varying,
    jobid numeric,
    messagelevel character varying,
    functionname character varying,
    createddatetime timestamp with time zone
);

CREATE TABLE questionset (
    questionset character varying(10) NOT NULL
);

CREATE TABLE respondenttype (
    respondenttype character varying(10) NOT NULL
);

CREATE SEQUENCE responseidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE TABLE response (
    responseid bigint DEFAULT nextval('responseidseq'::regclass) NOT NULL,
    caseid bigint,
    inboundchannel character varying(10),
    datetime timestamp with time zone
);

CREATE TABLE sample (
    sampleid integer NOT NULL,
    name character varying(20),
    description character varying(100),
    addresscriteria character varying(100),
    survey character varying(20)
);

CREATE TABLE samplecasetypeselector (
    samplecasetypeselectorid integer NOT NULL,
    sampleid integer,
    casetypeid integer,
    respondenttype character varying(10)
);

CREATE TABLE survey (
    survey character varying(20) NOT NULL
);

CREATE TABLE unlinkedcasereceipt (
    caseref character varying(16) NOT NULL,
    inboundchannel character varying(10),
    responsedatetime timestamp with time zone
);

ALTER TABLE ONLY actionplanmapping
    ADD CONSTRAINT actionplanmapping_pkey PRIMARY KEY (actionplanmappingid);

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (uprn);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT case_pkey PRIMARY KEY (caseid);

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseevent_pkey PRIMARY KEY (caseeventid);

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT casegroup_pkey PRIMARY KEY (casegroupid);

ALTER TABLE ONLY casestate
    ADD CONSTRAINT casestate_pkey PRIMARY KEY (state);

ALTER TABLE ONLY casetype
    ADD CONSTRAINT casetype_pkey PRIMARY KEY (casetypeid);

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (name);

ALTER TABLE ONLY contact
    ADD CONSTRAINT contact_pkey PRIMARY KEY (contactid);

ALTER TABLE ONLY messagelog
    ADD CONSTRAINT messageid_pkey PRIMARY KEY (messageid);

ALTER TABLE ONLY questionset
    ADD CONSTRAINT questionset_pkey PRIMARY KEY (questionset);

ALTER TABLE ONLY respondenttype
    ADD CONSTRAINT respondenttype_pkey PRIMARY KEY (respondenttype);

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (responseid);

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (sampleid);

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT samplecasetypeselector_pkey PRIMARY KEY (samplecasetypeselectorid);

ALTER TABLE ONLY survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (survey);

CREATE INDEX address_lad12cd_idx ON address USING btree (lad12cd);

CREATE INDEX address_msoa11cd_idx ON address USING btree (msoa11cd);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT actionplanmappingid_fkey FOREIGN KEY (actionplanmappingid) REFERENCES actionplanmapping(actionplanmappingid);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casegroupid_fkey FOREIGN KEY (casegroupid) REFERENCES casegroup(casegroupid);

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);

ALTER TABLE ONLY response
    ADD CONSTRAINT caseid_fkey FOREIGN KEY (caseid) REFERENCES "case"(caseid);

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT casetyeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);

ALTER TABLE ONLY actionplanmapping
    ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT casetypeid_fkey FOREIGN KEY (casetypeid) REFERENCES casetype(casetypeid);

ALTER TABLE ONLY caseevent
    ADD CONSTRAINT category_fkey FOREIGN KEY (category) REFERENCES category(name);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT contactid_fkey FOREIGN KEY (contactid) REFERENCES contact(contactid);

ALTER TABLE ONLY category
    ADD CONSTRAINT newcaserespondenttype_fkey FOREIGN KEY (newcaserespondenttype) REFERENCES respondenttype(respondenttype);

ALTER TABLE ONLY category
    ADD CONSTRAINT oldcaserespondenttype_fkey FOREIGN KEY (oldcaserespondenttype) REFERENCES respondenttype(respondenttype);

ALTER TABLE ONLY casetype
    ADD CONSTRAINT questionset_fkey FOREIGN KEY (questionset) REFERENCES questionset(questionset);

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT respondenttype_fkey FOREIGN KEY (respondenttype) REFERENCES respondenttype(respondenttype);

ALTER TABLE ONLY casetype
    ADD CONSTRAINT respondenttype_fkey FOREIGN KEY (respondenttype) REFERENCES respondenttype(respondenttype);

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);

ALTER TABLE ONLY samplecasetypeselector
    ADD CONSTRAINT sampleid_fkey FOREIGN KEY (sampleid) REFERENCES sample(sampleid);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT sourcecaseid_fkey FOREIGN KEY (sourcecaseid) REFERENCES "case"(caseid);

ALTER TABLE ONLY "case"
    ADD CONSTRAINT state_fkey FOREIGN KEY (state) REFERENCES casestate(state);

ALTER TABLE ONLY sample
    ADD CONSTRAINT survey_fkey FOREIGN KEY (survey) REFERENCES survey(survey);

ALTER TABLE ONLY casegroup
    ADD CONSTRAINT uprn_fkey FOREIGN KEY (uprn) REFERENCES address(uprn);
