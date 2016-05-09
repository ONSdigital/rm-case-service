
set schema 'caseframe';

-- will truncate case, caseevent and questionnaire as well as address

TRUNCATE address CASCADE;

--drop exisitng address table

 DROP TABLE address CASCADE;

--create new address table

CREATE TABLE address
(
  uprn numeric(12,0) NOT NULL,
  addresstype character varying(6),
  estabtype character varying(6),
  category character varying(20),
  organisation_name character varying(60),
  address_line1 character varying(60),
  address_line2 character varying(60),
  locality character varying(35),
  town_name character varying(30),
  postcode character varying(8),
  oa11cd character varying(9),
  lsoa11cd character varying(9),
  msoa11cd character varying(9),
  lad12cd character varying(9),
  region11cd character varying(9),
  eastings numeric(8,0),
  northings numeric(8,0),
  htc numeric(8,0),
  latitude double precision,
  longitude double precision,
  CONSTRAINT address_pkey PRIMARY KEY (uprn)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE address
  OWNER TO role_connect;
GRANT ALL ON TABLE address TO role_connect;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE address TO caseframesvc;

-- Index: caseframe.address_lad12cd_idx

-- DROP INDEX caseframe.address_lad12cd_idx;

CREATE INDEX address_lad12cd_idx
  ON address
  USING btree
  (lad12cd COLLATE pg_catalog."default");

-- Index: caseframe.address_msoa11cd_idx

-- DROP INDEX address_msoa11cd_idx;

CREATE INDEX address_msoa11cd_idx
  ON address
  USING btree
  (msoa11cd COLLATE pg_catalog."default");

