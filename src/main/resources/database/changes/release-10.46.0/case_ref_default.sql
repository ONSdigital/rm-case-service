ALTER TABLE casesvc."case"
    ALTER COLUMN caseref SET DEFAULT nextval('casesvc.caserefseq'::regclass);
