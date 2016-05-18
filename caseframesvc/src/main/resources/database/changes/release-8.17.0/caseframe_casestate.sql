--creating casestate table in caseframe

CREATE TABLE caseframe.casestate (
    state character varying(100) NOT NULL,
    description character varying(250)
);


ALTER TABLE caseframe.casestate OWNER TO postgres;

--insert initial data into casestate table

INSERT INTO caseframe.casestate (state, description) VALUES ('INIT', 'Initial creation of case');
INSERT INTO caseframe.casestate (state, description) VALUES ('CLOSED', 'Case Closed ');
INSERT INTO caseframe.casestate (state, description) VALUES ('RESPONDED', '2016 Hotels only response received');


ALTER TABLE ONLY caseframe.casestate
    ADD CONSTRAINT casestate_pkey PRIMARY KEY (state);


GRANT ALL ON TABLE caseframe.casestate TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE caseframe.casestate TO caseframesvc;


