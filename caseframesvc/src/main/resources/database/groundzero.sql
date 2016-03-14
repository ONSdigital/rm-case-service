--ALTER USER caseframesvc SET search_path to 'caseframe,refdata';

DROP OWNED BY caseframesvc;
DROP ROLE caseframesvc;

CREATE USER caseframesvc LOGIN
  PASSWORD 'caseframesvc'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
GRANT role_connect TO caseframesvc;


DROP SCHEMA caseframe cascade;
create schema CASEFRAME AUTHORIZATION role_connect;

REVOKE CONNECT ON DATABASE postgres FROM PUBLIC;
GRANT CONNECT
ON DATABASE postgres 
TO caseframesvc;

REVOKE ALL
ON ALL TABLES IN SCHEMA caseframe 
FROM PUBLIC;

REVOKE ALL
ON ALL SEQUENCES IN SCHEMA caseframe 
FROM PUBLIC;

ALTER DEFAULT PRIVILEGES 
    FOR USER postgres
    IN SCHEMA caseframe
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO caseframesvc;

ALTER DEFAULT PRIVILEGES 
    FOR USER postgres
    IN SCHEMA caseframe
    GRANT ALL ON SEQUENCES TO caseframesvc;

GRANT ALL PRIVILEGES ON DATABASE postgres to caseframesvc;
GRANT ALL ON SCHEMA caseframe TO caseframesvc;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA caseframe TO caseframesvc;

GRANT ALL ON SCHEMA refdata TO caseframesvc;
GRANT SELECT ON ALL TABLES IN SCHEMA refdata TO caseframesvc;



