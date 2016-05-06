--inserts new seed data into sample to use category from address table

set schema 'caseframe';

truncate sample cascade;

--sample

INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (1, 'Residential', 'Households', 'addresstype = ''HH''', 1, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (2, 'Hotels Guest Houses', 'Hotels Guest Houses Bed and Breakfasts', 'addresstype = ''CE'' and category = ''Hotel''', 2, 1);
INSERT INTO sample (sampleid, name, description, addresscriteria, casetypeid, surveyid) VALUES (3, 'Care Homes', 'Care Homes', 'addresstype = ''CE'' and category = ''Care home''', 3, 1);


