
CREATE SEQUENCE casesvc.reporttypeidseq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 999999999999
  START 18
  CACHE 1;
ALTER TABLE casesvc.reporttypeidseq
  OWNER TO casesvc;

DELETE from casesvc.reportrepository;
DELETE from casesvc.reporttype;

ALTER TABLE casesvc.reporttype ADD COLUMN reporttypeid bigint DEFAULT nextval('casesvc.reporttypeidseq'::regclass) NOT NULL;

ALTER TABLE casesvc.reporttype ADD COLUMN orderid int;

ALTER TABLE casesvc.reporttype ADD COLUMN displayname text;

ALTER TABLE ONLY casesvc.reporttype
    ADD CONSTRAINT reporttypeid_pkey PRIMARY KEY (reporttypeid);

INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('HH_RETURNRATE', 10, 'HH Return Rate');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('HH_NORETURNS', 20, 'HH Noreturns');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('HH_RETURNRATE_SAMPLE', 30, 'HH Returnrate Sample');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('HH_RETURNRATE_LA', 40, 'HH Returnrate La');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('CE_RETURNRATE_UNI', 50, 'CE Returnrate Uni');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('CE_RETURNRATE_HOTEL', 60, 'CE Returnrate Hotel');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('HL_METRICS', 70, 'HL Metrics');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('PRINT_VOLUMES', 80, 'Print Volumes');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('HH_OUTSTANDING_CASES', 90, 'HH Outstanding Cases');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('SH_OUTSTANDING_CASES', 100, 'SH Outstanding Cases');
INSERT INTO casesvc.reporttype(reporttype, orderid, displayname) VALUES('CE_OUTSTANDING_CASES', 110, 'CE Outstanding Cases');