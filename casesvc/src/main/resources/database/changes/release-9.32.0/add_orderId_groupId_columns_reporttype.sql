
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

ALTER TABLE ONLY casesvc.reporttype
    ADD CONSTRAINT reporttypeid_pkey PRIMARY KEY (reporttypeid);

INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('HH_RETURNRATE', 10);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('HH_NORETURNS', 20);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('HH_RETURNRATE_SAMPLE', 30);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('HH_RETURNRATE_LA', 40);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('CE_RETURNRATE_UNI', 50);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('CE_RETURNRATE_HOTEL', 60);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('HL_METRICS', 70);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('PRINT_VOLUMES', 80);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('HH_OUTSTANDING_CASES', 90);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('SH_OUTSTANDING_CASES', 100);
INSERT INTO casesvc.reporttype(reporttype, orderid) VALUES('CE_OUTSTANDING_CASES', 110);

select * from casesvc.insert_university_report_into_reportrepository();
select * from casesvc.insert_university_report_into_reportrepository();
select * from casesvc.insert_university_report_into_reportrepository();