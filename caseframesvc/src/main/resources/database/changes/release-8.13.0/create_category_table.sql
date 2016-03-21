--create category table in caseframe

set schema 'caseframe';

CREATE TABLE category
(
 name character varying(40) NOT NULL,
 description character varying(50),
 closecase boolean,
 manual boolean,
 role character varying(20),
 generatedactiontype character varying(30)
);

ALTER TABLE caseframe.category
  OWNER TO role_connect;

ALTER TABLE ONLY caseframe.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (name);