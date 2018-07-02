
-- CASE SERVICE

-- case table

-- Index: casesvc.case_casegroupfk_index
-- DROP INDEX casesvc.case_casegroupfk_index;

CREATE INDEX case_casegroupfk_index ON casesvc."case" USING btree (casegroupfk);


-- Index: casesvc.case_state_index
-- DROP INDEX casesvc.case_state_index;

CREATE INDEX case_state_index ON casesvc."case" USING btree (stateFK);


---------------------------------------------------------------------------
---------------------------------------------------------------------------

-- caseevent table

ALTER TABLE ONLY casesvc.caseevent ADD CONSTRAINT category_fkey FOREIGN KEY (categoryFK) REFERENCES casesvc.category(categoryPK);


-- Index: casesvc.caseevent_casefk_index
-- DROP INDEX casesvc.caseevent_casefk_index;

CREATE INDEX caseevent_casefk_index ON casesvc.caseevent USING btree (casefk);


-- Index: casesvc.caseevent_categoryfk_index
-- DROP INDEX casesvc.caseevent_categoryfk_index;

CREATE INDEX caseevent_categoryfk_index ON casesvc.caseevent USING btree (categoryfk);


---------------------------------------------------------------------------
---------------------------------------------------------------------------

-- response table

-- Index: casesvc.response_casefk_index
-- DROP INDEX casesvc.response_casefk_index;

CREATE INDEX response_casefk_index ON casesvc.response USING btree (casefk);







