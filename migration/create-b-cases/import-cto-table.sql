CREATE TABLE casesvc.temp_cto
(
  collectionexerciseid uuid NOT NULL,
  actionplanid uuid NOT null,
  CONSTRAINT temp_cto_pkey PRIMARY KEY (collectionexerciseid)
);

COPY casesvc.temp_cto(collectionexerciseid, actionplanid)
FROM STDIN WITH CSV;