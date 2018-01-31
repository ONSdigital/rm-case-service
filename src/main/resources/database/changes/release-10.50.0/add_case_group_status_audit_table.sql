CREATE TABLE casesvc.casegroupstatusaudit (
    casegroupfk bigint NOT NULL,
    partyid uuid,
    status  character varying(20),
    createddatetime timestamp with time zone
);