-- TODO: DO I NEED TO SAY SCHEMA? is this the right way of adding table

CREATE TABLE casesvc.casegroupstatusaudit (
    casegroupfk bigint NOT NULL,
    createdby character varying(128),
    status  character varying(20),
    createddatetime timestamp with time zone
);