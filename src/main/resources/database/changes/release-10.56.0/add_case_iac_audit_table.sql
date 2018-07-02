CREATE SEQUENCE casesvc.caseiacauditseq;
CREATE TABLE casesvc.caseiacaudit (
    caseiacauditpk bigint NOT NULL,
    casefk bigint NOT NULL,
    iac character varying(12) NOT NULL,
    createddatetime timestamp with time zone
);