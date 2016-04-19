
-- Sequence: caseframe.messageseq
-- DROP SEQUENCE caseframe.messageseq;
CREATE SEQUENCE caseframe.messageseq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 999999999999
  START 1
  CACHE 1;
ALTER TABLE caseframe.messageseq
  OWNER TO postgres;


-- Table: caseframe.messagelog
-- DROP TABLE caseframe.messagelog;
CREATE TABLE caseframe.messagelog
(
  messageid bigint NOT NULL DEFAULT nextval('caseframe.messageseq'::regclass),
  messagetext character varying,
  jobid numeric,
  messagelevel character varying,
  functionname character varying,
  createddatetime timestamp with time zone,
  CONSTRAINT messageid_pkey PRIMARY KEY (messageid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE caseframe.messagelog
  OWNER TO postgres;




