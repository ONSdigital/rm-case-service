SET schema 'casesvc';

CREATE TABLE unlinkedcasereceipt (
    caseref character varying(16) NOT NULL,
    inboundchannel character varying(10),
    responsedatetime timestamp with time zone
);

ALTER TABLE casesvc.unlinkedcasereceipt OWNER TO postgres;
