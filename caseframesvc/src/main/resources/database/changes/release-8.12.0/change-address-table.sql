--drop and recreate the address table to allow new columns for organisation_name and locality

set schema 'caseframe';

DELETE FROM questionnaire;
DELETE FROM "case";
DELETE FROM address;


DROP TABLE address CASCADE;

CREATE TABLE address
(
  uprn numeric(12,0) NOT NULL,
  addresstype character varying(6),
  estabtype character varying(6),
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
  longitude double precision
);

ALTER TABLE caseframe.address
  OWNER TO postgres;

ALTER TABLE ONLY caseframe.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (uprn);

CREATE INDEX address_lad12cd_idx ON caseframe.address USING btree (lad12cd);

CREATE INDEX address_msoa11cd_idx ON caseframe.address USING btree (msoa11cd);

--case table has foreign key constraint to address table

ALTER TABLE ONLY caseframe."case"
    ADD CONSTRAINT case_uprn_fkey FOREIGN KEY (uprn) REFERENCES caseframe.address(uprn);
