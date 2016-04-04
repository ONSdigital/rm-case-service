set schema 'caseframe';
ALTER TABLE caseevent ADD CONSTRAINT caseevent_category_fkey FOREIGN KEY (category) REFERENCES caseframe."category" (name) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
