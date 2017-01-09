set schema 'casesvc';

CREATE SEQUENCE reportidseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;

CREATE TABLE reportrepository (
    reportid bigint DEFAULT nextval('reportidseq'::regclass) NOT NULL,
    reporttype character varying,
    reportdate date,
    contents text,
    createddatetime timestamp with time zone
);

CREATE TABLE reporttype (
    reporttype character varying UNIQUE
);

ALTER TABLE ONLY reportrepository
    ADD CONSTRAINT report_pkey PRIMARY KEY (reportid);
    
ALTER TABLE ONLY reportrepository
    ADD CONSTRAINT reporttype_fkey FOREIGN KEY (reporttype) REFERENCES reporttype(reporttype);

ALTER TABLE ONLY reportrepository
    ADD CONSTRAINT uq_reportrepository UNIQUE(reporttype, reportdate);        

INSERT INTO reporttype VALUES ('UNIVERSITY');