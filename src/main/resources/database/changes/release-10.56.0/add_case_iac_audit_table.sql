CREATE SEQUENCE casesvc.caseiacauditseq;
CREATE TABLE casesvc.caseiacaudit (
    caseiacauditpk bigint DEFAULT nextval('casesvc.caseiacauditseq') NOT NULL,
    casefk bigint NOT NULL,
    iac character varying(12) NOT NULL,
    createddatetime timestamp with time zone
);

INSERT INTO casesvc.caseiacaudit (casefk, iac, createddatetime)
SELECT casepk, iac, now() FROM casesvc.case
WHERE sampleunittype = 'B'
ORDER BY casepk;