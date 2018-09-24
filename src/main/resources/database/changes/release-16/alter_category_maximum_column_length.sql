ALTER TABLE casesvc.category ALTER COLUMN categorypk TYPE character varying(60);
ALTER TABLE casesvc.category ALTER COLUMN longdescription TYPE character varying(100);
ALTER TABLE casesvc.caseevent ALTER COLUMN categoryfk TYPE character varying(60);