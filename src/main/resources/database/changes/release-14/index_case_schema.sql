
-- CASE IAC AUDIT SERVICE

-- caseiacaudit table

-- Index: casesvc.caseiacaudit_casefk_index
-- DROP INDEX casesvc.caseiacaudit_casefk_index;

CREATE INDEX caseiacaudit_casefk_index ON casesvc."caseiacaudit" USING btree (casefk);


-- Index: casesvc.caseiacaudit_iac_index
-- DROP INDEX casesvc.caseiacaudit_iac_index;

CREATE UNIQUE INDEX caseiacaudit_iac_index ON casesvc."caseiacaudit" USING btree (iac);
