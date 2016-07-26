-- PLEASE NOTE - execute as a query, not pgScript --

-- Remove below for caseframe when everyone moved to casesvc schema 

DO $$
BEGIN
IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname='caseframesvc') THEN
   DROP OWNED BY caseframesvc;
END IF;
END$$;
DROP ROLE IF EXISTS caseframesvc;

DROP SCHEMA if exists caseframe cascade;

--Set up casesvc schema
DO $$
BEGIN
IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname='casesvc') THEN
  DROP OWNED BY casesvc;
END IF;
END$$;

DROP ROLE IF EXISTS casesvc;

CREATE USER casesvc LOGIN
  PASSWORD 'casesvc'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
GRANT role_connect TO casesvc;

DROP SCHEMA IF EXISTS casesvc cascade;
CREATE SCHEMA casesvc AUTHORIZATION role_connect;

REVOKE CONNECT ON DATABASE postgres FROM PUBLIC;
GRANT CONNECT
ON DATABASE postgres 
TO casesvc;

REVOKE ALL
ON ALL TABLES IN SCHEMA casesvc 
FROM PUBLIC;

REVOKE ALL
ON ALL SEQUENCES IN SCHEMA casesvc 
FROM PUBLIC;

ALTER DEFAULT PRIVILEGES 
    FOR USER postgres
    IN SCHEMA casesvc
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO casesvc;

ALTER DEFAULT PRIVILEGES 
    FOR USER postgres
    IN SCHEMA casesvc
    GRANT ALL ON SEQUENCES TO casesvc;

GRANT ALL PRIVILEGES ON DATABASE postgres to casesvc;
GRANT ALL ON SCHEMA casesvc TO casesvc;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA casesvc TO casesvc;

