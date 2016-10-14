SET  schema 'casesvc';

CREATE TABLE unlinkedcasereceipt (
    caseref character varying(16) NOT NULL,
    inboundchannel character varying(10),
    respondedatetime timestamp with time zone
);
