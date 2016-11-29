SET schema 'casesvc';

CREATE SEQUENCE unlinkedcasereceiptidseq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 999999999999
    CACHE 1;

ALTER TABLE casesvc.unlinkedcasereceipt ADD column id bigint NOT NULL DEFAULT nextval('casesvc.unlinkedcasereceiptidseq'::regclass);
ALTER TABLE casesvc.unlinkedcasereceipt ALTER COLUMN caseref TYPE character varying(256);
