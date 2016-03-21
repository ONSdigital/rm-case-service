-- Sequence: caseframe.caseeventid_seq

-- DROP SEQUENCE caseframe.caseeventid_seq;

CREATE SEQUENCE caseframe.caseeventidseq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 999999999999
  START 762
  CACHE 1;
ALTER TABLE caseframe.caseeventidseq
  OWNER TO postgres;


ALTER TABLE caseframe.caseevent ALTER COLUMN caseeventid type bigint;
ALTER TABLE caseframe.caseevent ALTER COLUMN caseeventid SET DEFAULT nextval('caseframe.caseeventidseq'::regclass);

ALTER TABLE caseframe.caseevent ADD COLUMN subcategory character varying(100);
ALTER TABLE caseframe.caseevent ALTER COLUMN category type character varying(40);