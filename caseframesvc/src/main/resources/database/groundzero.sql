--ALTER USER caseframesvc SET search_path to 'caseframe,refdata';

DROP USER caseframesvc;
DROP OWNED BY caseframesvc;

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






drop table caseframe.address cascade;
drop table caseframe.action cascade;
drop table caseframe.actionplan cascade;
drop table caseframe.case cascade;
drop table caseframe.casetype cascade;
drop table caseframe.caseevent cascade;
drop table caseframe.questionnaire cascade;
drop table caseframe.questionset cascade;
drop table caseframe.survey cascade;
drop table caseframe.sample cascade;
drop table caseframe.databasechangelog cascade;
drop table caseframe.databasechangeloglock cascade;

