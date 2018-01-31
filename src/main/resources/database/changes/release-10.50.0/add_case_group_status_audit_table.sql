CREATE TABLE casesvc.casegroupstatusaudit (
    casegroupstatusauditpk bigint NOT NULL,
    casegroupfk bigint NOT NULL,
    partyid uuid,
    status  character varying(20),
    createddatetime timestamp with time zone
);

CREATE SEQUENCE casesvc.casegroupstatusauditseq;