ALTER TABLE casesvc.case ALTER COLUMN caseid SET DEFAULT nextval('casesvc.caseidseq'::regclass);
ALTER TABLE casesvc.case ALTER COLUMN caseref SET DEFAULT nextval('casesvc.caserefseq'::regclass);
ALTER TABLE casesvc.contact ALTER COLUMN phonenumber TYPE character varying(20);
